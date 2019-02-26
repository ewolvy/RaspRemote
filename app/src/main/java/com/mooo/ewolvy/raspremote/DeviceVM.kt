package com.mooo.ewolvy.raspremote

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mooo.ewolvy.raspremote.database.Device
import com.mooo.ewolvy.raspremote.database.DeviceRepository
import com.mooo.ewolvy.raspremote.database.DeviceRoomDB
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

class DeviceVM (application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
    get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: DeviceRepository
    val allDevices: LiveData<List<Device>>

    init {
        val devicesDao = DeviceRoomDB.getDatabase(application, scope).deviceDao()
        repository = DeviceRepository(devicesDao)
        allDevices = repository.allDevices
    }

    fun insert(device: Device) = scope.launch(Dispatchers.IO) {
        repository.insert(device)
    }

    fun delete(device: Device) = scope.launch(Dispatchers.IO) {
        repository.delete(device)
    }

    fun move(fromPosition: Int, toPosition: Int) = scope.launch(Dispatchers.IO) {
        val fromDevice = allDevices.value?.get(fromPosition)
        val toDevice = allDevices.value?.get(toPosition)
        if (fromDevice != null && toDevice != null){
            fromDevice.position = toPosition
            toDevice.position = fromPosition
            repository.update (fromDevice)
            repository.update (toDevice)
            Log.d("*DEVICE VIEW MODEL*", "Database updated")
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}