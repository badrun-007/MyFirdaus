package com.badrun.my259firdaus.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.AyatActivity
import com.badrun.my259firdaus.model.Surat
import com.badrun.my259firdaus.model.SuratEntity

class AdapterSurat(val mContext: Context, val items:ArrayList<SuratEntity>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DATA_TYPE_SURAT : Int = 0
    private val DATA_TYPE_CACHE : Int = 1

    override fun getItemViewType(position: Int): Int {
        return if(items.isEmpty()){
            DATA_TYPE_CACHE
        } else {
            DATA_TYPE_SURAT
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var noSurat : TextView
        var namaSurat : TextView
        var arabSurat : TextView
        var infoSurat : TextView
        var cvSurat : CardView
        init {
            noSurat = view.findViewById(R.id.noSurat)
            namaSurat = view.findViewById(R.id.txtSurat)
            arabSurat = view.findViewById(R.id.arabName)
            infoSurat = view.findViewById(R.id.txtInfo)
            cvSurat = view.findViewById(R.id.item_surat)
        }
    }

    inner class ViewHolderDua(view: View): RecyclerView.ViewHolder(view){
        var noSurat : TextView
        var namaSurat : TextView
        var arabSurat : TextView
        var infoSurat : TextView
        var cvSurat : CardView
        init {
            noSurat = view.findViewById(R.id.noSurat)
            namaSurat = view.findViewById(R.id.txtSurat)
            arabSurat = view.findViewById(R.id.arabName)
            infoSurat = view.findViewById(R.id.txtInfo)
            cvSurat = view.findViewById(R.id.item_surat)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == DATA_TYPE_SURAT){
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_surat,parent,false)
            return ViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_surat,parent,false)
            return ViewHolderDua(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_TYPE_SURAT && items.isNotEmpty()){
            val data = items[position]
            val h = holder as ViewHolder
            h.noSurat.text = data.nomor.toString()
            h.namaSurat.text = data.namaLatin
            h.arabSurat.text = data.nama
            val infoSurat = "${data.tempatTurun} | ${data.jumlahAyat} ayat"
            h.infoSurat.text = infoSurat

            h.cvSurat.setOnClickListener{
                val i = Intent(mContext, AyatActivity::class.java)
                i.putExtra("nosurat", data.nomor)
                i.putExtra("namaSurat", data.namaLatin)
                mContext.startActivity(i)
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size

    }
    fun showData(dataSurat : List<SuratEntity>){
        items.clear()
        items.addAll(dataSurat)
        notifyDataSetChanged()
    }


}





