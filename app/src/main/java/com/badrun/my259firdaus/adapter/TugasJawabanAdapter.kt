package com.badrun.my259firdaus.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.databinding.ItemTugasBinding
import com.badrun.my259firdaus.model.response.JawabanSantri
import com.badrun.my259firdaus.model.response.TugasJawabanItem

class TugasJawabanAdapter(
    private val context: Context,
    private val listTugas: List<TugasJawabanItem>,
    private val onItemClick: (TugasJawabanItem) -> Unit

) : RecyclerView.Adapter<TugasJawabanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTugasBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tugas: TugasJawabanItem) {
            binding.tvJudul.text = tugas.judul
            binding.tvTanggal.text = tugas.deadline
            binding.tvJumlahJawaban.text = "${tugas.jumlah_jawaban} jawaban masuk"

            itemView.setOnClickListener {
                onItemClick(tugas)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTugasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listTugas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTugas[position])
    }
}

