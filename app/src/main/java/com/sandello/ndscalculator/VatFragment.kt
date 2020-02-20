package com.sandello.ndscalculator

import android.annotation.SuppressLint
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


class VatFragment : Fragment() {
    private var amountDouble: Double? = null
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null
    var formatter: DecimalFormat = NumberFormat.getNumberInstance() as DecimalFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formatter.roundingMode = RoundingMode.FLOOR
        formatter = DecimalFormat("#,###.##")
        formatter.maximumFractionDigits = 2

        myClipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        loadVal()

        view.rootView!!.amountEditText!!.isFocusableInTouchMode = true
        view.rootView!!.amountEditText!!.requestFocus()
        view.rootView!!.amountEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.rootView!!.amountEditText.removeTextChangedListener(this)
                format()
                view.rootView!!.amountEditText.addTextChangedListener(this)
                count()
            }

        })
        view.rootView!!.percentEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                count()
                saveVal(view.rootView!!.percentEditText.text.toString())
            }
        })

        vatAddEditText.setOnClickListener { copyVal("vatAdd") }
        amountIncludeEditText.setOnClickListener { copyVal("amountInclude") }
        vatNetEditText.setOnClickListener { copyVal("vatNet") }
        amountExcludeEditText.setOnClickListener { copyVal("amountExclude") }
    }

    @SuppressLint("SetTextI18n")
    private fun format() {
        var string: String
        try {
            formatter.minimumFractionDigits = 0
            string = view!!.rootView.amountEditText.text.toString()
            var pos = view!!.rootView.amountEditText.selectionStart

            if (string.isNotEmpty()) {
                view!!.rootView.amountEditTextLayout.error = ""
                if (pos > 0 && (string.substring(pos - 1, pos).contains("[,.]".toRegex()))) {
                    string = string.replaceRange(pos - 1, pos, ".")
                    view!!.rootView.amountEditText.setSelection(view!!.rootView.amountEditText.text!!.length)
                }
                string = string.replaceFirst("[,.]".toRegex(), ":")
                string = string.replace("[,.]".toRegex(), "")
                string = string.replace(formatter.decimalFormatSymbols.groupingSeparator.toString(), "")
                string = string.replace(":", ".")
                if (string.startsWith(".")) {
                    string = string.replaceRange(0, 0, "0")
                }
                Log.d("string1", string)
                if (string.substringAfter(formatter.decimalFormatSymbols.decimalSeparator) != "0"
                        && string.substringAfter(".") != "."
                ) {
                    if (string.contains(".")) {
                        if (string.substringAfter(".").length <= 2) {
                            amountDouble = string.toDouble()
                        }
                    } else {
                        amountDouble = string.toDouble()
                    }
                    if (!string.contains(".")) {
                        view!!.rootView.amountEditText.setText(formatter.format(amountDouble).toString())
                        pos = view!!.rootView.amountEditText.text!!.length
                    } else {
                        when {
                            string.substringAfter(".").length in 1..2 -> {
                                view!!.rootView.amountEditText.setText(formatter.format(amountDouble).toString())

                            }
                            string.substringAfter(".").isEmpty() -> {
                                view!!.rootView.amountEditText.setText("${formatter.format(amountDouble)}${formatter.decimalFormatSymbols.decimalSeparator}")
                            }
                            else -> {
                                view!!.rootView.amountEditText.setText(formatter.format(amountDouble).toString())
                            }
                        }
                        if (pos > view!!.rootView.amountEditText.text!!.length) pos = view!!.rootView.amountEditText.text!!.length
                    }
                    view!!.rootView.amountEditText.setSelection(pos)
                }

            } else if (string.isEmpty()) {
                view!!.rootView.amountEditTextLayout.error = ""
            } else if (string.substringAfter(".").isEmpty() || string.substringAfter(".") == "0") {
                view!!.rootView.amountEditTextLayout.error = ""
            } else {
                view!!.rootView.amountEditTextLayout.error = "проверьте сумму"
            }
            saveVal(view!!.rootView!!.percentEditText.text.toString())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun count() {
        formatter.minimumFractionDigits = 2
        try {
            if (amountDouble != null && view!!.rootView.amountEditText.text.toString().isNotEmpty() && view!!.rootView.percentEditText.text.toString().isNotEmpty()) {
                val amount = amountDouble!!
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
            Snackbar.make(view!!.rootView.snackbar, "${getString(R.string.copied)} ${vatAddEditText.text}", Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "amountInclude" && amountIncludeEditText.text.toString() != "0" && amountIncludeEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", amountIncludeEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, "${getString(R.string.copied)} ${amountIncludeEditText.text}", Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "vatNet" && vatNetEditText.text.toString() != "0" && vatNetEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", vatNetEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, "${getString(R.string.copied)} ${vatNetEditText.text}", Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "amountExclude" && amountExcludeEditText.text.toString() != "0" && amountExcludeEditText.text.toString() != "") {
            myClip = ClipData.newPlainText("text", amountExcludeEditText.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, "${getString(R.string.copied)} ${amountExcludeEditText.text}", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveVal(percent: String) {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        try {
            editor?.putString("percent", percent)
            editor?.putString("amount", amountDouble.toString())
        } catch (e: NumberFormatException) {
        }
        editor?.apply()
    }

    private fun loadVal() {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        view?.rootView?.percentEditText?.setText(prefs?.getString("percent", "20"))
        try {
            view!!.rootView.amountEditText.setText(formatter.format(prefs!!.getString("amount", "")?.toDouble()))
            format()
        } catch (e: NumberFormatException) {
        }
        count()
    }
}