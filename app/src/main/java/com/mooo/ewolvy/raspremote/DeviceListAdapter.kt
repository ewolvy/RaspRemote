package com.mooo.ewolvy.raspremote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mooo.ewolvy.raspremote.database.Device
import java.util.*

class DeviceListAdapter internal constructor(
    context: Context) : RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var devices = emptyList<Device>() // Cached copy of devices

    internal fun setDevices(devices: List<Device>) {
        if (this.devices != devices) {
            this.devices = devices
            notifyDataSetChanged()
        }
    }

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameItemView: TextView = itemView.findViewById(R.id.textview_item_name)
        val deviceLinkItemView: TextView = itemView.findViewById(R.id.textview_item_link)
        val deviceIconItemView: ImageView = itemView.findViewById(R.id.imageview_item_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView = inflater.inflate(R.layout.main_item, parent, false)
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val current = devices[position]
        holder.deviceNameItemView.text = current.name
        val linkText = "${current.server}:${current.port}/${current.alias}"
        holder.deviceLinkItemView.text = linkText
        holder.deviceIconItemView.setImageResource(when (current.type){
            0, 1 -> R.drawable.ic_air_conditioning
            2 -> R.drawable.ic_ceiling_lamp
            else -> R.drawable.ic_heater
        })
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
}