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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.main_item.view.*
import java.util.*

class DeviceListAdapter(private val context: Context) :
    ListAdapter<Device, DeviceListAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    companion object{
        class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.type == newItem.type &&
                        oldItem.server == newItem.server &&
                        oldItem.port == newItem.port &&
                        oldItem.username == newItem.username &&
                        oldItem.password == newItem.password &&
                        oldItem.alias == newItem.alias &&
                        oldItem.certificateFile == newItem.certificateFile &&
                        oldItem.position == newItem.position
            }
        }
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameItemView: TextView = itemView.textview_item_name
        val deviceLinkItemView: TextView = itemView.textview_item_link
        val devicePositionItemView: TextView = itemView.textview_item_position
        val deviceIconItemView: ImageView = itemView.imageview_item_icon
        val deviceEditItemView: ImageView = itemView.imageview_item_edit
        val deviceItemContainer: ConstraintLayout = itemView.item_container
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false))
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val current = getItem(position)
        val linkText = "${current.server}:${current.port}/${current.alias}"

        holder.deviceNameItemView.text = current.name
        holder.deviceLinkItemView.text = linkText
        holder.devicePositionItemView.text = current.position.toString()

        holder.deviceIconItemView.setImageResource(when (current.type){
            0, 1 -> R.drawable.ic_air_conditioning
            2 -> R.drawable.ic_ceiling_lamp
            else -> R.drawable.ic_heater
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
            Snackbar.make(it, "Prueba ${current.position}", Snackbar.LENGTH_LONG).show()
            //TODO: launch activity depending on device type
        }
    }

    /*fun moveDevices (fromPosition: Int, toPosition: Int){
        getItem(fromPosition).position = toPosition
        getItem(toPosition).position = fromPosition
        Collections.swap(devices, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }*/

    fun getDeviceAt (position: Int): Device{
        return getItem(position)
    }
}