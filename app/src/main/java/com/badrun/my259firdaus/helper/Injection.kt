package com.badrun.my259firdaus.helper

import android.content.Context
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.NewsDatabase
import com.badrun.my259firdaus.remote.NewsRepository

object Injection {
    fun provideRepository(context: Context): NewsRepository {
        val apiService = ApiConfig.create(context)
        val database = NewsDatabase.getInstance(context)
        val dao = database.newsDao()
        val appExecutors = AppExec()
        return NewsRepository.getInstance(apiService, dao, appExecutors)
    }
}