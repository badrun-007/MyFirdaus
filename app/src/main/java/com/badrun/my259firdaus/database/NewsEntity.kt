package com.badrun.my259firdaus.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "news")
class NewsEntity (
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id:Int?,

    @field:ColumnInfo(name = "judul")
    val judul :String?,

    @field:ColumnInfo(name = "kategori")
    val kategori:String?,

    @field:ColumnInfo(name = "deskripsi")
    val deskripsi:String?,

    @field:ColumnInfo(name = "image")
    val image:String?,

    @field:ColumnInfo(name = "created")
    val created_at:String?,

    @field:ColumnInfo(name = "updated")
    val updated_at:String?,

): Parcelable
