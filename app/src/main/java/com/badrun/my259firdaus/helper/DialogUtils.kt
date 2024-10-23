package com.badrun.my259firdaus.helper

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.badrun.my259firdaus.R

object DialogUtils {
    fun showNoInternetDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Gagal Memuat Data")
        builder.setMessage("Tidak ada koneksi internet. Periksa koneksi internet Anda dan coba lagi.")
        builder.setNegativeButton("Batal", null)

        // Tambahkan gambar ke dalam dialog
        builder.setIcon(R.drawable.no_wifi)

        builder.show()
    }

    fun showServerConnectionFailedDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Gagal Terhubung dengan Server")
        builder.setMessage("Gagal terhubung dengan server. Mohon coba lagi nanti.")
        builder.setNegativeButton("Ok", null)

        // Tambahkan gambar ke dalam dialog
        builder.setIcon(R.drawable.prohibited)

        builder.show()
    }
}