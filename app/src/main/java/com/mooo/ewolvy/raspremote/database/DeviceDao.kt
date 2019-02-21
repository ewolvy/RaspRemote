package com.mooo.ewolvy.raspremote.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device_table ORDER BY position ASC")
    fun getAllDevices(): LiveData<List<Device>>

    @Insert
    fun insert(device: Device)

    @Delete
    fun delete(device: Device)

    @Query("DELETE FROM device_table")
    fun deleteAll()
}