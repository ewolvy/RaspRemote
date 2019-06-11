package com.mooo.ewolvy.raspremote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.mooo.ewolvy.raspremote.activities.EditItemActivity
import com.mooo.ewolvy.raspremote.database.Device
import com.mooo.ewolvy.raspremote.activities.PlugActivity
import kotlinx.android.synthetic.main.main_item.view.*
import java.util.*

class DeviceListAdapter internal constructor(
    private val context: Context) : RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var devices = emptyList<Device>() // Cached copy of devices

    internal fun setDevices(devices: List<Device>) {
        if (this.devices != devices) {
            this.devices = devices
            notifyDataSetChanged()
        }
    }

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        val deviceNameItemView: TextView = itemView.findViewById(R.id.textview_item_name)
        val deviceLinkItemView: TextView = itemView.findViewById(R.id.textview_item_link)
        val deviceIconItemView: ImageView = itemView.findViewById(R.id.imageview_item_icon)
        val deviceEditItemView: ImageView = itemView.findViewById(R.id.imageview_item_edit)
        val deviceItemContainer: ConstraintLayout = itemView.findViewById(R.id.item_container)

        override fun onItemSelected(backgroundColor: Int) {
            itemView.item_container.setBackgroundColor(backgroundColor)
        }

        override fun onItemClear(backgroundColor: Int) {
            itemView.item_container.setBackgroundColor(backgroundColor)
        }

        override fun onDeviceSwiped(position: Int, viewModel: DeviceVM) {
            viewModel.delete(getDeviceAt(position))
            deleteDevice(position)
            notifyItemRemoved(position)

        }

        override fun onDeviceMoved(from: Int, to: Int, viewModel: DeviceVM): Boolean {
            moveDevices(from, to)
            viewModel.updateDevice(getDeviceAt(from))
            viewModel.updateDevice(getDeviceAt(to))
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView = inflater.inflate(R.layout.main_item, parent, false)
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val current = devices[position]
        val linkText = "${current.server}:${current.port}/${current.alias}"

        holder.deviceNameItemView.text = current.name
        holder.deviceLinkItemView.text = linkText

        holder.deviceIconItemView.setImageResource(when (current.type){
            Device.TYPE_AC_KAYSUN, Device.TYPE_AC_PROKLIMA -> R.drawable.ic_air_conditioning
            Device.TYPE_LAMP -> R.drawable.ic_ceiling_lamp
            Device.TYPE_PLUG -> R.drawable.ic_wireless_plug
            else -> R.drawable.ic_question_mark
        })

        holder.deviceEditItemView.setOnClickListener {
            val intent = Intent(context, EditItemActivity::class.java)
            val extras = Bundle()
            extras.putInt(EditItemActivity.EDIT_PURPOSE, EditItemActivity.EDIT_FOR_EDIT)
            extras.putParcelable(EditItemActivity.EXTRA_DEVICE, current)
            intent.putExtras(extras)
            startActivityForResult(context as Activity, intent, EditItemActivity.EDIT_FOR_EDIT, null)
        }

        holder.deviceItemContainer.setOnClickListener{
            when (current.type){
                Device.TYPE_PLUG -> {
                    val intent = Intent(context, PlugActivity::class.java)
                    val extras = Bundle()
                    extras.putParcelable("DEVICE", current)
                    intent.putExtras(extras)
                    startActivity(context as Activity, intent, null)
                }
            }
        }
    }

    fun moveDevices (fromPosition: Int, toPosition: Int){
        devices[fromPosition].position = toPosition
        devices[toPosition].position = fromPosition
        Collections.swap(devices, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getDeviceAt (position: Int): Device{
        return devices[position]
    }

    override fun getItemCount() = devices.size

    fun deleteDevice(position: Int) {
        notifyItemRemoved(position)
    }
}