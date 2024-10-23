package com.badrun.my259firdaus.model

data class ResponseUser (
    val code : Int?,
    val message:String?,
    val data: User
    ) : java.io.Serializable