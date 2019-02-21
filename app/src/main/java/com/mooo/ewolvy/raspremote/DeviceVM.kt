package com.mooo.ewolvy.raspremote

import android.app.Application
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
        val wordsDao = DeviceRoomDB.getDatabase(application, scope).deviceDao()
        repository = DeviceRepository(wordsDao)
        allDevices = repository.allDevices
    }

    fun insert(device: Device) = scope.launch(Dispatchers.IO) {
        repository.insert(device)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}