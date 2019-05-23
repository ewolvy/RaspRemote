package com.mooo.ewolvy.raspremote

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
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

class MainActivity : AppCompatActivity() {

    private lateinit var deviceVM: DeviceVM
    private lateinit var adapter: DeviceListAdapter

    // Override standard functions of the Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewSetup()
        listenersSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_main_item_about -> showAbout()
            R.id.menu_main_add_fake_data -> addFakeData()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EditItemActivity.EDIT_FOR_NEW ->
                when (resultCode) {
                    Activity.RESULT_OK -> { // TODO: Change the text displayed
                        data?.extras?.let {
                            val device: Device = it.getParcelable(EditItemActivity.EXTRA_DEVICE) ?: return
                            device.position = deviceVM.allDevices.value?.size ?: 0
                            deviceVM.insert(device)
                            Toast.makeText(this, "Recibido nuevo", Toast.LENGTH_LONG).show()
                        }
                    }
                    Activity.RESULT_CANCELED -> // TODO: Change the text displayed
                        Toast.makeText(this, "Cancelado nuevo", Toast.LENGTH_LONG).show()
                }
            EditItemActivity.EDIT_FOR_EDIT ->
                when (resultCode) {
                    Activity.RESULT_OK -> { // TODO: Change the text displayed
                        data?.extras?.let{
                            val device: Device = it.getParcelable(EditItemActivity.EXTRA_DEVICE) ?: return
                            deviceVM.updateDevice(device)
                            Toast.makeText(this, "Recibida edición", Toast.LENGTH_LONG).show()
                        }
                    }
                    Activity.RESULT_CANCELED -> { // TODO: Change the text displayed
                        Toast.makeText(this, "Cancelada edición", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // Helper functions for readability
    private fun recyclerViewSetup(){
        // Create the adapter and assign it to the RecyclerView
        adapter = DeviceListAdapter(this)
        recview_main.adapter = adapter
        recview_main.layoutManager = LinearLayoutManager(this)

        // Create the ViewModel and set the observer to notify the adapter when devices are changed
        deviceVM = ViewModelProviders.of(this).get(DeviceVM::class.java)
        deviceVM.allDevices.observe(this,
            Observer<List<Device>?> {devices -> devices?.let {adapter.setDevices(it)}})

        // Create the Helper Class to manage swipes and moves of devices
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

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onDeviceSwiped(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // Progressive faint on delete gesture
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val width = viewHolder.itemView.width.toFloat()
                    val alpha = 1.0f - Math.abs(dX) / width
                    viewHolder.itemView.alpha = alpha
                    viewHolder.itemView.translationX = dX
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (viewHolder is ItemTouchHelperViewHolder) {
                    val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
                    itemViewHolder.onItemClear(resources.getColor(R.color.colorPrimaryLight, theme))
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                // We only want the active item
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    if (viewHolder is ItemTouchHelperViewHolder) {
                        val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
                        itemViewHolder.onItemSelected(resources.getColor(R.color.colorPrimary, theme))
                    }
                }

                super.onSelectedChanged(viewHolder, actionState)
            }
        }
        // Assign the helper to the RecyclerView
        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(recview_main)
    }

    private fun listenersSetup(){
        fab_main.setOnClickListener {
            val intent = Intent(this@MainActivity, EditItemActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditItemActivity.EDIT_PURPOSE, EditItemActivity.EDIT_FOR_NEW)
            intent.putExtras(extras)
            startActivityForResult(intent, EditItemActivity.EDIT_FOR_NEW)
        }
    }

    private fun onDeviceMoved(fromPosition: Int, toPosition:Int): Boolean{
        adapter.moveDevices(fromPosition, toPosition)
        deviceVM.updateDevice(adapter.getDeviceAt(fromPosition))
        deviceVM.updateDevice(adapter.getDeviceAt(toPosition))
        return true
    }

    private fun onDeviceSwiped(position: Int){
        deviceVM.delete(adapter.getDeviceAt(position))
        adapter.deleteDevice(position)
        adapter.notifyItemRemoved(position)
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
        with(builder) {
            setIcon(R.mipmap.ic_launcher)
            setTitle(R.string.app_name)
            setView(messageView)
            setPositiveButton(R.string.dialog_ok, null)
            create()
            show()
        }
    }

    private fun addFakeData(){ //TODO: Remove this function before final version
        var device = Device (
            0,
            "Aire",
            0,
            "https://ewolvy.mooo.com",
            1207,
            "juanjo",
            "m4ndr4k3",
            "AAKaysun",
            "/cert.pem",
            0,
            ""
        )
        device.position = deviceVM.allDevices.value?.size ?: 0
        deviceVM.insert(device)
        Log.d("MAIN", "Insertado ${device.name} en posición: ${device.position}")

        device = Device(
            0,
            "Lámpara",
            2,
            "https://ewolvy.mooo.com",
            2106,
            "juanjo",
            "m4ndr4k3",
            "Lamp",
            "/cert.pem",
            1,
            ""
        )
        device.position = (deviceVM.allDevices.value?.size ?: 0) + 1
        deviceVM.insert(device)
        Log.d("MAIN", "Insertado ${device.name} en posición: ${device.position}")

        device = Device(
            0,
            "Estufa",
            3,
            "https://ewolvy.mooo.com",
            2106,
            "juanjo",
            "m4ndr4k3",
            "Estufa",
            "/cert.pem",
            2,
            ""
        )
        device.position = (deviceVM.allDevices.value?.size ?: 0) + 2
        deviceVM.insert(device)
        Log.d("MAIN", "Insertado ${device.name} en posición: ${device.position}")
    }
}
