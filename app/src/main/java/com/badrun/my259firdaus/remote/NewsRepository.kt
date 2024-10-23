package com.badrun.my259firdaus.remote


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.badrun.my259firdaus.api.ApiService
import com.badrun.my259firdaus.database.NewsDao
import com.badrun.my259firdaus.database.NewsEntity
import com.badrun.my259firdaus.helper.AppExec
import com.badrun.my259firdaus.helper.DialogUtils
import com.badrun.my259firdaus.model.ResponseNews
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao,
    private val appExecutors: AppExec
) {
    private val result = MediatorLiveData<Result<List<NewsEntity>>>()

    fun getHeadlineNews(): LiveData<Result<List<NewsEntity>>> {
        result.value = Result.Loading
        val client = apiService.getNews()
        client.enqueue(object : Callback<ResponseNews> {
            override fun onResponse(call: Call<ResponseNews>, response: Response<ResponseNews>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.news
                    val newsList = ArrayList<NewsEntity>()
                    appExecutors.diskIO.execute {
                        articles?.forEach { article ->
                            val news = NewsEntity(
                                article.id,
                                article.judul,
                                article.kategori,
                                article.deskripsi,
                                article.image,
                                article.created_at,
                                article.updated_at
                            )
                            newsList.add(news)
                        }
                        newsDao.deleteAll()
                        newsDao.insertNews(newsList)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                result.value = Result.Error("Gagal Terhubung Dengan Database")
            }
        })
        val localData = newsDao.getNews()
        result.addSource(localData) { newData: List<NewsEntity> ->
            val sortedNews = newData.sortedByDescending { it.id } //Invers urutan News
            result.value = Result.Success(sortedNews)
        }
        return result
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao,
            appExecutors: AppExec
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDao, appExecutors)
            }.also { instance = it }
    }
}