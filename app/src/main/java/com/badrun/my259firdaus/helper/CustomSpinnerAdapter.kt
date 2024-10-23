package com.badrun.my259firdaus.helper

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.badrun.my259firdaus.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>,
    private val fontPath: String
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.typeface = Typeface.createFromAsset(context.assets, fontPath)
        textView.text = items[position]
        if (position == 0) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.Orange)) // Set color black for first item
        } else {
            textView.setTextColor(Color.BLACK) // Set color orange for other items
        }
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.typeface = Typeface.createFromAsset(context.assets, fontPath)
        textView.text = items[position]
        if (position == 0) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.Grey)) // Set color black for first item
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.Green)) // Set color orange for other items
        } // Set color orange for selected item
        return view
    }
}