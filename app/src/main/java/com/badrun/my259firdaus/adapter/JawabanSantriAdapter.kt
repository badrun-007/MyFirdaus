package com.badrun.my259firdaus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.databinding.ItemJawabanSantriBinding
import com.badrun.my259firdaus.model.response.JawabanSantri
import com.bumptech.glide.Glide

class JawabanSantriAdapter(
    private val listJawaban: List<JawabanSantri>,
    private val onItemClick: (JawabanSantri) -> Unit
) : RecyclerView.Adapter<JawabanSantriAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemJawabanSantriBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(jawaban: JawabanSantri) {
            binding.tvNamaSantri.text = jawaban.nama
            binding.tvTeksJawaban.text = jawaban.teks_jawaban
            binding.tvWaktuKirim.text = jawaban.waktu_kirim ?: "-"
            binding.tvStatusJawaban.text = if (jawaban.sudah_kirim == true) "Sudah Kirim" else "Belum Kirim"

            // Set klik listener ke seluruh item
            itemView.setOnClickListener {
                onItemClick(jawaban)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJawabanSantriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listJawaban.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listJawaban[position])
    }
}

