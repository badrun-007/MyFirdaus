package com.badrun.my259firdaus.model

data class Ayat(
    val nomor: Int,
    val nama: String,
    val namaLatin: String,
    val jumlahAyat: Int,
    val tempatTurun: String,
    val arti: String,
    val deskripsi: String,
    val audioFull: Audio,
    val ayat: ArrayList<DataAyat>

) : java.io.Serializable {

    data class Audio(
        val _01: String,
        val _02: String,
        val _03: String,
        val _04: String,
        val _05: String)

    data class DataAyat(
        val nomorAyat: Int,
        val teksArab: String,
        val teksLatin: String,
        val teksIndonesia: String,
        val audio: Audio)

}

