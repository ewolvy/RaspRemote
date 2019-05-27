package com.mooo.ewolvy.raspremote

import android.content.Context
import android.widget.Toast

object CommandManager {
    fun sendCommand (address: String,
                     username: String,
                     password: String,
                     certificate: String,
                     appContext: Context){
        // Make this call async!!!
        val response = SSLConnection.connect(address, username, password, certificate)
        Toast.makeText(appContext, response, Toast.LENGTH_LONG).show()
    }
}