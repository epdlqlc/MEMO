package com.example.davidpark.myapplication.utilities

import android.net.Uri
import android.util.Log
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.HashMap
import java.net.*
import javax.net.ssl.SSLSocketFactory

object NetworkUtils {

    private val TAG = NetworkUtils::class.java!!.getSimpleName()
    private val BASE_URL = "http://34.216.161.17/test/"

    fun buildUrl(filename: String, params: HashMap<String, String>): URL? {
        var startUri = Uri.parse(BASE_URL + filename).buildUpon()

        for((key, value) in params){
            startUri.appendQueryParameter(key, value)
        }

        val builtUri = startUri.build()

        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        Log.v(TAG, "Built URI " + url!!)

        return url
    }

    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL?): String? {
        val urlConnection = url?.openConnection() as HttpURLConnection
        try {
            val `in` = urlConnection.inputStream

            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            return if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    fun sendPostRequest(requestURL: String, postDataParams: HashMap<String, String>): String {

        val url: URL
        var response = ""
        try {
            url = URL(requestURL)

            val conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true


            val os = conn.outputStream
            val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8"))
            writer.write(getPostDataString(postDataParams))

            writer.flush()
            writer.close()
            os.close()
            val responseCode = conn.responseCode

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                response = br.readLine()
            } else {
                response = "Error Registering"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getPostDataString(params: HashMap<String, String>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params) {
            if (first)
                first = false
            else
                result.append("&")

            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }

        return result.toString()
    }
}
