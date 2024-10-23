package com.badrun.my259firdaus.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.badrun.my259firdaus.model.Guru
import com.badrun.my259firdaus.model.Orangtua
import com.badrun.my259firdaus.model.Santri
import com.badrun.my259firdaus.model.Tamu
import com.badrun.my259firdaus.model.User
import com.google.gson.Gson

class SharedPref (activity: Activity) {
    val login = "login"
    val user = "data user"

    val mypref = "MAIN_PRF"
    val sp : SharedPreferences

    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun deleteShared(){
        val data = sp.edit()
        data.clear()
        data.apply()
    }

    fun setStatusLogin(value:Boolean){
        sp.edit().putBoolean(login,value).apply()
    }

    fun getStatusLogin() : Boolean{
        return sp.getBoolean(login,false)
    }

    fun setUser(value : User){
        val data = Gson().toJson(value, User::class.java)
        sp.edit().putString(user,data).apply()
    }

    fun getUser(): User? {
        val data = sp.getString(user, null) ?: return null
        return Gson().fromJson(data, User::class.java)
    }

    fun setGuru(value : Guru){
        val data = Gson().toJson(value, Guru::class.java)
        sp.edit().putString("guru",data).apply()
    }

    fun getGuru(): Guru? {
        val data = sp.getString("guru", null) ?: return null
        return Gson().fromJson(data, Guru::class.java)
    }

    fun setSantri(value : Santri){
        val data = Gson().toJson(value, Santri::class.java)
        sp.edit().putString("santri",data).apply()
    }

    fun getSantri(): Santri? {
        val data = sp.getString("santri", null) ?: return null
        return Gson().fromJson(data, Santri::class.java)
    }

    fun setOrangtua(value : Orangtua){
        val data = Gson().toJson(value, Orangtua::class.java)
        sp.edit().putString("orangtua",data).apply()
    }

    fun getOrangtua(): Orangtua? {
        val data = sp.getString("orangtua", null) ?: return null
        return Gson().fromJson(data, Orangtua::class.java)
    }

    fun setTamu(value : Tamu){
        val data = Gson().toJson(value, Tamu::class.java)
        sp.edit().putString("tamu",data).apply()
    }

    fun getTamu(): Tamu? {
        val data = sp.getString("tamu", null) ?: return null
        return Gson().fromJson(data, Tamu::class.java)
    }


}