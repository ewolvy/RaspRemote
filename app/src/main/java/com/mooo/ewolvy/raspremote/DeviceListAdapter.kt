package com.mooo.ewolvy.raspremote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mooo.ewolvy.raspremote.database.Device

class DeviceListAdapter internal constructor(
    context: Context) : RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var devices = emptyList<Device>() // Cached copy of devices

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

    internal fun setDevices(devices: List<Device>) {
        this.devices = devices
        notifyDataSetChanged()
    }

    override fun getItemCount() = devices.size
}