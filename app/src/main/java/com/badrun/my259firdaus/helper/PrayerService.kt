package com.badrun.my259firdaus.helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.view.ContentInfoCompat.Flags
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.R
import java.util.*

class PrayerService : Service() {

    private lateinit var s: SharedSholat
    private val NOTIFICATION_ID = 12345
    companion object {
        const val ACTION_CANCEL_ALARMS = "com.badrun.my259firdaus.action.CANCEL_ALARMS"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        s = SharedSholat(this)
        val action = intent?.action

        if (action == ACTION_CANCEL_ALARMS) {
            cancelPrayerAlarms(this)
        } else {
            val prayerTimes = getPrayerTimesFromPreferences(this)
            if (prayerTimes.isNotEmpty()) {
                schedulePrayerAlarms(this)
            }
            startForeground(NOTIFICATION_ID, createNotification())
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun schedulePrayerAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val prayerTimes = getPrayerTimesFromPreferences(context)
        val now = Calendar.getInstance()

        var id = 1
        for ((prayerName, prayerTime) in prayerTimes) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", prayerName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, id, alarmIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, prayerTime.first)
                set(Calendar.MINUTE, prayerTime.second)
                set(Calendar.SECOND, 0)
                if (before(now)) {
                    add(Calendar.DAY_OF_MONTH, 1)  // Tambahkan sehari jika waktu sudah lewat
                }
            }

            alarmManager.setRepeating(
                AlarmManager.RTC,
                alarmTime.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent)

            id++
        }
    }

    fun cancelPrayerAlarms(context: Context) {
        val prayerTimes = getPrayerTimesFromPreferences(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var id = 1
        for ((prayerName, _) in prayerTimes) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", prayerName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, id, alarmIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.cancel(pendingIntent)
            id++
        }
    }

    private fun getPrayerTimesFromPreferences(context: Context): Map<String, Pair<Int, Int>> {
        val jadwal = s.getJadwal() // Get the Jadwal object, may be null
        val timings = jadwal?.timings // Access timings if jadwal is not null

        return if (timings != null) {
            val fajrTime = timings.Fajr.substring(0, 5)
            val dhuhrTime = timings.Dhuhr.substring(0, 5)
            val asrTime = timings.Asr.substring(0, 5)
            val maghribTime = timings.Maghrib.substring(0, 5)
            val ishaTime = timings.Isha.substring(0, 5)

            // Parse and return the prayer times if available
            mapOf(
                "Shubuh" to parseTime(fajrTime),
                "Dzuhur" to parseTime(dhuhrTime),
                "Ashar" to parseTime(asrTime),
                "Maghrib" to parseTime(maghribTime),
                "Isya" to parseTime(ishaTime)
            )
        } else {
            // Return default times or empty map if timings are not available
            mapOf(
                "Shubuh" to Pair(5, 0),   // Default time or your chosen default
                "Dzuhur" to Pair(12, 0),
                "Ashar" to Pair(15, 0),
                "Maghrib" to Pair(18, 0),
                "Isya" to Pair(20, 0)
            )
        }
    }

    private fun parseTime(timeString: String): Pair<Int, Int> {
        val parts = timeString.split(":")
        return if (parts.size == 2) {
            Pair(parts[0].toInt(), parts[1].toInt())
        } else {
            Pair(0, 0) // Default time if parsing fails
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

        return NotificationCompat.Builder(this, "notif_adzan")
            .setContentTitle("My 259 Firdaus")
            .setContentText("Standby For Adzan")
            .setSmallIcon(R.drawable.ppi259)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Menghentikan layanan foreground saat layanan dihentikan
        stopForeground(true)
    }

}