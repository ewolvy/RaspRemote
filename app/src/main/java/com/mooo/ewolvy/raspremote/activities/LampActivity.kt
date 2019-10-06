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
import kotlinx.android.synthetic.main.activity_lamp.*

class LampActivity : AppCompatActivity() {

    val device: Device by lazy { getDevice(intent.extras) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lamp)

        title = "${this.getString(this.applicationInfo.labelRes)}: ${device.name}"
        setupListeners()
    }

    private fun setupListeners() {
        ib_lamp_power.setOnClickListener { doActionForButton(it.id) }
        ib_lamp_music.setOnClickListener { doActionForButton(it.id) }
        ib_lamp_night.setOnClickListener { doActionForButton(it.id) }

        ib_lamp_less_bright.setOnClickListener { doActionForButton(it.id) }
        ib_lamp_more_bright.setOnClickListener { doActionForButton(it.id) }

        ib_lamp_warmer.setOnClickListener { doActionForButton(it.id) }
        ib_lamp_cooler.setOnClickListener { doActionForButton(it.id) }

        ib_lamp_bright_25.setOnClickListener { doActionForButton(it.id) }
        ib_lamp_bright_50.setOnClickListener { doActionForButton(it.id) }
        ib_lamp_bright_75.setOnClickListener { doActionForButton(it.id) }
        ib_lamp_bright_100.setOnClickListener { doActionForButton(it.id) }
    }

    private fun doActionForButton (buttonId: Int) {
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
            R.id.ib_lamp_power -> "POWER"
            R.id.ib_lamp_music -> "BLUETOOTH"
            R.id.ib_lamp_night -> "NIGHT"

            R.id.ib_lamp_less_bright -> "BRIGHTMINUS"
            R.id.ib_lamp_more_bright -> "BRIGHTPLUS"

            R.id.ib_lamp_warmer -> "COLORMINUS"
            R.id.ib_lamp_cooler -> "COLORPLUS"

            R.id.ib_lamp_bright_25 -> "PERCENT25"
            R.id.ib_lamp_bright_50 -> "PERCENT50"
            R.id.ib_lamp_bright_75 -> "PERCENT75"
            R.id.ib_lamp_bright_100 -> "PERCENT100"

            else -> "BUTTON_ID_ERROR"
        }

        CommandManager.sendCommand(
            device.getFullAddress(),
            device.username,
            device.password,
            device.certificateFile,
            command,
            this) { this.runOnUiThread {
                Toast.makeText(this@LampActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getDevice (extras: Bundle?): Device {
        return extras?.getParcelable("DEVICE") ?: Device()
    }
}
