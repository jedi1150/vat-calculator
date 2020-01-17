package com.sandello.ndscalculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_vat.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class VatFragment : Fragment() {
    var originalString: String = ""
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myClipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        loadVal()
        if ((view.rootView!!.amountEditText!!.text.toString() != "" && view.rootView!!.amountEditText!!.text.toString() != "0") && (view.rootView!!.percentEditText!!.text.toString() != "" && view.rootView!!.percentEditText!!.text.toString() != "0")) {
            count()
        }
        view.rootView!!.amountEditText!!.isFocusableInTouchMode = true
        view.rootView!!.amountEditText!!.requestFocus()
        view.rootView!!.amountEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                view.rootView!!.amountEditText.removeTextChangedListener(this)
                format(s)
                view.rootView!!.amountEditText.addTextChangedListener(this)
                count()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        })
        view.rootView!!.percentEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                count()
                saveVal(view.rootView!!.percentEditText.text.toString(), "")
            }
        })

        vatAddEditText.setOnClickListener { copyVal("vatAdd") }
        amountIncludeEditText.setOnClickListener { copyVal("amountInclude") }
        vatNetEditText.setOnClickListener { copyVal("vatNet") }
        amountExcludeEditText.setOnClickListener { copyVal("amountExclude") }
    }

    fun format(s: CharSequence) {
        var formattedString = ""
        val current = resources.configuration.locale
        val formatter: DecimalFormat = NumberFormat.getInstance() as DecimalFormat
        formatter.roundingMode = RoundingMode.FLOOR
//        formatter.applyPattern("###,###.##")
        try {
            if (s.toString().substringAfter(',').isNotEmpty() && s.toString().substringAfter('.').isNotEmpty()) {
                originalString = s.replace("\\s".toRegex(), "")
                if (current == Locale.FRANCE)
                    if (s.toString().contains(",")) {
                        originalString = originalString.replace(",", ".")
                    }
                if (current == Locale.US)
                    if (s.toString().contains(",")) {
                        originalString = originalString.replace(",", "")
                    }
                if (current == Locale.GERMANY) {
                    if (s.toString().contains(".")) {
                        originalString = originalString.replace(".", "")
                    }
                    if (s.toString().contains(",")) {
                        originalString = originalString.replace(",", ".")
                    }
                }
                if (s.toString().contains(",")) {
                    originalString = originalString.replace(",", ".")
                }
                if (originalString.substringAfter('.').length > 2)
                    originalString = originalString.replaceAfter('.', originalString.substringAfter('.').substring(0, 2))

                formattedString = formatter.format(originalString.toDouble())


                view!!.rootView!!.amountEditText.setText(formattedString)
                view!!.rootView!!.amountEditText.setSelection(view!!.rootView!!.amountEditText.text!!.length)
                saveVal(view!!.rootView!!.percentEditText.text.toString(), originalString)
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun count() {
        val formatter: DecimalFormat = NumberFormat.getInstance() as DecimalFormat
        formatter.roundingMode = RoundingMode.HALF_UP
        formatter.maximumFractionDigits = 2
        try {
            if (view!!.rootView.amountEditText.text.toString().isNotEmpty() && view!!.rootView.percentEditText.text.toString().isNotEmpty()) {
                val amount = originalString.replace(",", ".").toDouble()
                val percent = view!!.rootView.percentEditText.text.toString().toDouble()
                //Начисление НДС
                val vatAdd = amount * percent / 100
                val amountInclude = amount + amount * percent / 100
                //Выделение НДС
                val vatNet = amount * percent / (percent + 100)
                val vatExclude = amount - vatNet

                vatAddEditText.setText(formatter.format(vatAdd))
                amountIncludeEditText.setText(formatter.format(amountInclude))
                vatNetEditText.setText(formatter.format(vatNet))
                amountExcludeEditText.setText(formatter.format(vatExclude))
            } else {
                vatAddEditText.setText("0")
                amountIncludeEditText.setText("0")
                vatNetEditText.setText("0")
                amountExcludeEditText.setText("0")
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun copyVal(viewString: String) {
        if (viewString == "vatAdd" && vatAddEditText.text.toString() != "0" && vatAddEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", vatAddEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + vatAddEditText.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "amountInclude" && amountIncludeEditText.text.toString() != "0" && amountIncludeEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", amountIncludeEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + amountIncludeEditText.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "vatNet" && vatNetEditText.text.toString() != "0" && vatNetEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", vatNetEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + vatNetEditText.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "amountExclude" && amountExcludeEditText.text.toString() != "0" && amountExcludeEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", amountExcludeEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + amountExcludeEditText.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveVal(percent: String, amount: String) {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        editor?.putString("percent", percent)
        if (amount != "") {
            editor?.putString("amount", amount)
            Log.d("amount", amount)
        }
        editor?.apply()
    }

    private fun loadVal() {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        view?.rootView?.amountEditText?.setText(prefs?.getString("amount", ""))
        view?.rootView?.percentEditText?.setText(prefs?.getString("percent", "20"))
        format(prefs?.getString("amount", "")!!)
    }
}
