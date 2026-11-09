package com.badrun.my259firdaus.model.response

data class ResponseJawabanSantri(
    val code: Int,
    val message: String,
    val data: List<TugasJawabanItem>
)

