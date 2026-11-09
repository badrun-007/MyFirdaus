package com.badrun.my259firdaus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.model.response.ResponseGetGuru

class MatpelGuruAdapter(private val matpelList: List<ResponseGetGuru.MatpelStatus>) :
    RecyclerView.Adapter<MatpelGuruAdapter.MatpelViewHolder>() {

    inner class MatpelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMatpel: TextView = itemView.findViewById(R.id.tvMatpel)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatpelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_matpel_status, parent, false)
        return MatpelViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatpelViewHolder, position: Int) {
        val matpel = matpelList[position]
        holder.tvMatpel.text = "${matpel.matpel} - ${matpel.kelas}"
        holder.tvStatus.text = if (matpel.sudah_ada_materi) "Sudah Buat Materi" else "Belum Buat Materi"
        holder.tvStatus.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (matpel.sudah_ada_materi) R.color.Green_muda else R.color.Orange
            )
        )
    }

    override fun getItemCount(): Int = matpelList.size
}