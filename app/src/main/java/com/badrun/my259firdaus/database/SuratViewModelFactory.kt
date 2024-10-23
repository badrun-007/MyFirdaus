package com.badrun.my259firdaus.database

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.badrun.my259firdaus.helper.Injection
import com.badrun.my259firdaus.helper.InjectionSurat
import com.badrun.my259firdaus.remote.SuratRepository

class SuratViewModelFactory private constructor(private val suratRepository: SuratRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuratViewModel::class.java)) {
            return SuratViewModel(suratRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SuratViewModelFactory? = null
        fun getInstance(context: Context): SuratViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SuratViewModelFactory(InjectionSurat.provideRepository(context))
            }.also { instance = it }
    }

}