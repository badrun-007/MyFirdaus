package com.badrun.my259firdaus.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Notification::class], version = 1)
abstract class NotifDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: NotifDatabase? = null

        fun getDatabase(context: Context): NotifDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotifDatabase::class.java,
                    "notification_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}