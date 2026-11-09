package com.badrun.my259firdaus.model

data class User(
    val role : Int,
    val id : Int,
    val email : String,
    val user_id : Int,
    val image : String,
    var nama : String,
    val gender : String,
    val perpustakaan : String,
    var kelas : String?
)