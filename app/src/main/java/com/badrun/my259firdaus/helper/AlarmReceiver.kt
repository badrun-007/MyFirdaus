package com.badrun.my259firdaus.helper


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.R
import java.util.UUID
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: return
        Log.e("BDR", "onReceive: $prayerName", )
        showPrayerNotification(context, prayerName)
    }

    private fun showPrayerNotification(context: Context?, prayerName: String) {
        context ?: return
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.let {
            createNotificationChannel(context)
            val notificationBuilder = NotificationCompat.Builder(context, "notif_adzan")
                .setSmallIcon(R.drawable.ppi259)
                .setContentTitle("Waktu Sholat $prayerName") // Menggunakan judul notifikasi sesuai waktu sholat
                .setContentText("Saatnya sholat $prayerName Ikhawatu Iman") // Menggunakan teks notifikasi yang disertakan
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntent(context))

            playAdzan(context)

            it.notify(UUID.randomUUID().hashCode(), notificationBuilder.build())
        }
    }

    private fun playAdzan(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.adzan)
        mediaPlayer.start()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Shalat Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("notif_adzan", channelName, importance)
            val notificationManager = context. getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}