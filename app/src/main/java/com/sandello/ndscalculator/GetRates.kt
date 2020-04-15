package com.sandello.ndscalculator

import android.content.Context
import android.os.StrictMode
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.net.URL

var listRates: List<Rate>? = null

class GetRates {
    fun main(context: Context) {
        val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val url = URL("http://jedioleg.asuscomm.com:8000").readText()
        val json = Json(JsonConfiguration.Stable.copy(isLenient = true, ignoreUnknownKeys = true))
        listRates = json.parse(Rate.serializer().list, url)
        val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "rates"
        ).allowMainThreadQueries().build()
        GlobalScope.launch(Dispatchers.IO) {
            delay(1000L)
            db.rateDao().deleteAll()
            db.rateDao().insertAllCountries(listRates!!)
        }

//        Toast.makeText(context, "Rates updated", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context, arrayOf(listRates).size, Toast.LENGTH_LONG).show()
    }
}