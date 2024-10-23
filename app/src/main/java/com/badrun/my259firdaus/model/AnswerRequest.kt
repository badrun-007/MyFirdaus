package com.badrun.my259firdaus.model

data class AnswerRequest(
    val transaksi_id: Int,
    val answers: List<UserAnswer>,
    val score: Float // Tambahkan score jika Anda ingin mengirimkan score juga
){
    data class UserAnswer(
        val soal_id: Int,
        val answer: String
    )
}
