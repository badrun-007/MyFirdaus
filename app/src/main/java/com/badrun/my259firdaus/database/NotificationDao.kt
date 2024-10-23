package com.badrun.my259firdaus.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: Notification)

    @Query("SELECT * FROM notifications WHERE isRead = 0")
    suspend fun getUnreadNotifications(): List<Notification>

    @Update
    suspend fun update(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    @Query("SELECT * FROM notifications ORDER BY id DESC")
    suspend fun getAllNotifications(): List<Notification>
}