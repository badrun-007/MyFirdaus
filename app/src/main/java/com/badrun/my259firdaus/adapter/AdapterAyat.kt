package com.badrun.my259firdaus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.model.Ayat

class AdapterAyat(val mContext : Context, var items:ArrayList<Ayat.DataAyat>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DATA_AYAT_SERVER = 0
    private val DATA_AYAT_ROOM = 1

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()){
            DATA_AYAT_ROOM
        } else {
            DATA_AYAT_SERVER
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvNomorAyat : TextView
        var tvArabic : TextView
        var tvTerjemahan : TextView

        init {
            tvNomorAyat = itemView.findViewById(R.id.noAyat_card)
            tvArabic = itemView.findViewById(R.id.txtAyat_card)
            tvTerjemahan = itemView.findViewById(R.id.txtArti_card)

        }
    }

    inner class ViewHolderDua(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvNomorAyat : TextView
        var tvArabic : TextView
        var tvTerjemahan : TextView

        init {
            tvNomorAyat = itemView.findViewById(R.id.noAyat_card)
            tvArabic = itemView.findViewById(R.id.txtAyat_card)
            tvTerjemahan = itemView.findViewById(R.id.txtArti_card)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == DATA_AYAT_SERVER){
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_ayat,parent,false)
            return ViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_ayat,parent,false)
            return ViewHolderDua(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_AYAT_SERVER && items.isNotEmpty()){
            val arab = items[position]
            val h = holder as ViewHolder
            h.tvArabic.text = arab.teksArab
            h.tvNomorAyat.text = arab.nomorAyat.toString()
            h.tvTerjemahan.text = arab.teksIndonesia

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun showData(dataNews : ArrayList<Ayat.DataAyat>){
        items.clear()
        items.addAll(dataNews)
        notifyDataSetChanged()
    }



}