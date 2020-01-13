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


class VatFragment : Fragment() {
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vat, container, false)
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myClipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        loadVal()
        if ((view.rootView!!.editText2!!.text.toString() != "" && view.rootView!!.editText2!!.text.toString() != "0") && (view.rootView!!.editText3!!.text.toString() != "" && view.rootView!!.editText3!!.text.toString() != "0")) {
            count()
            view.rootView?.clear_fab?.show()
        }
        view.rootView!!.editText2!!.isFocusableInTouchMode = true
        view.rootView!!.editText2!!.requestFocus()
        view.rootView!!.editText2!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
//                if (s.isNotEmpty()) {
//                    val comPos = view.rootView!!.editText2.selectionStart
//                    view.rootView!!.editText2.removeTextChangedListener(this)
//                    view.rootView!!.editText2.setText(String.format("%-,8f", s.toString().toFloat()))
//                    view.rootView!!.editText2.addTextChangedListener(this)
//                    view.rootView!!.editText2.setSelection(comPos + 1)
//                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0 && s.contains(",")) {
                    val comPos = view.rootView!!.editText2.text.toString().indexOf(",")
                    view.rootView!!.editText2.setText(s.replace(",".toRegex(), "."))
                    view.rootView!!.editText2.setSelection(comPos + 1)
                } else {
                    count()
                    saveVal()
                }
            }
        })
        view.rootView!!.editText3!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0 && s.contains(",")) {
                    val comPos = view.rootView!!.editText3.text.toString().indexOf(",")
                    view.rootView!!.editText3.setText(s.replace(",".toRegex(), "."))
                    view.rootView!!.editText3.setSelection(comPos + 1)
                } else {
                    count()
                    saveVal()
                }
            }
        })
        view.rootView?.clear_fab?.setOnClickListener {
            view.rootView?.editText2?.setText("")
            view.rootView?.clear_fab?.hide()
        }

        editText7.setOnClickListener {
            copyVal("7")
        }
        editText8.setOnClickListener {
            copyVal("8")
        }
        editText9.setOnClickListener {
            copyVal("9")
        }
        editText10.setOnClickListener {
            copyVal("10")
        }
    }

    fun count() {
        try {
            if (view!!.rootView.editText2.text.toString().isNotEmpty() && view!!.rootView.editText3.text.toString().isNotEmpty()) {
                val value = view!!.rootView.editText2.text.toString().toDouble()
                val vat = view!!.rootView.editText3.text.toString().toDouble()
                //Начисление НДС
                val vat1 = value * vat / 100.toDouble()
                val sum1 = value + value * vat / 100.toDouble()
                //Выделение НДС

                val vat2 = value * vat / (vat + 100)
                val sum2 = value - vat2
                editText7.setText(String.format("%,1.2f", vat1))
                editText8.setText(String.format("%,1.2f", sum1))
                editText9.setText(String.format("%,1.2f", vat2))
                editText10.setText(String.format("%,1.2f", sum2))

                editText7.isEnabled = true
                editText8.isEnabled = true
                editText9.isEnabled = true
                editText10.isEnabled = true
                view!!.rootView?.clear_fab?.show()
            } else {
                editText7.setText("0")
                editText8.setText("0")
                editText9.setText("0")
                editText10.setText("0")
                editText7.isEnabled = false
                editText8.isEnabled = false
                editText9.isEnabled = false
                editText10.isEnabled = false
                view!!.rootView?.clear_fab?.hide()
            }
        } catch (e: Exception) {
            Log.d("count", e.message.toString())
        }
    }

    private fun copyVal(viewString: String) {
        if (viewString == "7" && editText7.text.toString() != "0" && editText7.text.toString() != "") {
            myClip = ClipData.newPlainText("text", editText7.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + editText7.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "8" && editText8.text.toString() != "0" && editText8.text.toString() != "") {
            myClip = ClipData.newPlainText("text", editText8.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + editText8.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "9" && editText9.text.toString() != "0" && editText9.text.toString() != "") {
            myClip = ClipData.newPlainText("text", editText9.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + editText9.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
        if (viewString == "10" && editText10.text.toString() != "0" && editText10.text.toString() != "") {
            myClip = ClipData.newPlainText("text", editText10.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, getString(R.string.copied) + " " + editText10.text.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveVal() {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        editor?.putString("et2", view?.rootView?.editText2?.text.toString())
        editor?.putString("et3", view?.rootView?.editText3?.text.toString())
        editor?.apply()
    }

    private fun loadVal() {
        val prefs = context?.getSharedPreferences("val", Context.MODE_PRIVATE)
        view?.rootView?.editText2?.setText(prefs?.getString("et2", ""))
        view?.rootView?.editText3?.setText(prefs?.getString("et3", "20"))
    }
}
