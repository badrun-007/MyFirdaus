package com.badrun.my259firdaus.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.badrun.my259firdaus.model.SuratEntity
import com.badrun.my259firdaus.remote.SuratRepository

class SuratViewModel (private val suratRepository: SuratRepository) : ViewModel() {
    fun getHeadlineSurat() = suratRepository.getHeadlineSurat()

}