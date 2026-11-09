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
import android.os.Handler
import android.os.Looper
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class ExamActivity : AppCompatActivity() {

    private lateinit var button : Button

    private lateinit var bannerAd : AdView



    private val handler = Handler(Looper.getMainLooper())
    private var isDialogShowing = false

    private var isSuspiciousDetected = false
    private var isBlocked = false
    private var blockedUntil: Long = 0L

    private val browserPackages = listOf(
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
        "com.google.android.googlequicksearchbox" to "Google",
        "com.google.android.apps.bard" to "Google Gemini",
        "com.google.android.apps.gemini" to "Google Gemini",
        "com.x.ai.grok" to "Grok AI",
        "com.perplexity.ai" to "Perplexity AI",
        "com.microsoft.copilot" to "Microsoft Copilot",
        "com.swiftkey.swiftkeyconfigurator" to "SwiftKey AI",
        "com.google.android.googlequicksearchbox" to "Google",
        "com.google.android.apps.googleassistant" to "Google Assistant",

        "com.amazon.dee.app" to "Amazon Alexa",

        "com.quora.android" to "Quora",
        "com.reddit.frontpage" to "Reddit",
        "org.wikipedia" to "Wikipedia",

        "com.adobe.scan.android" to "Adobe Scan",
        "com.microsoft.office.officelens" to "Office Lens",

        "com.vivaldi.browser" to "Vivaldi",
        "com.duckduckgo.mobile.android" to "DuckDuckGo",
        "com.ecosia.android" to "Ecosia"

        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        bannerAd = findViewById(R.id.adBannerView4)
        val adRequestSatu = AdRequest.Builder().build()
        bannerAd.loadAd(adRequestSatu)


        button = findViewById(R.id.btn_masuk_exam)
        // Periksa dan minta izin jika diperlukan
        requestUsageStatsPermission()

        /*val usageStatsMan = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val usageEvents = usageStatsMan.queryEvents(time - 3 * 60 * 60 * 1000, time) // Ambil event dalam 3 Jam terakhir
        val event = UsageEvents.Event()*/
        button.setOnClickListener {
            startActivity(Intent(this, LoginExamActivity::class.java))
        }
    }

    private fun showDialog(msg: String) {
        AlertDialog.Builder(this)
            .setTitle(msg)
            .setIcon(R.drawable.forbidden)
            .setMessage(
                "Untuk melanjutkan ujian, aplikasi yang terdeteksi harus ditutup terlebih dahulu.\n\n" +
                        "Setelah aplikasi ditutup, Anda tetap harus menunggu selama 2 menit sebelum dapat melanjutkan."
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                isDialogShowing = false
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
            button.isEnabled = false
            // Jika izin belum diberikan, arahkan pengguna ke pengaturan
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            finish()
        } else {
            button.isEnabled = true
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

    fun getTopApp(context: Context): String? {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 60 * 5,
            time
        )

        if (stats.isNullOrEmpty()) return null

        val recentApp = stats.maxByOrNull { it.lastTimeUsed }
        return recentApp?.packageName
    }

    fun isBrowserRecentlyUsed(
        context: Context,
        browserPackages: List<Pair<String, String>>
    ): Pair<Boolean, String?> {

        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val events = usm.queryEvents(time - 1000 * 60, time) // ⬅️ 1 menit
        val event = UsageEvents.Event()

        var detectedApp: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(event)

            for ((pkg, name) in browserPackages) {
                if (event.packageName == pkg &&
                    event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
                ) {
                    detectedApp = name
                }
            }
        }

        return Pair(detectedApp != null, detectedApp)
    }

    private fun checkBrowser() {
        val now = System.currentTimeMillis()

        val topApp = getTopApp(this)
        val recent = isBrowserRecentlyUsed(this, browserPackages)

        val isBrowserNow = browserPackages.any { it.first == topApp }
        val isBrowserRecent = recent.first

        val browserDetected = isBrowserNow || isBrowserRecent

        // ==========================================
        // 1. Browser terdeteksi
        // ==========================================
        if (browserDetected) {

            isSuspiciousDetected = true
            isBlocked = false

            button.isEnabled = false
            button.text = "Aplikasi Terdeteksi"

            if (!isDialogShowing) {
                isDialogShowing = true

                val appName = recent.second ?: "Browser"
                showDialog("$appName Terdeteksi !!!")
            }

            return
        }

        // ==========================================
        // 2. Browser sudah tidak terdeteksi
        // ==========================================
        if (isSuspiciousDetected && !isBlocked) {

            isSuspiciousDetected = false
            isBlocked = true

            // BARU MULAI 2 MENIT DI SINI
            blockedUntil = now + (2 * 60 * 1000)

            button.isEnabled = false
        }

        // ==========================================
        // 3. Countdown 2 menit
        // ==========================================
        if (isBlocked) {

            val sisa = ((blockedUntil - now) / 1000)
                .coerceAtLeast(0)

            if (sisa > 0) {

                button.isEnabled = false
                button.text = "Tunggu ${sisa} detik"

            } else {

                isBlocked = false

                button.isEnabled = true
                button.text = "Masuk Ujian"
            }
        }
    }

    private val runnablee = object : Runnable {
        override fun run() {
            checkBrowser()
            handler.postDelayed(this, 2000)
        }
    }

    override fun onResume() {
        super.onResume()

        handler.removeCallbacks(runnablee)
        handler.post(runnablee)
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnablee)
    }
}