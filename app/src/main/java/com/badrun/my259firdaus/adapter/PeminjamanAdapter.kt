package com.badrun.my259firdaus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.model.Payment
import com.badrun.my259firdaus.model.PeminjamanData
import com.bumptech.glide.Glide
import org.apache.commons.text.WordUtils
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PeminjamanAdapter(private var dataList: List<PeminjamanData>, private val onPaymentClick: (PeminjamanData) -> Unit) : RecyclerView.Adapter<PeminjamanAdapter.PeminjamanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeminjamanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_peminjaman, parent, false)
        return PeminjamanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeminjamanViewHolder, position: Int) {
        val peminjaman = dataList[position]
        holder.bind(peminjaman)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(newData: List<PeminjamanData>) {
        dataList = newData
        notifyDataSetChanged()
    }

    inner class PeminjamanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val judulBukuTextView: TextView = itemView.findViewById(R.id.book_title)
        private val penulisBukuTextView: TextView = itemView.findViewById(R.id.book_author)
        private val waktuPeminjaman: TextView = itemView.findViewById(R.id.waktu_peminjaman)
        private val tanggalPeminjaman : TextView = itemView.findViewById(R.id.tgl_peminjaman)
        private val codeBuku : TextView = itemView.findViewById(R.id.code_buku)
        private val tanggalPengembalian : TextView = itemView.findViewById(R.id.tgl_pengembalian)
        private val statusPeminjaman : TextView = itemView.findViewById(R.id.status_peminjaman)
        private val coverBuku : ImageView = itemView.findViewById(R.id.book_cover)
        private val denda : TextView = itemView.findViewById(R.id.denda)
        private val card : CardView = itemView.findViewById(R.id.layoutBuku)
        // Tambahkan view lainnya sesuai kebutuhan

        fun bind(peminjaman: PeminjamanData) {
            judulBukuTextView.text = peminjaman.judul_buku
            penulisBukuTextView.text = peminjaman.penulis
            codeBuku.text = "Kode Buku : "+peminjaman.id_buku
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'Jam' HH:mm:ss", Locale("id", "ID"))

            try {
                // Parse dan format tanggal
                val datePeminjaman = inputFormat.parse(peminjaman.tgl_peminjaman)
                val datePengembalian = inputFormat.parse(peminjaman.tgl_pengembalian)
                val formattedPeminjaman = outputFormat.format(datePeminjaman as Date)
                val formattedPengembalian = outputFormat.format(datePengembalian as Date)

                tanggalPeminjaman.text = "Tanggal Peminjaman: $formattedPeminjaman"
                tanggalPengembalian.text = "Tanggal Pengembalian: $formattedPengembalian"
            } catch (e: Exception) {
                e.printStackTrace()
                tanggalPeminjaman.text = "Tanggal Peminjaman: "
                tanggalPengembalian.text = "Tanggal Pengembalian: "
            }

            waktuPeminjaman.text = "Waktu Peminjaman : "+peminjaman.waktu_peminjaman.toString()+" Hari"
            statusPeminjaman.text = "Status Peminjaman : "+ peminjaman.status
            val link = "https://${ApiConfig.iplink.ip}/storage/buku/"+peminjaman.cover
            Glide.with(coverBuku.context).load(link).into(coverBuku)


            if (peminjaman.denda > 0) {
                denda.visibility = View.VISIBLE
                val formattedDenda = formatNumberWithSeparator(peminjaman.denda)
                val dendaTerbilang = numberToWords(peminjaman.denda.toLong())
                denda.text = "Denda : Rp $formattedDenda,00 \n($dendaTerbilang Rupiah)"
            } else {
                denda.visibility = View.GONE
            }

            card.setOnClickListener {
                onPaymentClick(peminjaman)
            }

        }
    }

    fun numberToWords(number: Long): String {
        if (number == 0L) return "Nol"

        val ones = arrayOf("", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan")
        val teens = arrayOf("Sepuluh", "Sebelas", "Dua Belas", "Tiga Belas", "Empat Belas", "Lima Belas", "Enam Belas", "Tujuh Belas", "Delapan Belas", "Sembilan Belas")
        val tens = arrayOf("", "Sepuluh", "Dua Puluh", "Tiga Puluh", "Empat Puluh", "Lima Puluh", "Enam Puluh", "Tujuh Puluh", "Delapan Puluh", "Sembilan Puluh")
        val hundreds = arrayOf("", "Seratus", "Dua Ratus", "Tiga Ratus", "Empat Ratus", "Lima Ratus", "Enam Ratus", "Tujuh Ratus", "Delapan Ratus", "Sembilan Ratus")

        fun convertTens(n: Long): String {
            if (n < 10) return ones[n.toInt()]
            if (n < 20) return teens[(n - 10).toInt()]
            val ten = n / 10
            val one = n % 10
            return if (one == 0L) {
                tens[ten.toInt()]
            } else {
                "${tens[ten.toInt()]} ${ones[one.toInt()]}"
            }
        }

        fun convertHundreds(n: Long): String {
            if (n == 0L) return ""
            val hundred = n / 100
            val rest = n % 100
            return if (hundred > 0) {
                "${hundreds[hundred.toInt()]} ${convertTens(rest)}".trim()
            } else {
                convertTens(rest)
            }
        }

        fun convertThousand(n: Long): String {
            if (n < 1000) return convertHundreds(n)
            val thousand = n / 1000
            val rest = n % 1000
            return if (rest == 0L) {
                "${convertHundreds(thousand)} Ribu"
            } else {
                "${convertHundreds(thousand)} Ribu ${convertHundreds(rest)}"
            }
        }

        fun convertMillion(n: Long): String {
            if (n < 1000000) return convertThousand(n)
            val million = n / 1000000
            val rest = n % 1000000
            return if (rest == 0L) {
                "${convertHundreds(million)} Juta"
            } else {
                "${convertHundreds(million)} Juta ${convertThousand(rest)}"
            }
        }

        fun convertBillion(n: Long): String {
            if (n < 1000000000) return convertMillion(n)
            val billion = n / 1000000000
            val rest = n % 1000000000
            return if (rest == 0L) {
                "${convertHundreds(billion)} Miliar"
            } else {
                "${convertHundreds(billion)} Miliar ${convertMillion(rest)}"
            }
        }

        return convertBillion(number)
    }

    fun formatNumberWithSeparator(number: Int): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }
}
