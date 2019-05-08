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
        deviceDao.delete(device)
    }

    @WorkerThread
    fun update(device: Device){
        deviceDao.update(device)
    }

    fun getDevices(): LiveData<List<Device>> {
        return allDevices
    }
}