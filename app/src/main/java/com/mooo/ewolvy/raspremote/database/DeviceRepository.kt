package com.mooo.ewolvy.raspremote.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class DeviceRepository (private val deviceDao: DeviceDao) {
    val allDevices: LiveData<List<Device>> = deviceDao.getAllDevices()

    @WorkerThread
    suspend fun insert(device: Device) {
        deviceDao.insert(device)
    }

    @WorkerThread
    suspend fun insertAt(device: Device, position: Int){
        val last = (allDevices.value?.size ?: 1) - 1
        for (x in position..last){
            allDevices.value?.get(x)?.position = x
        }
        device.position = position
        deviceDao.insert(device)
    }

    @WorkerThread
    suspend fun delete(device: Device) {
        val position = device.position
        val last = (allDevices.value?.size ?: 1) - 1
        if (position < last){
            for (x in position + 1..last){
                allDevices.value?.get(x)?.position = x - 1
            }
        }
        deviceDao.delete(device)
    }
}