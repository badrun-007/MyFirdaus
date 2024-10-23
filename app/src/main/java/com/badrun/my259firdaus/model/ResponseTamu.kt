package com.badrun.my259firdaus.model

data class ResponseTamu (
    val code : Int?,
    val message:String?,
    val data: Tamu
    ) : java.io.Serializable