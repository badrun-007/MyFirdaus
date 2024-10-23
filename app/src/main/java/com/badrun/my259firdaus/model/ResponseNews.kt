package com.badrun.my259firdaus.model

import com.badrun.my259firdaus.database.NewsEntity


data class ResponseNews (
    val code : Int?,
    val message:String?,
    val news: ArrayList<NewsEntity>?
) : java.io.Serializable