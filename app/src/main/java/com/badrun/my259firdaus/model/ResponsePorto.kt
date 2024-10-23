package com.badrun.my259firdaus.model

import java.io.Serializable

data class ResponsePorto(
    val code: Int,
    val message: String,
    val data: Psb
) : Serializable
