package com.badrun.my259firdaus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.model.response.ResponseGetGuru

class GuruAdapter(
    private val guruList: List<ResponseGetGuru>,
    private val onItemClick: (ResponseGetGuru) -> Unit
) : RecyclerView.Adapter<GuruAdapter.GuruViewHolder>() {

    inner class GuruViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaGuru)
        val tvJumlahMateri: TextView = itemView.findViewById(R.id.tvJumlahMateri)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuruViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guru, parent, false)
        return GuruViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuruViewHolder, position: Int) {
        val guru = guruList[position]

        val jumlahTrue = guru.matpels.count { it.sudah_ada_materi }
        val totalMatpel = guru.matpels.size

        holder.tvNama.text = guru.nama
        holder.tvJumlahMateri.text = "Materi: $jumlahTrue/$totalMatpel"
        holder.itemView.setOnClickListener { onItemClick(guru) }
    }

    override fun getItemCount(): Int = guruList.size
}

