package com.mooo.ewolvy.raspremote.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class DeviceRepository (private val deviceDao: DeviceDao) {
    val allDevices: LiveData<List<Device>> = deviceDao.getAllDevices()

    @WorkerThread
    suspend fun insert(word: Device) {
        deviceDao.insert(word)
    }
}