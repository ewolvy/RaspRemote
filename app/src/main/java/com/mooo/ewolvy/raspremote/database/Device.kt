package com.mooo.ewolvy.raspremote.database

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "device_table")
data class Device (
    @PrimaryKey(autoGenerate = true) val _id: Int = 0,
    val name: String = "",
    val type: Int = 0,
    val server: String = "",
    val port: Int = 0,
    val username: String = "",
    val password: String = "",
    val alias: String = "",
    val certificateFile: Uri = Uri.EMPTY,
    var position: Int = 0,
    val currentState: String = "") : Parcelable{

    companion object{
        const val TYPE_AC_KAYSUN = 0
        const val TYPE_AC_PROKLIMA = 1
        const val TYPE_AC_GENERAL = 2
        const val TYPE_LAMP = 3
        const val TYPE_PLUG = 4
    }

    fun getFullAddress(): String {
        return "$server:$port/$alias/"
    }
}