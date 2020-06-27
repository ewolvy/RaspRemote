package com.mooo.ewolvy.raspremote

import android.util.Base64
import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

import javax.net.ssl.HttpsURLConnection

object SSLConnection {
    // Constants
    private const val TAG = "SSLConnection"

    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    fun easyConnect(urlAddress: String, username: String, password: String): String {
        val jsonResponse: String
        val urlConnection = URL(urlAddress).openConnection() as HttpsURLConnection
        val userCredentials = "$username:$password"
        val basicAuth = "Basic " + Base64.encodeToString(userCredentials.toByteArray(), 0)
        try {
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.addRequestProperty("Authorization", basicAuth)
            urlConnection.connect()
            if (urlConnection.responseCode == 200) {
                val inputStream = urlConnection.inputStream
                try {
                    jsonResponse = readFromStream(inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "IO Exception reading from stream: $e")
                    return e.message ?: "ERROR"
                }
            } else {
                return urlConnection.responseMessage
            }
        } catch (e: IOException) {
            Log.e(TAG, "IO Exception connection to URL: $e")
            return e.message ?: "ERROR"
        }
        Log.d(TAG, "Response: $jsonResponse")

        return jsonResponse

    }
}