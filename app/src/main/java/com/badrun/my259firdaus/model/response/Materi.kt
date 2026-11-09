package com.badrun.my259firdaus.model.response

data class Materi(
    val id: Int,
    val guru_id: Int?,
    val matpel_id: Int,
    val kelas_id: Int,
    val judul: String,
    val deskripsi: String,
    val file: String?,
    val link: String?,
    val mulai: String,
    val deadline: String,
    val created_at: String,
    val updated_at: String,
    val file_url: String?,
    val image_urls: List<String>, // Array of image URLs
    val matpel: Matpel,  // Matpel (Mata Pelajaran)
    val kelas: Kelas,    // Kelas
    val guru: Guru,
    val server_time: String
) {
    data class Guru(val name: String)
    data class Matpel(
        val id: Int,
        val name: String,
        val created_at: String?,
        val updated_at: String?
    )

    data class Kelas(
        val id: Int,
        val name: String,
        val level: Int,
        val created_at: String?,
        val updated_at: String?
    )

}
