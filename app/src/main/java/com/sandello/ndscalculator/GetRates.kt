package com.sandello.ndscalculator

import android.content.Context
import android.os.StrictMode
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
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
        var result: String? = ""

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
                    val json = Json { isLenient = true }
                    listRates = json.decodeFromString(ListSerializer(Rate.serializer()), data)
                    val db = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java, "rates"
                    ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                    db.rateDao().deleteAll()
                    db.rateDao().insertAllCountries(listRates!!)
                    val currentRate = if (Locale.getDefault().country != "")
                        db.rateDao().findByCountry(Locale.getDefault().country)!!
                    else
                        db.rateDao().findByCountry(Locale.getDefault().language)!!
                    result = currentRate.rate.toString()
                    stringBuilder.clear()
                    Log.d("dao", db.rateDao().findByCountry("ru").toString())
                    bufferedReader.close()
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