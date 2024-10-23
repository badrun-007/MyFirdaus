package com.badrun.my259firdaus.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.badrun.my259firdaus.model.SuratEntity

@Dao
interface SuratDao {
    @Query("SELECT * FROM surat ")
    fun getSurat(): LiveData<List<SuratEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSurat(news: List<SuratEntity>)

    @Query("DELETE FROM surat")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM surat")
    fun countSurat(): Int



}