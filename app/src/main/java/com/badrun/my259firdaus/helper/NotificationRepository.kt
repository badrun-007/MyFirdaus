package com.badrun.my259firdaus.helper


import com.badrun.my259firdaus.database.Notification
import com.badrun.my259firdaus.database.NotificationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun insert(notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.insert(notification)
        }
    }

    suspend fun getUnreadNotifications(): List<Notification> {
        return withContext(Dispatchers.IO) {
            notificationDao.getUnreadNotifications()
        }
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun getAllNotifications(): List<Notification> {
        return notificationDao.getAllNotifications()
    }
}