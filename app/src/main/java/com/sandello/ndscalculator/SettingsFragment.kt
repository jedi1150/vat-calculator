package com.sandello.ndscalculator

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val themePref = findPreference("theme") as ListPreference?
        val translatePref = findPreference("translate") as Preference?
        val githubPref = findPreference("github") as Preference?
//        val ratesPref = findPreference("rates") as ListPreference?

        var themeEntries = arrayOf(getString(R.string.light), getString(R.string.dark), getString(R.string.battery_saver))
        val themeEntryValues = arrayOf("0", "1", "2")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            themePref?.setOnPreferenceChangeListener { _, newValue ->
                when (newValue) {
                    "0" -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    "1" -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    "2" -> setTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
                true
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            themeEntries = arrayOf(getString(R.string.light), getString(R.string.dark), getString(R.string.system_default))
            themePref?.setOnPreferenceChangeListener { _, newValue ->
                when (newValue.toString()) {
                    "0" -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    "1" -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    "2" -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                true
            }
        }
        themePref?.entries = themeEntries
        themePref?.entryValues = themeEntryValues
        if (themePref?.value == null)
            themePref?.setValueIndex(2)

//        ratesPref?.setOnPreferenceClickListener {
//            val db = Room.databaseBuilder(
//                    context!!,
//                    AppDatabase::class.java, "rates"
//            ).allowMainThreadQueries().build()
//            val newArray = emptyArray<String>()
//            Toast.makeText(context, listRates!![0].country, Toast.LENGTH_SHORT).show()
////            for (i in 0..listRates!!.size) {
////                newArray[i] = listRates!![i].country
////            }
//            ratesPref.entries = arrayOf("0", "1")
//            ratesPref.entryValues = arrayOf("0", "1")
//            true
//        }

        translatePref?.setOnPreferenceClickListener {
            val url = "https://lokalise.com/project/228402545e30480daadfd6.44294341/?view=multi&filter=platform_2"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context!!, Uri.parse(url))
            true
        }
        githubPref?.setOnPreferenceClickListener {
            val url = "https://github.com/jedi1150/VAT-Calculator"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context!!, Uri.parse(url))
            true
        }
    }

    private fun setTheme(themeMode: Int) {
        (activity as AppCompatActivity).delegate.localNightMode = themeMode
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}