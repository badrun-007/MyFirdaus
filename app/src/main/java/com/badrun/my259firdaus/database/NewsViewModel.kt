package com.badrun.my259firdaus.database


import androidx.lifecycle.ViewModel
import com.badrun.my259firdaus.remote.NewsRepository

class NewsViewModel (private val newsRepository: NewsRepository) : ViewModel() {
    fun getHeadlineNews() = newsRepository.getHeadlineNews()

}