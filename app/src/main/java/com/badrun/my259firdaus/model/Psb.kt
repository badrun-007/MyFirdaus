package com.badrun.my259firdaus.model

import java.io.Serializable

data class Psb(
    val nama_santri: String,
    val nik_santri: String,
    val nisn_santri: String,
    val tempatlahir_santri: String,
    val tanggallahir_santri: String,
    val anakke_santri: String,
    val saudarake_santri: String,
    val gender: String,
    val alamat: String,
    val sekolah_asal: String,
    val alamat_sekolah_asal: String,
    val nama_ayah: String,
    val nik_ayah: String,
    val tempatlahir_ayah: String,
    val tanggallahir_ayah: String,
    val nomor_hp_ayah: String,
    val pendidikan_ayah: String,
    val pekerjaan_ayah: String,
    val penghasilan_ayah: String,
    val alamat_ayah: String,
    val nama_ibu: String,
    val nik_ibu: String,
    val tempatlahir_ibu: String,
    val tanggallahir_ibu: String,
    val nomor_hp_ibu: String,
    val pendidikan_ibu: String,
    val pekerjaan_ibu: String,
    val penghasilan_ibu: String,
    val alamat_ibu: String,
    val foto_santri: String,
    val ijazah: String,
    val kartu_keluarga: String,
    val akte_kelahiran: String,
    val kip: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
) : Serializable
