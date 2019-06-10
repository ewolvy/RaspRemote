package com.mooo.ewolvy.raspremote

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.cert.CertificateFactory

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object SSLConnection {
    // Constants
    private const val TAG = "SSLConnection"

    private fun setUpHttpsConnection(urlString: String,
                                     fileName: Uri,
                                     context: Context
    ): HttpsURLConnection? {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            val cf = CertificateFactory.getInstance("X.509")

            // Check file availability
            val state = Environment.getExternalStorageState()
            if (state == Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY) {
                // Use certificate from file
                val fis = context.contentResolver.openInputStream(fileName)
                val bis = BufferedInputStream(fis)

                if (bis.available() <= 0) {
                    Log.d(TAG, "setUpHttpsConnection: BufferedInputStream didn't work. File: $fileName")
                    bis.close()
                    fis?.close()
                    return null
                } else {
                    val ca = cf.generateCertificate(bis)

                    // Create a KeyStore containing our trusted CAs
                    val keyStoreType = KeyStore.getDefaultType()
                    val keyStore = KeyStore.getInstance(keyStoreType)
                    keyStore.load(null, null)
                    keyStore.setCertificateEntry("ca", ca)

                    // Create a TrustManager that trusts the CAs in our KeyStore
                    val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                    val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
                    tmf.init(keyStore)

                    // Create an SSLContext that uses our TrustManager
                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, tmf.trustManagers, null)

                    // Tell the URLConnection to use a SocketFactory from our SSLContext
                    val url = URL(urlString)
                    val urlConnection = url.openConnection() as HttpsURLConnection
                    urlConnection.sslSocketFactory = sslContext.socketFactory

                    Log.d(TAG, "setUpHttpsConnection: urlConnection created")
                    bis.close()
                    fis?.close()
                    return urlConnection
                }
            } else {
                Log.d(TAG, "setUpHttpsConnection: State was $state FileName: $fileName")
                return null
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Failed to establish SSL connection to server: $ex")
            return null
        }

    }

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

    fun connect(urlAddress: String, username: String, password: String, certificate: Uri, context: Context): String {
        val urlConnection = setUpHttpsConnection(urlAddress, certificate, context) ?: return "ERROR"
        var jsonResponse = ""

        try {
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            val userCredentials = "$username:$password"
            val basicAuth = "Basic " + Base64.encodeToString(userCredentials.toByteArray(), 0)
            urlConnection.setRequestProperty("Authorization", basicAuth)
            urlConnection.connect()
            if (urlConnection.responseCode == 200) {
                val inputStream = urlConnection.inputStream
                try {
                    jsonResponse = readFromStream(inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "IO Exception: $e")
        }
        Log.d(TAG, "Response: $jsonResponse")
        return jsonResponse
    }
}