package com.mooo.ewolvy.raspremote

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.activity_main.*

const val MAIN_PREFERENCES = "MainActivityPreferences"

class MainActivity : AppCompatActivity() {

    private lateinit var deviceVM: DeviceVM
    private lateinit var adapter: DeviceListAdapter
    private val observer = Observer<List<Device>?> {devices -> devices?.let {adapter.setDevices(it)}}
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = DeviceListAdapter(this)
        recview_main.adapter = adapter
        recview_main.layoutManager = LinearLayoutManager(this)

        deviceVM = ViewModelProviders.of(this).get(DeviceVM::class.java)
        deviceVM.allDevices.observe(this, observer)

        val touchHelperCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return onDeviceMoved(viewHolder.adapterPosition, target.adapterPosition)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                deviceVM.allDevices.observe(context, observer)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onDeviceSwiped(viewHolder.adapterPosition)
            }
        }
        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(recview_main)

        setupListeners()
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

    private fun setupListeners(){
        fab_main.setOnClickListener {
            val intent = Intent(this@MainActivity, EditItemActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditItemActivity.EDIT_PURPOSE, EditItemActivity.EDIT_FOR_NEW)
            intent.putExtras(extras)
            startActivityForResult(intent, EditItemActivity.EDIT_FOR_NEW)
        }
    }

    private fun onDeviceMoved(fromPosition: Int, toPosition:Int): Boolean{
        deviceVM.allDevices.removeObserver(observer)
        adapter.move(fromPosition, toPosition)
        deviceVM.move(fromPosition, toPosition)
        return true
    }

    private fun onDeviceSwiped(position: Int){
        deviceVM.delete(adapter.getDeviceAt(position))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EditItemActivity.EDIT_FOR_NEW ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this, "Recibido", Toast.LENGTH_LONG).show()
                        val extras = data?.extras
                        if (extras != null) {
                            val device: Device = extras.getParcelable(EditItemActivity.EXTRA_DEVICE) ?: return
                            device.position = deviceVM.allDevices.value?.size ?: 0
                            deviceVM.insert(device)
                        }
                    }
                    Activity.RESULT_CANCELED ->
                        Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
                }
        }
    }
}
