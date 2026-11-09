package com.badrun.my259firdaus.model.response

data class TugasJawabanItem(
    val tugas_id: Int,
    val judul: String,
    val deadline: String?,
    val jumlah_jawaban: Int,
    val jawaban_santri: List<JawabanSantri>
)