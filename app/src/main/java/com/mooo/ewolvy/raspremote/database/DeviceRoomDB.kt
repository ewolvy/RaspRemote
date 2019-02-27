package com.mooo.ewolvy.raspremote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Device::class], version = 1)
abstract class DeviceRoomDB: RoomDatabase() {
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: DeviceRoomDB? = null

        fun getDatabase(context: Context): DeviceRoomDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeviceRoomDB::class.java,
                    "Device_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}