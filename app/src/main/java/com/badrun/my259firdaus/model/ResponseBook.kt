package com.badrun.my259firdaus.model

data class ResponseBook (
    val code : Int,
    val message:String,
    val data: List<Buku>
)