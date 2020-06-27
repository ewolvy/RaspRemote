package com.mooo.ewolvy.raspremote.database

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
    val path: String = "",
    val username: String = "",
    val password: String = "",
    val params: String = "",
    var position: Int = 0,
    var currentState: String = "") : Parcelable{

    companion object{
        const val TYPE_AC_KAYSUN = 0
        const val TYPE_AC_PROKLIMA = 1
        const val TYPE_AC_GENERAL = 2
        const val TYPE_LAMP = 3
        const val TYPE_PLUG = 4
        const val TYPE_SENSOR = 5
    }

    fun getFullAddress(): String {
        return when (type) {
            TYPE_AC_GENERAL,
            TYPE_AC_KAYSUN,
            TYPE_AC_PROKLIMA -> "$server:$port/$path?device=$params&command="

            TYPE_PLUG,
            TYPE_LAMP -> "$server:$port/$path?command="

            else -> "$server:$port/$path"
        }
    }
}