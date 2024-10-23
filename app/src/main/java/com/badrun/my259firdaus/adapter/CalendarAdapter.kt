package com.badrun.my259firdaus.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.model.calendarModel
import java.util.Calendar

class CalendarAdapter(private val mData : ArrayList<calendarModel>) :
    RecyclerView.Adapter<CalendarAdapter.HomeItem>() {

    override fun onBindViewHolder(holder: HomeItem, position: Int) {
        val item = mData[position]

        holder.date.text = item.date.toString()

        // Set default background color
        holder.date.setBackgroundColor(Color.TRANSPARENT)

        // Set text color based on month and year
        if (item.month == item.calendarCompare.get(Calendar.MONTH) && item.year == item.calendarCompare.get(Calendar.YEAR)) {
            holder.date.setTextColor(ContextCompat.getColor(holder.context, R.color.date_true))
        } else {
            holder.date.setTextColor(ContextCompat.getColor(holder.context, R.color.date_false))
        }

        // Set specific date background color
        if (item.status == "hijau") {
            holder.date.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.dot))
        } else if (item.status == "merah") {
            holder.date.setBackgroundColor(Color.RED)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return HomeItem(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class HomeItem(mView: View) : RecyclerView.ViewHolder(mView) {
        var itemLayout: LinearLayout = mView.findViewById(R.id.item_layout)
        var date: TextView = mView.findViewById(R.id.date)
        var dot: ImageView = mView.findViewById(R.id.dot)
        var context: Context = mView.context
    }
}