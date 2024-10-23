package com.badrun.my259firdaus.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.model.Payment
import java.text.NumberFormat
import java.util.Locale

class PaymentAdapter (private val paymentList: List<Payment>, private val onPaymentClick: (Payment) -> Unit) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_payment, parent, false)
        return PaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = paymentList[position]
        
        holder.orderIdTextView.text = "Order Id : "+payment.orderId
        holder.statusTextView.text = "Status Pembayaran : "+payment.status

        val cleanedAmount = removeDecimalAndFormat(payment.grossAmount)
        val formattedGroos = formatNumberWithSeparator(cleanedAmount.toInt())
        val dendaTerGroos = numberToWords(cleanedAmount.toLong())

        holder.grossAmountTextView.text = "Jumlah Yang Dibayar : RP $formattedGroos,00 \n($dendaTerGroos Rupiah) "
        holder.paymentType.text = "Tipe Pembayaran : "+payment.paymentType
        holder.orderTime.text = "Time Order : "+payment.timeOrder
        holder.expiryOrder.text = "Expiry Order : "+payment.expiryTime

        holder.card.setOnClickListener {
            onPaymentClick(payment)
        }
    }

    override fun getItemCount() = paymentList.size

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val grossAmountTextView: TextView = itemView.findViewById(R.id.grossAmountTextView)
        val paymentType : TextView = itemView.findViewById(R.id.paymentTypeTextView)
        val orderTime : TextView = itemView.findViewById(R.id.timeOrderTextView)
        val expiryOrder : TextView = itemView.findViewById(R.id.expireOrderTextView)
        val card : CardView = itemView.findViewById(R.id.layoutCardPayment)
    }

    fun removeDecimalAndFormat(amountString: String): String {
        // Membagi string pada titik desimal dan mengambil bagian pertama
        val parts = amountString.split(".")
        val integerPart = parts[0]
        return integerPart
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