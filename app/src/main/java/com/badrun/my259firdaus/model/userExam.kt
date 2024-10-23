package com.badrun.my259firdaus.model

import java.io.Serializable

data class userExam(
    val id : Int,
    val nama : String,
    val username : String ,
    val class_id:Int,
    val class_name:String,
    val image:String,
    val schedule_id:Int,
    val matpel_id:Int,
    val matpel:String,
    val start_time:String,
    val end_time:String,
    val transaksi_id:Int,
    val token:String
) : Serializable
