package com.mooo.ewolvy.raspremote

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

const val MAIN_PREFERENCES = "MainActivityPreferences"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setButtonListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) loadMenuPreferences(menu)
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

    private fun setButtonListeners(){
        fab_main.setOnClickListener {
            val intent = Intent(this@MainActivity, EditItemActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditItemActivity.EDIT_PURPOSE, EditItemActivity.EDIT_FOR_NEW)
            intent.putExtras(extras)
            startActivity(intent)
        }
    }

    @SuppressLint("InflateParams") // One of the right uses of null on inflate method is for an AlertDialog
    private fun showAbout(){
        // Inflate the about message contents
        // Usually, the second parameter should not be null, but there are some exceptions,
        // as when it is used on an AlertDialog.
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

    private fun loadMenuPreferences(menu: Menu){
        val preferences = getSharedPreferences(MAIN_PREFERENCES, 0)
        menu.findItem(R.id.menu_main_switch_air_conditioners).isChecked = preferences.getBoolean(R.id.menu_main_switch_air_conditioners.toString(), true)
        menu.findItem(R.id.menu_main_switch_heaters).isChecked = preferences.getBoolean(R.id.menu_main_switch_heaters.toString(), true)
        menu.findItem(R.id.menu_main_switch_lamps).isChecked = preferences.getBoolean(R.id.menu_main_switch_lamps.toString(), true)
    }

    private fun toggleOption(item: MenuItem){
        item.isChecked = !item.isChecked
        savePreference (item.itemId, item.isChecked)
        //TODO Change the data displayed
    }

    private fun savePreference(itemId: Int, isChecked: Boolean){
        val sharedPreferences = getSharedPreferences(MAIN_PREFERENCES, 0)
        val preferencesEditor = sharedPreferences.edit()
        preferencesEditor.putBoolean(itemId.toString(), isChecked)
        preferencesEditor.apply()
    }
}
