package com.badrun.my259firdaus.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.model.Soal

class SoalNoAdapter (
    private val soalList: List<Soal>,
    private val viewPager: ViewPager,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SoalNoAdapter.ViewHolder>() {

    private val answeredSoalIndices = mutableSetOf<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_soal_no)

        fun bind(soalNo: Int) {
            textView.text = soalNo.toString()

            // Set background dan warna teks sesuai status soal
            if (answeredSoalIndices.contains(soalNo)) {
                textView.setBackgroundResource(R.drawable.filled_shape_soal) // Drawable untuk kotak yang terisi
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white)) // Ganti dengan warna yang diinginkan
            } else {
                textView.setBackgroundResource(R.drawable.border_shape_soal) // Drawable untuk kotak dengan border
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary)) // Ganti dengan warna yang diinginkan
            }

            itemView.setOnClickListener {
                Log.e("BDR", "Item clicked at position: ${soalNo - 1}, CurrentItem before: ${viewPager.currentItem}")
                viewPager.currentItem = soalNo - 1 // Pindah ke soal yang dipilih
                Log.e("BDR", "CurrentItem after: ${viewPager.currentItem}")
                onItemClick(soalNo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_no_soal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("BDR", "Binding item at position: ${position + 1}")
        holder.bind(position + 1)
    }

    override fun getItemCount(): Int = soalList.size

    fun markSoalAnswered(index: Int) {
        answeredSoalIndices.add(index)
        notifyItemChanged(index - 1) // Perbarui item yang telah dijawab
    }

    fun resetAnsweredStatus() {
        answeredSoalIndices.clear() // Menghapus semua tanda soal yang sudah dijawab
        notifyDataSetChanged() // Memperbarui tampilan agar status terupdate
    }
}