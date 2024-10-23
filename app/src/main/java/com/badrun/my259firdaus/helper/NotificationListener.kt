package com.badrun.my259firdaus.helper

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.badrun.my259firdaus.database.NotifDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private lateinit var notificationRepository: NotificationRepository

    override fun onCreate() {
        super.onCreate()
        val database = NotifDatabase.getDatabase(this)
        notificationRepository = NotificationRepository(database.notificationDao())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d("NotificationListener", "Notification posted from: ${sbn.packageName}")
        saveNotificationToDatabase(sbn)
    }

    private fun saveNotificationToDatabase(sbn: StatusBarNotification) {
        // Extract notification details
        val packageName = sbn.packageName
        val title = sbn.notification.extras?.getString(Notification.EXTRA_TITLE)
        val text = sbn.notification.extras?.getString(Notification.EXTRA_TEXT)

        if (title != null && text != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val notification = com.badrun.my259firdaus.database.Notification(
                    title = title,
                    message = text,
                    isRead = false
                )
                notificationRepository.insert(notification)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification when it is removed (optional)
    }
}