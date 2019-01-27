package com.mooo.ewolvy.raspremote

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AlertDialog
import android.widget.TextView



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareButtonListeners()
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

        button_about.setOnClickListener {
            showAbout()
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

    @SuppressLint("InflateParams")
    private fun showAbout(){
        // Inflate the about message contents
        val messageView = layoutInflater.inflate(R.layout.layout_about, null, false)

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        val textView = messageView.findViewById(R.id.tv_about_credits) as TextView
        val defaultColor = textView.textColors.defaultColor
        textView.setTextColor(defaultColor)

        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setTitle(R.string.app_name)
        builder.setView(messageView)
        builder.setPositiveButton("Ok", null)
        builder.create()
        builder.show()
    }
}
