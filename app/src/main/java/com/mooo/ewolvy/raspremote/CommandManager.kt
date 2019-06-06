package com.mooo.ewolvy.raspremote

import android.net.Uri
import kotlinx.coroutines.*

object CommandManager {
    fun sendCommand (address: String,
                     username: String,
                     password: String,
                     certificate: Uri,
                     command:String,
                     callback: (String) -> Unit){
        val fullAddress = "$address$command"
        GlobalScope.launch {
            val response = SSLConnection.connect(fullAddress, username, password, certificate)
            callback (response)
        }
    }
}