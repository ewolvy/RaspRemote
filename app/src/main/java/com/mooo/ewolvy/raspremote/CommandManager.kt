package com.mooo.ewolvy.raspremote

import kotlinx.coroutines.*

object CommandManager {
    fun sendCommand (address: String,
                     username: String,
                     password: String,
                     certificate: String,
                     command:String,
                     callback: (String) -> Unit){
        val fullAddress = "$address$command"
        GlobalScope.launch {
            val response = SSLConnection.connect(fullAddress, username, password, certificate)
            callback (response)
        }
    }
}