package com.badrun.my259firdaus.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.badrun.my259firdaus.model.SuratEntity

@Database(entities = [SuratEntity::class], version = 1, exportSchema = false)
abstract class SuratDatabase : RoomDatabase() {
    abstract fun suratDao(): SuratDao

    companion object {
        @Volatile
        private var instance: SuratDatabase? = null

        fun getInstance(context: Context): SuratDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context): SuratDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SuratDatabase::class.java, "Surat.db"
            ).build()
        }
    }

    fun isSuratTableNotEmpty(): Boolean {
        return suratDao().countSurat() > 0
    }
}