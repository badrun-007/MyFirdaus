package com.badrun.my259firdaus.model

data class ResponseSurat(val code: Int,
                         val message: String,
                         val data: ArrayList<SuratEntity>)
