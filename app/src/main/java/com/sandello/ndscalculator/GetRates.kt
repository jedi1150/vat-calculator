package com.sandello.ndscalculator

import android.content.Context
import android.os.StrictMode
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

var listRates: List<Rate>? = null

class GetRates {
    suspend fun main(context: Context): String = withContext(Dispatchers.IO) {
        val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        var result: String? = null

        StrictMode.setThreadPolicy(policy)
        if (checkConnection(context)) {
            val url = URL("https://jsonbase.com/sandello/vat_rates")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 150
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
//                    val prefs = context.getSharedPreferences("val", Context.MODE_PRIVATE)
//                    val editor = prefs.edit()
                    val currentRate = if (Locale.getDefault().country != "")
                        db.rateDao().findByCountry(Locale.getDefault().country)!!
                    else
                        db.rateDao().findByCountry(Locale.getDefault().language)!!
                    result = currentRate.rate.toString()
//                    editor?.putString("rate", currentRate.rate.toString())
//                    editor?.apply()
                    stringBuilder.clear()
                    bufferedReader.close()
                    db.close()
                } else {
                    println("Error ${connection.responseCode}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection.disconnect()
            }
        }
        return@withContext result.toString()
    }
}