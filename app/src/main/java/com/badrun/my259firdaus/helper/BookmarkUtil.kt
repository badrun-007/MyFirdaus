package com.badrun.my259firdaus.helper

import android.content.Context
import android.content.SharedPreferences
import com.badrun.my259firdaus.model.Buku

object BookmarkUtil {
    fun addBookmark(sharedPreferences: SharedPreferences, data: Buku) {
        sharedPreferences.edit().putBoolean(data.id.toString(), true).apply()
    }

    fun isBookmarked(sharedPreferences: SharedPreferences, data: Buku): Boolean {
        return sharedPreferences.getBoolean(data.id.toString(), false)
    }
}