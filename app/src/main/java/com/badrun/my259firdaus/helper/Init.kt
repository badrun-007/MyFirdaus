package com.badrun.my259firdaus.helper

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.badrun.my259firdaus.R

class Init {

    fun sendNotification(context: Context, title: String, message: String) {
        // Mendapatkan referensi dari NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Membangun notifikasi
        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ppi259)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Memeriksa apakah perangkat mendukung fitur saluran notifikasi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Membuat saluran notifikasi
            val channel = NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "channel_description"
            notificationManager.createNotificationChannel(channel)
        }

        // Menampilkan notifikasi
        notificationManager.notify(0, builder.build())
    }

    fun showCustomAlertDialog(activity:Activity , massage:String) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_custom, null)
        dialogBuilder.setView(dialogView)

        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialogButtonError)

        dialogTitle.text = "Kesalahan Server"
        dialogMessage.text = massage

        val alertDialog = dialogBuilder.create()

        dialogButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}