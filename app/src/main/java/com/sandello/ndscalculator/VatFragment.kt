package com.sandello.ndscalculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
                try {
                    if (s.toString().substringAfter(',').isNotEmpty()) {
                        var originalString = s.toString()
                        val longVal: Long
                        if (s.toString().contains(",")) {
                            originalString = originalString.replace(",", "")
                        }
                        longVal = originalString.toLong()
                        val formatter: DecimalFormat = NumberFormat.getInstance() as DecimalFormat
                        formatter.applyPattern("###,###.##")
                        formatter.roundingMode = RoundingMode.FLOOR
                        val formattedString: String = formatter.format(longVal)
                        if (s.toString().substringAfter('.').isNotEmpty()) {
                            view.rootView!!.amountEditText.setText(formattedString)
                            view.rootView!!.amountEditText.setSelection(view.rootView!!.amountEditText.text!!.length)
                        }
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                view.rootView!!.amountEditText.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                count()
                saveVal()
            }
        })
        view.rootView!!.percentEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0 && s.contains(",")) {
                    val comPos = view.rootView!!.percentEditText.text.toString().indexOf(",")
                    view.rootView!!.percentEditText.setText(s.replace(",".toRegex(), "."))
                    view.rootView!!.percentEditText.setSelection(comPos + 1)
                } else {
                    count()
                    saveVal()
                }
            }
        })


        vatAddEditText.setOnClickListener {
            copyVal("vatAdd")
        }
        amountIncludeEditText.setOnClickListener {
            copyVal("amountInclude")
        }
        vatNetEditText.setOnClickListener {
            copyVal("vatNet")
        }
        amountExcludeEditText.setOnClickListener {
            copyVal("amountExclude")
        }
    }

    fun count() {
        try {
            if (view!!.rootView.amountEditText.text.toString().isNotEmpty() && view!!.rootView.percentEditText.text.toString().isNotEmpty()) {
                val amount = view!!.rootView.amountEditText.text.toString().replace(",", "").toDouble()
                val percent = view!!.rootView.percentEditText.text.toString().replace(",", "").toDouble()
                //Начисление НДС
                val vatAdd = amount * percent / 100.toDouble()
                val amountInclude = amount + amount * percent / 100.toDouble()
                //Выделение НДС

                val vatNet = amount * percent / (percent + 100)
                val vatExclude = amount - vatNet
                vatAddEditText.setText(String.format("%,1.2f", vatAdd))
                amountIncludeEditText.setText(String.format("%,1.2f", amountInclude))
                vatNetEditText.setText(String.format("%,1.2f", vatNet))
                amountExcludeEditText.setText(String.format("%,1.2f", vatExclude))

                vatAddEditText.isEnabled = true
                amountIncludeEditText.isEnabled = true
                vatNetEditText.isEnabled = true
                amountExcludeEditText.isEnabled = true
            } else {
                vatAddEditText.setText("0")
                amountIncludeEditText.setText("0")
                vatNetEditText.setText("0")
                amountExcludeEditText.setText("0")
                vatAddEditText.isEnabled = false
                amountIncludeEditText.isEnabled = false
                vatNetEditText.isEnabled = false
                amountExcludeEditText.isEnabled = false
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

    private fun saveVal() {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        editor?.putString("amount", view?.rootView?.amountEditText?.text.toString())
        editor?.putString("percent", view?.rootView?.percentEditText?.text.toString())
        editor?.apply()
    }

    private fun loadVal() {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        view?.rootView?.amountEditText?.setText(prefs?.getString("amount", ""))
        view?.rootView?.percentEditText?.setText(prefs?.getString("percent", "20"))
    }
}
