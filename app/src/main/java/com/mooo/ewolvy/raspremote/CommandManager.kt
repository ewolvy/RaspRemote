package com.mooo.ewolvy.raspremote

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object CommandManager {
    fun sendCommand (address: String,
                     username: String,
                     password: String,
                     certificate: String,
                     command:String,
                     appContext: Context){
        val fullAddress = "$address$command"
        GlobalScope.launch(Dispatchers.Default, CoroutineStart.DEFAULT) {
            val response = SSLConnection.connect(fullAddress, username, password, certificate)
        }
    }
}