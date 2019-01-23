package com.mooo.ewolvy.raspremote

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareButtonListeners();
    }

    private fun prepareButtonListeners(){
        button_air_conditioner.setOnClickListener {
            launchAirConditionerActivity()
        }

        button_wireless_plugs.setOnClickListener {
            launchWirelessPlugs()
        }

        button_settings.setOnClickListener {
            launchSettings()
        }
    }

    private fun launchAirConditionerActivity(){
        TODO("Will launch the activity for air conditioner control")
    }

    private fun launchWirelessPlugs(){
        TODO("Will launch the activity for wireless plugs")
    }

    private fun launchSettings(){
        TODO("Will launch the activity for settings")
    }
}
