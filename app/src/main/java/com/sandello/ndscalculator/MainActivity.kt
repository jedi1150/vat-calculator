package com.sandello.ndscalculator

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.unit.Duration
import androidx.compose.ui.unit.inMilliseconds
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

var ratesReceived: Boolean = false

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null
    private lateinit var mInterstitialAd: InterstitialAd

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

        val adDialog = AlertDialog.Builder(this)
        adDialog.setTitle(getString(R.string.ad))
        adDialog.setMessage(getString(R.string.adMessage))
        adDialog.setCancelable(false)
        adDialog.setPositiveButton(getString(R.string.adWatch)) { _, _ ->
            mInterstitialAd.show(this)
        }
        adDialog.setNeutralButton(android.R.string.cancel) { _, _ ->
        }

        val adRequest = AdRequest.Builder().build()

        MobileAds.setRequestConfiguration(RequestConfiguration.Builder().setTestDeviceIds(listOf("E391F97E4B9B64A011FDEE11C58AEECF")).build())
        InterstitialAd.load(this, "ca-app-pub-3591700046184217/4372540618", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("adMob", adError.message)
                mInterstitialAd
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("adMob", "Ad was loaded.")
                mInterstitialAd = interstitialAd
                GlobalScope.launch(Dispatchers.Main) {
                    delay(Duration(seconds = 30).inMilliseconds())
                    adDialog.show()
                }
            }
        })
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