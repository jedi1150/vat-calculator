package com.sandello.ndscalculator

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.github.moneytostr.MoneyToStr
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottom_fragment.*
import kotlinx.android.synthetic.main.fragment_vat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


@ExperimentalStdlibApi
class VatFragment : Fragment() {
    private var pref: SharedPreferences? = null

    private var amountDouble: Double? = null
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null
    private var formatter: DecimalFormat = NumberFormat.getNumberInstance() as DecimalFormat
    private var formatterCount: DecimalFormat = NumberFormat.getNumberInstance() as DecimalFormat
    private val groupSymb = formatter.decimalFormatSymbols.groupingSeparator
    private val decSymb = formatter.decimalFormatSymbols.decimalSeparator

    private var vatAdd: Double? = null
    private var amountInclude: Double? = null
    private var vatNet: Double? = null
    private var amountExclude: Double? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        vat_layout.setOnApplyWindowInsetsListener { _, insets ->
            vatLinear.updatePadding(top = insets.systemWindowInsetTop, bottom = (insets.systemWindowInsetBottom + bottom_navigation.measuredHeight + 200), right = insets.systemWindowInsetRight, left = insets.systemWindowInsetLeft)
            insets
        }
        formatter.roundingMode = RoundingMode.FLOOR
        formatter = DecimalFormat("#,###.##")
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        formatterCount.roundingMode = RoundingMode.FLOOR
        formatterCount = DecimalFormat("#,###.##")
        formatterCount.minimumFractionDigits = 2
        formatterCount.maximumFractionDigits = 2

        myClipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        amountEditText!!.isFocusableInTouchMode = true
        amountEditText!!.requestFocus()

        amountEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                amountEditText.removeTextChangedListener(this)
                format()
                amountEditText.addTextChangedListener(this)
                count()
                saveVal()
            }

        })
        amountEditTextLayout!!.setEndIconOnClickListener {
            amountEditText.setText("")
            amountDouble = null
            saveVal()
        }
        rateEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (rateEditText.hasFocus()) {
                    count()
                    saveVal()

                    val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    pref.edit().putString("rate_id", null).apply()
                    pref.edit().putString("customRate", s.toString()).apply()
                }
            }
        })

        vatAddTextLayout!!.setEndIconOnClickListener { numToWord(R.string.vat, vatAdd!!) }
        amountIncludeTextLayout!!.setEndIconOnClickListener { numToWord(R.string.include_vat, amountInclude!!) }
        vatNetTextLayout!!.setEndIconOnClickListener { numToWord(R.string.vat, vatNet!!) }
        amountNetTextLayout!!.setEndIconOnClickListener { numToWord(R.string.without_vat, amountExclude!!) }

        // Copy values
        vatAddEditText.setOnClickListener { copyVal("vatAdd", "") }
        amountIncludeEditText.setOnClickListener { copyVal("amountInclude", "") }
        vatNetEditText.setOnClickListener { copyVal("vatNet", "") }
        amountExcludeEditText.setOnClickListener { copyVal("amountExclude", "") }

        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        settingsButton.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(vat_layout.windowToken, 0)
            findNavController().navigate(R.id.action_vatFragment_to_settingsFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun format() {
        var string: String
        try {
            string = amountEditText.text.toString()
            var pos = amountEditText.selectionStart

            if (pos > 0 && (string.substring(pos - 1, pos).contains("[,.]".toRegex()))) {
                string = string.replaceRange(pos - 1, pos, decSymb.toString())
                if (string.toCharArray().count { it.toString().contains(decSymb) } < 2 && pos > 0) {
                    if (string.startsWith(decSymb))
                        string = "0${string}"
                    amountEditText.setText(string).toString()
                    string = string.replaceFirst(decSymb.toString(), ":")
                    string = string.replace("[,.]".toRegex(), "")
                    string = string.replace(groupSymb.toString(), "") // Remove digit separator
                    string = string.replace(":", ".")
                    amountDouble = string.toDouble()
                    amountEditText.setSelection(amountEditText.text!!.length)
                }
            }
            if (string.isNotEmpty() && string.substringAfter(",").isNotEmpty() && string.substringAfter(".").isNotEmpty()) {
                amountEditTextLayout.error = ""

                string = string.replaceFirst(decSymb.toString(), ":")
                string = string.replace("[,.]".toRegex(), "")
                string = string.replace(groupSymb.toString(), "") // Remove digit separator
                string = string.replace(":", ".")
                if (string.startsWith(".")) {
                    string = string.replaceRange(0, 0, "0")
                }


                //Number of decimal places in the results
                if (string.contains(".")) {
                    if (string.substringAfter(".").length <= 2) {
                        formatterCount.minimumFractionDigits = 2
                        formatterCount.maximumFractionDigits = 2
                    }
                }


                if (string.substringAfter(".") != "0" && string.substringAfter(".") != ".") {
                    if (string.contains(".")) {
                        if (string.substringAfter(".").length <= 2) {
                            amountDouble = string.toDouble()
                        }
                    } else {
                        amountDouble = string.toDouble()
                    }
                    if (!string.contains(".")) {
                        amountEditText.setText(formatter.format(amountDouble!!).toString())
                        pos = amountEditText.text!!.length
                    } else {
                        when {
                            string.substringAfter(".").isEmpty() -> {
                                amountEditText.setText("${formatter.format(amountDouble!!)}${formatter.decimalFormatSymbols.decimalSeparator}")
                            }
                            else -> {
                                amountEditText.setText(formatter.format(amountDouble!!).toString())
                            }
                        }
                        if (pos > amountEditText.text!!.length) pos = amountEditText.text!!.length
                    }
                    amountEditText.setSelection(pos)
                } else {
                    amountDouble = string.toDouble()
                }
            } else if (string.isEmpty()) {
                amountEditTextLayout.error = ""
            } else if (string.substringAfter(".").isEmpty() || string.substringAfter(".") == "0") {
                amountEditTextLayout.error = ""
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun count() {
        try {
            if (amountDouble != null && amountEditText.text.toString().isNotEmpty() && rateEditText.text.toString().isNotEmpty()) {
                val amount = amountDouble!!
                val percent = rateEditText.text.toString().toDouble()
                //Начисление НДС
                vatAdd = amount * percent / 100
                amountInclude = amount + amount * percent / 100
                //Выделение НДС
                vatNet = amount * percent / (percent + 100)
                amountExclude = amount - vatNet!!

                vatAddEditText.setText(formatterCount.format(vatAdd!!))
                amountIncludeEditText.setText(formatterCount.format(amountInclude!!))
                vatNetEditText.setText(formatterCount.format(vatNet!!))
                amountExcludeEditText.setText(formatterCount.format(amountExclude!!))
            } else {
                vatAddEditText.setText("")
                amountIncludeEditText.setText("")
                vatNetEditText.setText("")
                amountExcludeEditText.setText("")
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun numToWord(title: Int, s: Double) {
        val currency = pref?.getString("currency", "") // Currency code. Ex: USD
        val penny = pref?.getString("penny", "") // Penny type. Ex: Text

        val currencySelected = when (currency) {
            "RUB" -> {
                MoneyToStr.Currency.RUR
            }
            "USD" -> {
                MoneyToStr.Currency.USD
            }
            "EUR" -> {
                MoneyToStr.Currency.EUR
            }
            "UAH" -> {
                MoneyToStr.Currency.UAH
            }
            else -> {
                MoneyToStr.Currency.USD
            }
        }

        val currencyLanguageSelected = when (Locale.getDefault()) {
            Locale.forLanguageTag("ru") -> {
                MoneyToStr.Language.RUS
            }
            Locale.forLanguageTag("uk") -> {
                MoneyToStr.Language.UKR
            }
            Locale.forLanguageTag("en") -> {
                MoneyToStr.Language.ENG
            }
            else -> {
                MoneyToStr.Language.ENG
            }
        }

        val pennySelected = when (penny) {
            "penny_type_text" -> {
                MoneyToStr.Pennies.TEXT
            }
            "penny_type_number" -> {
                MoneyToStr.Pennies.NUMBER
            }
            else -> {
                MoneyToStr.Pennies.TEXT
            }
        }

        val moneyAsWords: String = MoneyToStr(currencySelected, currencyLanguageSelected, pennySelected).convert(s)
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(title))
                .setMessage(moneyAsWords)
                .setPositiveButton(android.R.string.copy) { _, _ -> copyVal(null, moneyAsWords) }
                .show()
    }

    private fun checkToTranslate() {
        vatAddTextLayout!!.isEndIconVisible = true
        amountIncludeTextLayout!!.isEndIconVisible = true
        vatNetTextLayout!!.isEndIconVisible = true
        amountNetTextLayout!!.isEndIconVisible = true
    }

    private fun copyVal(viewString: String?, value: String?) {
        if (viewString == "vatAdd" && vatAddEditText.text.toString() != "0" && vatAddEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", vatAddEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(snackbar, "${getString(R.string.copied)} ${vatAddEditText.text}", Snackbar.LENGTH_SHORT).show()
        } else if (viewString == "amountInclude" && amountIncludeEditText.text.toString() != "0" && amountIncludeEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", amountIncludeEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(snackbar, "${getString(R.string.copied)} ${amountIncludeEditText.text}", Snackbar.LENGTH_SHORT).show()
        } else if (viewString == "vatNet" && vatNetEditText.text.toString() != "0" && vatNetEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", vatNetEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(snackbar, "${getString(R.string.copied)} ${vatNetEditText.text}", Snackbar.LENGTH_SHORT).show()
        } else if (viewString == "amountExclude" && amountExcludeEditText.text.toString() != "0" && amountExcludeEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", amountExcludeEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(snackbar, "${getString(R.string.copied)} ${amountExcludeEditText.text}", Snackbar.LENGTH_SHORT).show()
        } else if (viewString == null) {
            myClip = ClipData.newPlainText("text", value)
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(snackbar, getString(R.string.copied), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveVal() {
        val prefs = context?.getSharedPreferences("val", MODE_PRIVATE)
        val editor = prefs?.edit()
        try {
            if (amountDouble != null)
                editor?.putString("amount", amountDouble.toString())
            else
                editor?.putString("amount", "")
        } catch (e: NumberFormatException) {
        }
        editor?.apply()
    }

    private fun loadVal() {
        checkToTranslate()

        val prefsVal = context?.getSharedPreferences("val", MODE_PRIVATE)
        val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "rates"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        val amount = prefsVal?.getString("amount", "")
        val saveValue = pref?.getBoolean("save_value", true)
        val selectedRate = pref?.getString("rate_id", "") // Country code. Ex: ru
        val customRate = pref?.getString("customRate", "")


        if (saveValue!! && amount != null) { // If save values parameter is true set amount value
            try {
                amountEditText.setText(formatter.format(amount.toDouble()))
            } catch (e: NumberFormatException) {
            }
        }


        var retrievedRate = ""

        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (!ratesReceived) withContext(Dispatchers.IO) { retrievedRate = GetRates().main(requireContext()) } // Initialize receive rates only at launch
                launch(Dispatchers.Main) {
                    if (selectedRate != "" && db.rateDao().getAll().isNotEmpty()) {
                        val daoRate = db.rateDao().findById(selectedRate!!.toInt())!!.rate.toString()
                        if (daoRate != "") {
                            if (daoRate.substringAfter(".") == "0")
                                rateEditText?.setText(daoRate.substringBefore("."))
                            else
                                rateEditText?.setText(daoRate)
                        }
                    } else if (customRate != "") {
                        rateEditText?.setText(customRate)
                    } else {
                        if (retrievedRate != "") {
                            pref?.edit()?.putString("rate", Locale.getDefault().language)?.apply()
                            if (retrievedRate.substringAfter(".") == "0")
                                rateEditText?.setText(retrievedRate.substringBefore("."))
                            else
                                rateEditText?.setText(retrievedRate)
                        }
                    }
                    count()
                }
            } catch (e: Exception) {
            }
        }
        count()
        checkToTranslate()
        ratesReceived = true
        if (selectedRate == "" && customRate == "") {
            if (db.rateDao().getAll().isNotEmpty())
                Snackbar.make(snackbar, getString(R.string.set_vat_rate), Snackbar.LENGTH_LONG).setAction(getString(R.string.set)) {
                    val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(vat_layout.windowToken, 0)
                    val bundle = bundleOf("setRate" to true)
                    findNavController().navigate(R.id.action_vatFragment_to_settingsFragment, bundle)
                }.show()
            else
                Snackbar.make(snackbar, getString(R.string.set_vat_rate), Snackbar.LENGTH_LONG).show()
            amountEditText!!.isFocusableInTouchMode = true
            amountEditText!!.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        val compactView = pref?.getBoolean("compact_view", true) // Linear Layout orientation
        ll1.orientation = when (compactView) {
            true -> LinearLayout.HORIZONTAL
            false -> LinearLayout.VERTICAL
            else -> LinearLayout.HORIZONTAL
        }
        ll2.orientation = when (compactView) {
            true -> LinearLayout.HORIZONTAL
            false -> LinearLayout.VERTICAL
            else -> LinearLayout.HORIZONTAL
        }
        loadVal()
    }

    override fun onPause() {
        super.onPause()
        amountEditText.setText("")
    }
}