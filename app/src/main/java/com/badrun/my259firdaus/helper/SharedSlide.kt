package com.badrun.my259firdaus.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.badrun.my259firdaus.model.Slide
import com.google.gson.Gson

class SharedSlide (activity:Activity) {
    val user = "data sholat"

    val mypref = "jadwal_sholat"
    val sp : SharedPreferences

    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun deleteSharedSlide(){
        val data = sp.edit()
        data.clear()
        data.apply()
    }

    fun setSlide(value : Slide){
        val data = Gson().toJson(value, Slide::class.java)
        sp.edit().putString(user,data).apply()
    }

    fun getSlide(): Slide? {
        val data = sp.getString(user, null) ?: return null
        return Gson().fromJson(data, Slide::class.java)
    }
}