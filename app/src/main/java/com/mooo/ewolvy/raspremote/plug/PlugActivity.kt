package com.mooo.ewolvy.raspremote.plug

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mooo.ewolvy.raspremote.CommandManager
import com.mooo.ewolvy.raspremote.R
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.activity_plug.*

class PlugActivity : AppCompatActivity() {

    val device: Device by lazy { getDevice(intent.extras) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plug)

        setupListeners()
    }

    private fun setupListeners(){
        button_plug_1_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_1_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_2_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_2_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_3_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_3_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_4_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_4_off.setOnClickListener { doActionForButton(it.id) }
        button_plug_all_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_all_off.setOnClickListener { doActionForButton(it.id) }
    }

    private fun doActionForButton (buttonId: Int){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // TODO: Explain why we need the read permission
            } else {
                // Ask for permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }

        val command = when (buttonId){
            R.id.button_plug_1_on ->  "1_ON"
            R.id.button_plug_1_off ->  "1_OFF"
            R.id.button_plug_2_on -> "2_ON"
            R.id.button_plug_2_off -> "2_OFF"
            R.id.button_plug_3_on -> "3_ON"
            R.id.button_plug_3_off -> "3_OFF"
            R.id.button_plug_4_on -> "4_ON"
            R.id.button_plug_4_off -> "4_OFF"
            R.id.button_plug_all_on -> "ALL_ON"
            R.id.button_plug_all_off -> "ALL_OFF"
            else -> "BUTTON_ID_ERROR"
        }

        CommandManager.sendCommand(
            device.getFullAddress(),
            //"https://ewolvy.mooo.com:1207/AAProKlima/",
            device.username,
            device.password,
            device.certificateFile,
            command,
            //"32_HOT_1",
            this) { this.runOnUiThread {
                    Toast.makeText(this@PlugActivity, it, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun getDevice (extras: Bundle?): Device{
        return extras?.getParcelable("DEVICE") ?: Device()
    }
}
