package com.badrun.my259firdaus.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val isRead: Boolean = false
)