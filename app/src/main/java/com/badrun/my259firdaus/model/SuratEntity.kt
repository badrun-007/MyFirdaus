package com.badrun.my259firdaus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surat")
class SuratEntity (
    @field:ColumnInfo(name = "nomor")
    @field:PrimaryKey
    val nomor: Int,

    @field:ColumnInfo(name = "nama")
    val nama: String,

    @field:ColumnInfo(name = "namaLatin")
    val namaLatin: String,

    @field:ColumnInfo(name = "jumlahAyat")
    val jumlahAyat: Int,

    @field:ColumnInfo(name = "tempatTurun")
    val tempatTurun: String,

    @field:ColumnInfo(name = "arti")
    val arti: String,

    @field:ColumnInfo(name = "deskripsi")
    val deskripsi: String
)