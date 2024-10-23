package com.badrun.my259firdaus.model

data class ResponseJadwalSholat (
    val code : Int?,
    val status:String?,
    val data: ArrayList<JadwalSholat>?
) : java.io.Serializable