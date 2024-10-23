package com.badrun.my259firdaus.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.badrun.my259firdaus.database.NewsEntity

class SharedNewsViewModel : ViewModel() {
    private val _headlineNews = MutableLiveData<List<NewsEntity>>()
    val headlineNews: LiveData<List<NewsEntity>> get() = _headlineNews

    fun setHeadlineNews(news: List<NewsEntity>) {
        val sortedNews = news.sortedByDescending { it.id } //Invers urutan News
        _headlineNews.value = sortedNews
    }
}