package com.mooo.ewolvy.raspremote

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_main_item_about -> showAbout()
            R.id.menu_main_switch_air_conditioners,
            R.id.menu_main_switch_heaters,
            R.id.menu_main_switch_lamps -> toggleOption(item)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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

    private fun toggleOption(item: MenuItem){
        item.isChecked = !item.isChecked
    }
}
