package com.sandello.ndscalculator

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

var ratesReceived: Boolean = false

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale()
        setNightMode()
        setContentView(R.layout.activity_main)

        main_container.setOnApplyWindowInsetsListener { _, insets ->
            appBarLayout.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }
        navController = Navigation.findNavController(this, R.id.fragment)
        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.vatFragment) {
                toolbar.title = getString(R.string.vat).capitalize(Locale.ROOT)
            }
            if (destination.id == R.id.settingsFragment) {
                toolbar.title = getString(R.string.settings).capitalize(Locale.ROOT)
            }
        }

        toolbar.setupWithNavController(navController!!, AppBarConfiguration(navController!!.graph))

    }


    private fun setNightMode() {
        val isNightMode = this.resources.configuration.uiMode
                .and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        if (!isNightMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                xor View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                                xor View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        )
            }
        }
        val themePref = PreferenceManager.getDefaultSharedPreferences(this)
        if (themePref.getString("theme", "2") == "1") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }
        if (themePref.getString("theme", "2") == "0") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }

        if (themePref.getString("theme", "2") == "2") {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setLocale() {
        val localePref = PreferenceManager.getDefaultSharedPreferences(this)
        val language = localePref.getString("language", "en")
        if (language.toString() != "en") {
            Locale.setDefault(Locale.forLanguageTag(language.toString()))
            resources.configuration.setLocale(Locale.forLanguageTag(language.toString()))
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        }
    }
}