package com.badrun.my259firdaus.helper

import android.content.Context
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.SuratDatabase
import com.badrun.my259firdaus.remote.SuratRepository

object InjectionSurat {
    fun provideRepository(context: Context): SuratRepository {
        val apiService = ApiConfig.create(context,"https://equran.id/")
        val database = SuratDatabase.getInstance(context)
        val dao = database.suratDao()
        val appExecutors = AppExec()
        return SuratRepository.getInstance(apiService, dao, appExecutors, context)
    }
}