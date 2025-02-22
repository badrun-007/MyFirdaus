package com.badrun.my259firdaus.model

data class PeminjamanData(val id: Int,
                          val id_buku: String,
                          val id_user: Int,
                          val isbn : String,
                          val cover : String,
                          val nama_user: String,
                          val judul_buku: String,
                          val penulis: String,
                          val waktu_peminjaman: Int,
                          val tgl_peminjaman: String,
                          val tgl_pengembalian: String,
                          val petugas_perpus: String,
                          val status: String,
                          val denda: Int,
                          val hp: String,
                          val perpanjang : String,
                          val created_at: String,
                          val updated_at: String)
