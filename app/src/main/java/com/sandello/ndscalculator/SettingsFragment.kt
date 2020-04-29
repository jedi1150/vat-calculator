package com.sandello.ndscalculator

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.room.Room
import java.util.*

@ExperimentalStdlibApi
@Suppress("DEPRECATION")
class SettingsFragment : PreferenceFragmentCompat() {

    private var themeEntries = emptyArray<String>()
    private val themeEntryValues = arrayOf("0", "1", "2")
    private val rateEntries = mutableListOf<String>()
    private val rateEntryValues = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val themePref = findPreference("theme") as ListPreference?
        val languagePref = findPreference("language") as ListPreference?
        val ratesPref = findPreference("rate") as ListPreference?
        val translatePref = findPreference("translate") as Preference?
        val githubPref = findPreference("github") as Preference?

        fun themeSummary(newValue: String) {
            themePref?.summary = when (newValue) {
                "0" -> themeEntries[0]
                "1" -> themeEntries[1]
                "2" -> themeEntries[2]
                else -> themeEntries[2]
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            themeEntries = arrayOf(getString(R.string.light), getString(R.string.dark), getString(R.string.battery_saver))
            themePref?.setOnPreferenceChangeListener { _, newValue ->
                when (newValue) {
                    "0" -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    "1" -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    "2" -> setTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
                themeSummary(newValue.toString())
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
                themeSummary(newValue.toString())
                true
            }
        }
        themePref?.entries = themeEntries
        themePref?.entryValues = themeEntryValues
        if (themePref?.value == null)
            themePref?.setValueIndex(2)

        themeSummary(themePref?.value.toString())


        val availableLanguageISO = Locale.getISOLanguages().filter { it in resources.getStringArray(R.array.languages_code) }
        val availableLanguages = mutableListOf<String>()
        for (item in availableLanguageISO) {
            availableLanguages.add(Locale.forLanguageTag(item).getDisplayLanguage(Locale.forLanguageTag(item)).capitalize(Locale.ROOT))
        }
        languagePref?.entries = availableLanguages.toTypedArray()
        languagePref?.entryValues = availableLanguageISO.toTypedArray()


        fun languageSummary(newValue: String) {
            languagePref?.summary = Locale.forLanguageTag(newValue).displayLanguage.capitalize(Locale.ROOT)
        }
        if (languagePref?.value != null)
            languageSummary(languagePref.value!!)
        else {
            languagePref?.setValueIndex(languagePref.entryValues.indexOf(Locale.getDefault().language))
            languagePref?.summary = Locale.getDefault().displayLanguage.capitalize(Locale.ROOT)
        }

        languagePref?.setOnPreferenceChangeListener { _, newValue ->
            Locale.setDefault(Locale.forLanguageTag(newValue.toString()))
            resources.configuration.setLocale(Locale.forLanguageTag(newValue.toString()))
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
            findNavController().navigate(R.id.action_settingsFragment_self)
            true
        }


        val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "rates"
        ).allowMainThreadQueries().build()
        ratesPref?.isVisible = db.rateDao().getAll().isNotEmpty()
        for (element in db.rateDao().getAll()) {
            rateEntryValues.add(element.code)
            rateEntries.add(getString(R.string.rate_string, Locale("", element.code).displayCountry, element.rate) + "%")
        }
        ratesPref?.entries = rateEntries.toTypedArray()
        ratesPref?.entryValues = rateEntryValues.toTypedArray()

        fun rateSummary(newValue: String) {
            val data = db.rateDao().findByCountry(newValue)
            ratesPref?.setSummaryProvider {
                getString(R.string.rate_string, Locale("", data.code).displayCountry, data.rate) + "%"
            }
            val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
            val editor = prefs?.edit()
            editor?.putString("rate", data.rate.toString())
            editor?.apply()
        }

        if (ratesPref?.value != null)
            rateSummary(ratesPref.value!!)
        else {
            if (db.rateDao().getAll().isNotEmpty()) {
                val data = db.rateDao().findByCountry(Locale.getDefault().country)
                ratesPref?.setValueIndex(rateEntryValues.indexOf(data.code))
                rateSummary(ratesPref?.value!!)
            }
        }

        ratesPref?.setOnPreferenceChangeListener { _, newValue ->
            rateSummary(newValue.toString())
            true
        }

        translatePref?.setOnPreferenceClickListener {
            val url = "https://lokalise.com/project/228402545e30480daadfd6.44294341/?view=multi&filter=platform_2"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
            true
        }
        githubPref?.setOnPreferenceClickListener {
            val url = "https://github.com/jedi1150/VAT-Calculator"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
            true
        }
    }


    private fun setTheme(themeMode: Int) {
        (activity as AppCompatActivity).delegate.localNightMode = themeMode
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

}