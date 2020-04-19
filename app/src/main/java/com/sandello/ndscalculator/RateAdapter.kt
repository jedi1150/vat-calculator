package com.sandello.ndscalculator

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "rates")
data class Rate(
        @PrimaryKey var code: String = "",
        var country: String = "",
        var rate: Int = 0
)

class RateAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val rates: List<Rate>) :
        ArrayAdapter<Rate>(context, layoutResource, rates) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    @SuppressLint("SetTextI18n")
    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = "${rates[position].code} - ${rates[position].country} - ${rates[position].rate}"
        return view
    }


}