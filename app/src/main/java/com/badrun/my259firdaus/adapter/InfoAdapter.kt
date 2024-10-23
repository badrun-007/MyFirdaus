package com.badrun.my259firdaus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R

class InfoAdapter(private val infoList: List<String>) :
    RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_info, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.textInfo.text = infoList[position]
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textInfo: TextView = itemView.findViewById(R.id.textInfo)
    }
}