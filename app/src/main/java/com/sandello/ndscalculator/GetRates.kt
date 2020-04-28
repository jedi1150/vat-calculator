package com.sandello.ndscalculator

import android.content.Context
import android.os.StrictMode
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*


var listRates: List<Rate>? = null
var url: String? = null

class GetRates {
    fun main(context: Context) {
        val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        if (checkConnection(context)) {
            GlobalScope.launch(Dispatchers.IO) {
//                url = URL("http://jedioleg.asuscomm.com:8000").openConnection().getInputStream().bufferedReader().readText()
                val url2 = URL("http://jedioleg.asuscomm.com:8000")
                val con: HttpURLConnection = url2.openConnection() as HttpURLConnection
                con.connectTimeout = 500
                Log.d("rates", con.inputStream.bufferedReader().readText().toString())
                val json = Json(JsonConfiguration.Stable.copy(isLenient = true, ignoreUnknownKeys = true))
                listRates = json.parse(Rate.serializer().list, url!!)
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
            }
        }
    }
}