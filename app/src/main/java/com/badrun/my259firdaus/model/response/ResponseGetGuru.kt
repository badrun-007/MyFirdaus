package com.badrun.my259firdaus.model.response

class ResponseGetGuru (
    val id: Int,
    val nama: String,
    val matpels: List<MatpelStatus>
) {
    data class MatpelStatus(
        val matpel: String,
        val kelas: String,
        val sudah_ada_materi: Boolean
    )
}

