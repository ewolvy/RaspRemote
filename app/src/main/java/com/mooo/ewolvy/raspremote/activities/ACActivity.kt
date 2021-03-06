package com.mooo.ewolvy.raspremote.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.mooo.ewolvy.raspremote.CommandManager
import com.mooo.ewolvy.raspremote.DeviceVM
import com.mooo.ewolvy.raspremote.ac.ACStatus
import com.mooo.ewolvy.raspremote.R
import com.mooo.ewolvy.raspremote.ac.ACGeneral
import com.mooo.ewolvy.raspremote.ac.ACKaysun
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.ac_buttons.*
import kotlinx.android.synthetic.main.ac_display.*

class ACActivity : AppCompatActivity() {

    private val device: Device by lazy { getDevice(intent.extras) }
    private val status: ACStatus by lazy { getStatus(device) }

    private lateinit var deviceVM: DeviceVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ac)

        deviceVM = ViewModelProviders.of(this).get(DeviceVM::class.java)
        title = "${this.getString(this.applicationInfo.labelRes)}: ${device.name}"
        setupListeners()
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        if (device.currentState != status.toString()) {
            device.currentState = status.toString()
            deviceVM.updateDevice(device)
        }
    }

    private fun getDevice (extras: Bundle?): Device{
        return extras?.getParcelable("DEVICE") ?: Device()
    }

    private fun getStatus (device: Device): ACStatus{
        return when (device.type){
            Device.TYPE_AC_KAYSUN -> ACKaysun(device.currentState)
            //Device.TYPE_AC_PROKLIMA -> ACProKlima(device.currentState)
            Device.TYPE_AC_GENERAL -> ACGeneral(device.currentState)
            else -> ACKaysun(device.currentState)
        }
    }

    private fun setupListeners(){
        button_ac_mode.setOnClickListener { doActionForButton(it.id) }
        button_ac_temp_minus.setOnClickListener { doActionForButton(it.id) }
        button_ac_temp_plus.setOnClickListener { doActionForButton(it.id) }
        button_ac_fan.setOnClickListener { doActionForButton(it.id) }

        button_ac_send.setOnClickListener { sendCommandForButton(it.id) }
        button_ac_on.setOnClickListener { sendCommandForButton(it.id) }
        button_ac_off.setOnClickListener { sendCommandForButton(it.id) }
        button_ac_swing.setOnClickListener { sendCommandForButton(it.id) }
    }

    private fun doActionForButton(buttonId: Int){
        when (buttonId){
            R.id.button_ac_mode -> {
                status.setNextMode()
                updateUI()
            }
            R.id.button_ac_fan -> {
                status.setNextFan()
                updateUI()
            }

            R.id.button_ac_temp_plus -> {
                status.plusTemp()
                updateTemp()
            }
            R.id.button_ac_temp_minus -> {
                status.minusTemp()
                updateTemp()
            }
        }
    }

    private fun sendCommandForButton(buttonId: Int){
        val command = when (buttonId){
            R.id.button_ac_send -> status.getCode()
            R.id.button_ac_on -> status.getPowerOn()
            R.id.button_ac_off -> status.getPowerOff()
            R.id.button_ac_swing -> status.getSwing()

            else -> "BUTTON_ID_ERROR"
        }

        if (buttonId == R.id.button_ac_swing) updateSwing()

        CommandManager.sendCommand(
            device.getFullAddress(),
            device.username,
            device.password,
            command) { this.runOnUiThread {
                val toastMessage = if (it[it.lastIndex - 3] == '0'){
                    getString(R.string.response_ok)
                } else {
                    getString(R.string.response_fail)
                }
                val toast: Toast = Toast.makeText(this@ACActivity, toastMessage, Toast.LENGTH_LONG)
                val toastLayout = toast.view as LinearLayout
                val toastTV = toastLayout.getChildAt(0) as TextView
                toastTV.textSize = 40f
                toast.show()
            }
        }

    }

    private fun updateUI(){
        updateTemp()
        updateMode()
        updateFan()
        updateSwing()
    }

    private fun updateTemp(){
        if (status.isTempActive()) {
            textview_ac_temperature.text = status.getTemp().toString()
        } else {
            textview_ac_temperature.text = getString(R.string.ac_inactive_temp)
        }
    }

    private fun updateMode(){
        textview_ac_auto_mode.visibility = View.INVISIBLE
        textview_ac_cool_mode.visibility = View.INVISIBLE
        textview_ac_dry_mode.visibility = View.INVISIBLE
        textview_ac_heat_mode.visibility = View.INVISIBLE
        textview_ac_fan_mode.visibility = View.INVISIBLE
        when (status.getMode()){
            ACStatus.MODE_AUTO -> textview_ac_auto_mode.visibility = View.VISIBLE
            ACStatus.MODE_COOL -> textview_ac_cool_mode.visibility = View.VISIBLE
            ACStatus.MODE_DRY -> textview_ac_dry_mode.visibility = View.VISIBLE
            ACStatus.MODE_HEAT -> textview_ac_heat_mode.visibility = View.VISIBLE
            ACStatus.MODE_FAN -> textview_ac_fan_mode.visibility = View.VISIBLE
        }
    }

    private fun updateFan(){
        textview_ac_fan_level_1.visibility = View.INVISIBLE
        textview_ac_fan_level_2.visibility = View.INVISIBLE
        textview_ac_fan_level_3.visibility = View.INVISIBLE
        textview_ac_fan_level_auto.visibility = View.INVISIBLE
        textview_ac_fan_level_quiet.visibility = View.INVISIBLE
        when (status.getFan()){
            ACStatus.FAN_AUTO -> textview_ac_fan_level_auto.visibility = View.VISIBLE
            ACStatus.FAN_1 -> textview_ac_fan_level_1.visibility = View.VISIBLE
            ACStatus.FAN_2 -> {
                textview_ac_fan_level_1.visibility = View.VISIBLE
                textview_ac_fan_level_2.visibility = View.VISIBLE
            }
            ACStatus.FAN_3 -> {
                textview_ac_fan_level_1.visibility = View.VISIBLE
                textview_ac_fan_level_2.visibility = View.VISIBLE
                textview_ac_fan_level_3.visibility = View.VISIBLE
            }
            ACStatus.FAN_QUIET -> {
                if (status is ACGeneral) textview_ac_fan_level_quiet.visibility = View.VISIBLE
            }
        }
    }

    private fun updateSwing(){
        iv_ac_swing_icon.visibility = if (status.isSwingActive()) View.VISIBLE else View.INVISIBLE
    }
}
