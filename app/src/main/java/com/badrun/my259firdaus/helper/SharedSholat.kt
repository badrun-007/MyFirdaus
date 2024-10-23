package com.badrun.my259firdaus.helper


import android.content.Context
import android.content.SharedPreferences
import com.badrun.my259firdaus.model.JadwalSholat
import com.google.gson.Gson


class SharedSholat (activity: Context) {
    val user = "data sholat"

    val mypref = "jadwal_sholat"
    val sp : SharedPreferences

    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun deleteShared(){
        val data = sp.edit()
        data.clear()
        data.apply()
    }

    fun setJadwal(value : JadwalSholat){
        val data = Gson().toJson(value, JadwalSholat::class.java)
        sp.edit().putString(user,data).apply()
    }

    fun getJadwal(): JadwalSholat? {
        val data = sp.getString(user, null) ?: return null
        return Gson().fromJson(data, JadwalSholat::class.java)
    }


}