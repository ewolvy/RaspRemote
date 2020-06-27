package com.mooo.ewolvy.raspremote

import kotlinx.coroutines.*

object CommandManager {
    fun sendCommand (address: String,
                     username: String,
                     password: String,
                     command: String,
                     callback: (String) -> Unit){
        val fullAddress: String = if ("$address$command".last() == '/'){
            "$address$command".dropLast(1)
        } else {
            "$address$command"
        }
        GlobalScope.launch {
            val response = SSLConnection.easyConnect(fullAddress, username, password)
            callback (response)
        }
    }
}