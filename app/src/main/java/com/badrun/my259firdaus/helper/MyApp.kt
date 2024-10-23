package com.badrun.my259firdaus.helper

import android.app.Application
import androidx.room.Room
import com.badrun.my259firdaus.database.NotifDatabase


class MyApp : Application() {
    companion object {
        lateinit var database: NotifDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            NotifDatabase::class.java, "app_database"
        ).build()
    }
}