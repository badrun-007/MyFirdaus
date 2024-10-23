package com.badrun.my259firdaus.helper


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.activity.NotificationActivity
import com.badrun.my259firdaus.activity.PembayaranDetailActivity
import com.badrun.my259firdaus.activity.PerpustakaanActivity
import com.badrun.my259firdaus.database.NotifDatabase
import com.badrun.my259firdaus.database.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationRepository: NotificationRepository

    override fun onCreate() {
        super.onCreate()
        val database = NotifDatabase.getDatabase(this)
        notificationRepository = NotificationRepository(database.notificationDao())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            val type = data["type"]

            // Memilih tindakan berdasarkan type
            when (type) {
                "payment_notification" -> handleDataPayload(data)
                "move_notification" -> {
                    val title = "Segera Lakukan Pembayaran Denda Sodara"
                    val body = "Karena Telat ${data["hari"]} Hari, Maka Didenda Sebesar RP ${data["denda"]}"
                    moveActivity(title, body, data)
                    saveNotificationToDatabase(title, body)
                }
                "notif_daftarulang" -> {
                    val stat = data["status"]
                    var title = ""
                    var body = ""

                    if (stat == "Diterima"){
                        title = "Segera melakukan pendaftaran ulang"
                        body = "${data["nama"]}, $stat Disekolah Kami, Pendaftaran Ulang Bisa Klik di Transaksi Pembayaran Pendaftaran"
                    } else {
                        title = "Informasi Pendaftaran"
                        body = "Mohon Maaf Putra/i Bapak Ibu atas nama ${data["nama"]} belum ada kesempatan untuk bergabung Disekolah Kami dikarenakan ${data["alasan"]} , semoga ikhtiar kita diberi kelancaran dan jadi amal ibadah aamiin. \uD83D\uDE4F\uD83C\uDFFB ☺\uFE0F"
                    }
                    moveActivitydua(title,body, data)
                    saveNotificationToDatabase(title, body)
                }
                "notifbuku" -> {

                    val action = data["action"]

                    var title = ""
                    var body = ""
                    if (action == "reservasi"){
                         title = "Buku '${data["judul_buku"]}' sudah tersedia."
                         body = "Silahkan Datang ke Perpustakaan Untuk Mengambilnya."
                    } else if (action == "notif"){
                        title = "Buku '${data["judul_buku"]}' sudah tersedia untuk reservasi."
                        body = "Silahkan masuk ke menu perpustakaan untuk melakukan Reservasi."
                    } else {
                        title = "Buku ${data["judul_buku"]} harus dikembalikan besok."
                        body = "Harap mengembalikan buku tepat waktu untuk menghindari denda."
                    }

                    moveActivityTiga(title,body, data)
                    saveNotificationToDatabase(title, body)
                }

                "notifdenda" -> {
                    val title = "Buku ${data["judul_buku"]} yang Anda pinjam telat ${data["hari"]} Hari"
                    val body = "Karena Anda belum mengembalikannya maka di Denda sebesar RP.${data["denda"]} "
                    moveActivityTiga(title,body, data)
                    saveNotificationToDatabase(title, body)
                }

                "move_notif" -> {
                    val title = "Silahkan Login dengan akun santri tersebut"
                    val body = "Email/Username : ${data["username"]} Password : ${data["pass"]} "
                    moveActivityEmpat(title,body)
                    saveNotificationToDatabase(title, body)
                }
            }
        }

        // Handle notification payload
        remoteMessage.notification?.let {
            val title = it.title ?: "No Title"
            val body = it.body ?: "No Body"
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            sendNotification(title, body, pendingIntent)

            // Simpan notifikasi ke database
            saveNotificationToDatabase(title, body)
        }
    }

    private fun moveActivity(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, PerpustakaanActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("action", data["action"])

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        sendNotification(title, body, pendingIntent)
    }

    private fun moveActivityEmpat(title: String, body: String) {
        val intent = Intent(this, NotificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        sendNotification(title, body, pendingIntent)
    }

    private fun moveActivitydua(title: String, body: String, data: Map<String, String>) {
        val sharedPreferences = getSharedPreferences("daftarUlang", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("action", data["type"])
        editor.putString("status", data["status"])
        editor.apply()

        val intent = Intent(this, PembayaranDetailActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        sendNotification(title, body, pendingIntent)
    }

    private fun moveActivityTiga(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, PerpustakaanActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("action", data["action"])
        intent.putExtra("perpanjang", data["perpanjang"])

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        sendNotification(title, body, pendingIntent)
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Extract data from payload
        val orderId = data["order_id"]
        val status = data["status"]
        val grossAmount = data["gross_amount"]
        val paymentType = data["payment_type"]
        val timeOrder = data["transaction_time"]
        val expiryTime = data["expiry_time"]
        val snapToken = data["snap_token"]

        val dbHelper = DatabaseHelper(this)
        dbHelper.insertOrUpdatePayment(orderId!!, status, grossAmount, paymentType!!, timeOrder!!, expiryTime!!, snapToken!!)

        val intent = Intent(this, PembayaranDetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        sendNotification(
            "Payment Status Updated",
            "Status: $status", pendingIntent
        )

        // Simpan notifikasi ke database
        saveNotificationToDatabase("Payment Status Updated", "Status: $status")
    }

    private fun sendNotification(title: String?, messageBody: String?, pendingIntent: PendingIntent) {
        val channelId = "Midtrans Notifications"
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun saveNotificationToDatabase(title: String, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val notification = Notification(
                title = title,
                message = body,
                isRead = false
            )
            notificationRepository.insert(notification)
        }
    }


}