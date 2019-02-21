package com.mooo.ewolvy.raspremote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Database(entities = [Device::class], version = 1)
abstract class DeviceRoomDB: RoomDatabase() {
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: DeviceRoomDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DeviceRoomDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeviceRoomDB::class.java,
                    "Device_database"
                ).addCallback(DeviceDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class DeviceDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.deviceDao())
                }
            }
        }

        fun populateDatabase(deviceDao: DeviceDao) {
            deviceDao.deleteAll()

            var device = Device (0, "Aire", 0, "https://ewolvy.mooo.com", 1207, "juanjo", "m4ndr4k3", "AAKaysun", "/cert.pem", 0, "")
            deviceDao.insert(device)
            device = Device(0, "Lámpara", 2, "https://ewolvy.mooo.com", 2106, "juanjo", "m4ndr4k3", "Lamp", "/cert.pem", 1, "")
            deviceDao.insert(device)
            device = Device(0, "Estufa", 3, "https://ewolvy.mooo.com", 2106, "juanjo", "m4ndr4k3", "Estufa", "/cert.pem", 2, "")
            deviceDao.insert(device)
        }
    }
}