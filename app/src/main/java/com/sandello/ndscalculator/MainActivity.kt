package com.sandello.ndscalculator

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val nightModePref by lazy { getSharedPreferences("nightMode", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNightMode()
        setContentView(R.layout.activity_main)

        fun showMenu(anchor: View) {
            val popup = PopupMenu(this, anchor)
            popup.menuInflater.inflate(R.menu.menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.menuTheme) {
                    themeAlert()
                }
                if (it.itemId == R.id.menuTranslate) {
                    val url = "https://lokalise.com/project/228402545e30480daadfd6.44294341/?view=multi&filter=platform_2"
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this, Uri.parse(url))
                }
                if (it.itemId == R.id.menuGitHub) {
                    val url = "https://github.com/jedi1150/VAT-Calculator"
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this, Uri.parse(url))
                }
                true
            }
        }

        menuButton.setOnClickListener {
            showMenu(it)
        }

        main_container.setOnApplyWindowInsetsListener { _, insets ->
            bottom_navigation.updatePadding(bottom = insets.systemWindowInsetBottom, right = insets.systemWindowInsetRight, left = insets.systemWindowInsetLeft)
            insets
        }
    }


    override fun onSupportNavigateUp() = findNavController(R.id.fragment).navigateUp()


    private fun themeAlert() {
        var items = arrayOf(getString(R.string.light), getString(R.string.dark), getString(R.string.battery_saver))
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.chooseTheme))
                    .setSingleChoiceItems(items, nightModePref.getInt("nightMode", 2)) { dialogInterface, i ->
                        when (i) {
                            0 -> setTheme(AppCompatDelegate.MODE_NIGHT_NO, 0)
                            1 -> setTheme(AppCompatDelegate.MODE_NIGHT_YES, 1)
                            2 -> setTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, 2)
                        }
                        dialogInterface.cancel()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.cancel() }
                    .show()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            items = arrayOf(getString(R.string.light), getString(R.string.dark), getString(R.string.system_default))
            MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.chooseTheme))
                    .setSingleChoiceItems(items, nightModePref.getInt("nightMode", 2)) { dialogInterface, i ->
                        when (i) {
                            0 -> setTheme(AppCompatDelegate.MODE_NIGHT_NO, 0)
                            1 -> setTheme(AppCompatDelegate.MODE_NIGHT_YES, 1)
                            2 -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, 2)
                        }
                        dialogInterface.cancel()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.cancel() }
                    .show()
        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        delegate.localNightMode = themeMode
        saveTheme(prefsMode)
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
        val nightModePref = getSharedPreferences("nightMode", Context.MODE_PRIVATE)
        if (nightModePref.getInt("nightMode", 2) == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }
        if (nightModePref.getInt("nightMode", 2) == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }

        if (nightModePref.getInt("nightMode", 2) == 2) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
    }

    private fun saveTheme(theme: Int) = nightModePref.edit().putInt("nightMode", theme).apply()
}
