package com.mooo.ewolvy.raspremote.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "device_table")
data class Device (
    @PrimaryKey(autoGenerate = true) val _id: Int,
    val name: String,
    val type: Int,
    val server: String,
    val port: Int,
    val username: String,
    val password: String,
    val alias: String,
    val certificateFile: String,
    var position: Int,
    val currentState: String) : Parcelable