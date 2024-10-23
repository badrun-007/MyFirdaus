package com.badrun.my259firdaus.model

import java.io.Serializable

data class JadwalExam(val id:Int, val kelas:String, val matpel:String, val start_time :String, val end_time:String, val token:String) :Serializable
