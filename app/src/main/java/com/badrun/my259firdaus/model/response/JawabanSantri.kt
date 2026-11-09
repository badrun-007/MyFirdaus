package com.badrun.my259firdaus.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JawabanSantri(
    val user_id: Int,
    val nama: String,
    val sudah_kirim: Boolean,
    val teks_jawaban: String?,
    val gambar_jawaban: List<String>,
    val waktu_kirim: String?
) : Parcelable