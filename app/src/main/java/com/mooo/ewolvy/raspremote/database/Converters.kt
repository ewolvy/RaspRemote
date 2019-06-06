package com.mooo.ewolvy.raspremote.database

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    companion object{
        @TypeConverter
        @JvmStatic
        fun fromUri(value: Uri): String{
            return value.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toUri(value: String): Uri{
            return Uri.parse(value)
        }
    }
}