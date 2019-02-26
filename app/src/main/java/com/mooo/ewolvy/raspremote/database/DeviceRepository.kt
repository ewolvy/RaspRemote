package com.mooo.ewolvy.raspremote.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class DeviceRepository (private val deviceDao: DeviceDao) {
    val allDevices: LiveData<List<Device>> = deviceDao.getAllDevices()

    @WorkerThread
    fun insert(device: Device) {
        deviceDao.insert(device)
    }

    @WorkerThread
    fun delete(device: Device) {
        val position = device.position
        val last = (allDevices.value?.size ?: 1) - 1
        if (position < last){
            for (x in position + 1..last){
                allDevices.value?.get(x)?.position = x - 1
            }
        }
        deviceDao.delete(device)
    }

    @WorkerThread
    fun update(device: Device){
        deviceDao.update(device)
    }
}