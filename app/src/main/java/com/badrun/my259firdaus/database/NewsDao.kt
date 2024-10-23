package com.badrun.my259firdaus.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.badrun.my259firdaus.database.NewsEntity

@Dao
interface NewsDao {

    @Query("SELECT * FROM news ")
    fun getNews(): LiveData<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNews(news: List<NewsEntity>)

    @Update
    fun updateNews(news: NewsEntity)

    @Query("DELETE FROM news")
    fun deleteAll()


}