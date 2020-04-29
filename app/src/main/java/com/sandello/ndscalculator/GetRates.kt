package com.sandello.ndscalculator

import android.content.Context
import android.os.StrictMode
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


var listRates: List<Rate>? = null

class GetRates {
    fun main(context: Context) {
        val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        if (checkConnection(context)) {
            GlobalScope.launch(Dispatchers.IO) {
                val url = URL("http://jedioleg.asuscomm.com:8000")
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 100
                try {

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val stream = BufferedInputStream(connection.inputStream)
                        val bufferedReader = BufferedReader(InputStreamReader(stream))
                        val stringBuilder = StringBuilder()
                        bufferedReader.forEachLine { stringBuilder.append(it) }
                        val data: String = stringBuilder.toString()
                        val json = Json(JsonConfiguration.Stable.copy(isLenient = true, ignoreUnknownKeys = true))
                        listRates = json.parse(Rate.serializer().list, data)
                        val db = Room.databaseBuilder(
                                context,
                                AppDatabase::class.java, "rates"
                        ).allowMainThreadQueries().build()
                        db.rateDao().deleteAll()
                        db.rateDao().insertAllCountries(listRates!!)
                        val prefs = context.getSharedPreferences("val", Context.MODE_PRIVATE)
                        if (prefs?.getString("rate", "") == "") {
                            val editor = prefs.edit()
                            editor?.putString("rate", db.rateDao().findByCountry(Locale.getDefault().country).rate.toString())
                            editor?.apply()
                        }
                    } else {
                        println("Error ${connection.responseCode}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection.disconnect()
                }
            }

        }
    }
}