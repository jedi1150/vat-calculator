package com.sandello.ndscalculator

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_fragment.*
import kotlinx.android.synthetic.main.fragment_vat.*

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GetRates().main(this)
        setNightMode()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.fragment)
        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label.toString() == "VAT") {
                toolbar.visibility = View.GONE
                bottom_navigation.visibility = View.VISIBLE
            }
            if (destination.label.toString() == "Settings") {
                toolbar.visibility = View.VISIBLE
                bottom_navigation.visibility = View.GONE
            }
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        main_container.setOnApplyWindowInsetsListener { v, insets ->
            toolbar.updatePadding(top = insets.systemWindowInsetTop, right = insets.systemWindowInsetRight, left = insets.systemWindowInsetLeft)
            bottom_navigation.updatePadding(bottom = insets.systemWindowInsetBottom, right = insets.systemWindowInsetRight, left = insets.systemWindowInsetLeft)
            insets
        }

        settingsButton.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(vat_layout.windowToken, 0)
            navController!!.navigate(R.id.action_vatFragment_to_settingsFragment)
        }
    }

//    override fun onSupportNavigateUp() = navController!!.navigateUp()

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
}