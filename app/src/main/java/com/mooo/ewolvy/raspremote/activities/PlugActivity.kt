package com.mooo.ewolvy.raspremote.activities

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
        textview_plug_name.text = device.name
    }

    private fun setupListeners(){
        button_plug_on.setOnClickListener { doActionForButton(it.id) }
        button_plug_off.setOnClickListener { doActionForButton(it.id) }
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
            R.id.button_plug_on ->  "ON"
            R.id.button_plug_off ->  "OFF"
            else -> "BUTTON_ID_ERROR"
        }

        CommandManager.sendCommand(
            device.getFullAddress(),
            device.username,
            device.password,
            device.certificateFile,
            command,
            this) { this.runOnUiThread {
                    Toast.makeText(this@PlugActivity, it, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun getDevice (extras: Bundle?): Device{
        return extras?.getParcelable("DEVICE") ?: Device()
    }
}
