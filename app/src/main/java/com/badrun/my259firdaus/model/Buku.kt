package com.badrun.my259firdaus.model

data class Buku(
    val id: Int,
    val judul_buku: String,
    val penulis: String,
    val tahun_terbit: String,
    val penerbit: String,
    val cover_buku: String,
    val genre_buku: String,
    val sub_genre : String,
    val stok_buku: Int,
    val jumlah_buku : Int,
    val isbn: String,
    val harga : String,
    val deskripsi_buku: String,
    val halaman: String,
    val jenis: String,
    val buku: String?,
    val created_at: String,
    val updated_at: String,
    val code_buku: String
) : java.io.Serializable
