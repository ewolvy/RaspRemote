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
    val username: String = "",
    val password: String = "",
    val alias: String = "",
    val certificateFile: String = "",
    var position: Int = 0,
    val currentState: String = "") : Parcelable