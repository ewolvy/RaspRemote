package com.mooo.ewolvy.raspremote.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device_table ORDER BY position ASC")
    fun getAllDevices(): LiveData<List<Device>>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: Device)

    @Delete
    fun delete(device: Device)

    @Update
    fun update(device: Device)

    @Query("DELETE FROM device_table")
    fun deleteAll()
}