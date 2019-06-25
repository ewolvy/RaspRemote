package com.mooo.ewolvy.raspremote.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mooo.ewolvy.raspremote.ac.ACStatus
import com.mooo.ewolvy.raspremote.R
import com.mooo.ewolvy.raspremote.ac.ACKaysun
import com.mooo.ewolvy.raspremote.ac.ACProKlima
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.ac_buttons.*
import kotlinx.android.synthetic.main.ac_display.*

class ACActivity : AppCompatActivity() {

    private val device: Device by lazy { getDevice(intent.extras) }
    private val status: ACStatus by lazy { getStatus(device) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ac)

        setupListeners()
        updateUI()
    }

    private fun getDevice (extras: Bundle?): Device{
        return extras?.getParcelable("DEVICE") ?: Device()
    }

    private fun getStatus (device: Device): ACStatus{
        return when (device.type){
            Device.TYPE_AC_KAYSUN -> ACKaysun(device.currentState)
            Device.TYPE_AC_PROKLIMA -> ACProKlima(device.currentState)
            else -> ACKaysun(device.currentState)
        }
    }

    private fun setupListeners(){
        button_ac_mode.setOnClickListener { doActionForButton(it.id) }
        button_ac_temp_minus.setOnClickListener { doActionForButton(it.id) }
        button_ac_temp_plus.setOnClickListener { doActionForButton(it.id) }
        button_ac_fan.setOnClickListener { doActionForButton(it.id) }

        button_ac_send.setOnClickListener { doActionForButton(it.id) }
        button_ac_on.setOnClickListener { doActionForButton(it.id) }
        button_ac_off.setOnClickListener { doActionForButton(it.id) }
        button_ac_swing.setOnClickListener { doActionForButton(it.id) }
    }

    private fun doActionForButton(button: Int){
        when (button){
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

            /*
            R.id.button_ac_send ->
            R.id.button_ac_on ->
            R.id.button_ac_off ->
            R.id.button_ac_swing ->
             */
        }
    }

    private fun updateUI(){
        updateTemp()
        updateMode()
        updateFan()
    }

    private fun updateTemp(){
        textview_ac_temperature.text = status.temp.toString()
    }

    private fun updateMode(){
        textview_ac_auto_mode.visibility = View.INVISIBLE
        textview_ac_cool_mode.visibility = View.INVISIBLE
        textview_ac_dry_mode.visibility = View.INVISIBLE
        textview_ac_heat_mode.visibility = View.INVISIBLE
        textview_ac_fan_mode.visibility = View.INVISIBLE
        when (status.mode){
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
        when (status.fan){
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
        }
    }
}
