package com.badrun.my259firdaus.activity

import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.helper.MyDeviceAdminReceiver

class ExamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)


        val button = findViewById<Button>(R.id.btn_masuk_exam)
        // Periksa dan minta izin jika diperlukan
        requestUsageStatsPermission()

        val usageStatsMan = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val usageEvents = usageStatsMan.queryEvents(time - 3 * 60 * 60 * 1000, time) // Ambil event dalam 3 Jam terakhir
        val event = UsageEvents.Event()

        val browserPackages = listOf(
            "com.android.chrome" to "Google Chrome",
            "org.mozilla.firefox" to "Mozilla Firefox",
            "com.opera.browser" to "Opera",
            "com.opera.mini.native" to "Opera Mini",
            "com.sec.android.app.sbrowser" to "Samsung Browser",
            "com.microsoft.emmx" to "Microsoft Edge",
            "com.brave.browser" to "Brave",
            "com.heytap.browser" to "Realme Browser",
            "com.mi.globalbrowser" to "Xiomi Browser",
            "com.fly.web.smart.browser" to "Fly Browser",
            "com.duckduckgo.mobile.android" to "DuckDuckGo",
            "com.UCMobile.intl" to "UC Browser",
            "com.mi.globalbrowser.mini" to "Xiomi Browser",
            "com.bf.browser" to "BXE Browser",
            "com.bharat.browser" to "Super Browser",
            "com.daiju.stay" to "Stay Browser",
            "com.kiwibrowser.browser" to "Kiwi Browser",
            "com.yandex.browser" to "Yandex Browser",
            "com.microsoft.emmx" to "Edge Browser",
            "com.quran.kemenag" to "Quran Kemenag",
            "com.walukustudio.almatsurat" to "Al Matsurat",
            "com.openai.chatgpt" to "ChatGpt",
            "com.google.android.googlequicksearchbox" to "Google"
        )

        var browserDetected = false
        var browserActive = false
        var detectedAppName = ""

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            // Cek apakah ada browser yang terdeteksi
            for ((packageName, appName) in browserPackages) {
                if (event.packageName == packageName) {
                    if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        Log.e("BDR", "$appName moved to foreground")
                        browserDetected = true
                        detectedAppName = appName
                    } else if (event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        Log.e("BDR", "$appName moved to background")
                        browserDetected = false // Browser ditutup, jadi bisa mengaktifkan tombol
                        button.isEnabled = true // Aktifkan tombol jika browser berpindah ke background
                    }
                    break // Hentikan loop setelah event browser terdeteksi
                }
            }
        }

        // Jika salah satu browser terdeteksi, tampilkan dialog dan nonaktifkan tombol
        if (browserDetected) {
            showDialog("$detectedAppName Terdeteksi !!!")
            button.isEnabled = false  // Nonaktifkan tombol "Masuk"
        } else {
            // Cek jika tidak ada event browser dalam 1 menit
            val checkTime = System.currentTimeMillis()
            val usageEventsAfterDetection = usageStatsMan.queryEvents(checkTime - 1000 * 60, checkTime)
            var browserStillActive = false

            while (usageEventsAfterDetection.hasNextEvent()) {
                usageEventsAfterDetection.getNextEvent(event)

                // Periksa setiap paket browser
                for ((packageName, appName) in browserPackages) {
                    if (event.packageName == packageName) {
                        browserStillActive = true // Browser masih aktif
                        break // Hentikan loop setelah browser terdeteksi
                    }
                }
            }

            // Jika browser sudah tidak aktif, ubah browserDetected menjadi false
            if (!browserStillActive) {
                browserDetected = false
                // Anda bisa menampilkan kembali tombol atau melakukan tindakan lainnya
                button.isEnabled = true // Aktifkan tombol "Masuk" kembali jika diinginkan
            }
        }

        button.setOnClickListener {
            startActivity(Intent(this, LoginExamActivity::class.java))
        }
    }

    private fun showDialog(msg:String) {
        AlertDialog.Builder(this)
            .setTitle(msg)
            .setIcon(R.drawable.forbidden)
            .setMessage("Untuk melanjutkan, Harap Tutup Dulu Aplikasi Anda, Belajar Jujurlah!!!")
            .setPositiveButton("Keluar") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun checkUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        if (!checkUsageStatsPermission()) {

            // Jika izin belum diberikan, arahkan pengguna ke pengaturan
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            finish()
        } else {
            Log.d("Permission", "Izin sudah diberikan, tidak perlu meminta lagi")
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            // Jika aplikasi kehilangan fokus (user menekan Home)
            finish()
        }
    }
}