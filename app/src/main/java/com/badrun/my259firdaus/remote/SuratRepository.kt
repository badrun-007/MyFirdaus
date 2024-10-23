package com.badrun.my259firdaus.remote


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.api.ApiService
import com.badrun.my259firdaus.database.SuratDao
import com.badrun.my259firdaus.helper.AppExec
import com.badrun.my259firdaus.model.ResponseSurat
import com.badrun.my259firdaus.model.SuratEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuratRepository private constructor(
    private val apiService: ApiService,
    private val suratDao: SuratDao,
    private val appExecutors: AppExec,
    val con :Context
) {
    private val result = MediatorLiveData<Result<List<SuratEntity>>>()

    fun getHeadlineSurat(): LiveData<Result<List<SuratEntity>>> {
        result.value = Result.Loading

        val localData = suratDao.getSurat()
        if (result.hasActiveObservers()) {
            result.removeSource(localData)
        }

        result.addSource(localData) { localSuratList: List<SuratEntity> ->
            if (localSuratList.isNotEmpty()) {
                result.value = Result.Success(localSuratList)
            } else {
                val client = ApiConfig.create(con, "https://equran.id/").getSurah()
                client.enqueue(object : Callback<ResponseSurat> {
                    override fun onResponse(call: Call<ResponseSurat>, response: Response<ResponseSurat>) {
                        if (response.isSuccessful) {
                            val articles = response.body()?.data
                            val suratList = ArrayList<SuratEntity>()
                            appExecutors.diskIO.execute {
                                articles?.forEach { article ->
                                    val surat = SuratEntity(
                                        article.nomor,
                                        article.nama,
                                        article.namaLatin,
                                        article.jumlahAyat,
                                        article.tempatTurun,
                                        article.arti,
                                        article.deskripsi
                                    )
                                    suratList.add(surat)
                                }

                                Log.e("BDR", "onResponse: Pengambilan data ke API", )

                                suratDao.deleteAll()
                                suratDao.insertSurat(suratList)

                                result.postValue(Result.Success(suratList))
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseSurat>, t: Throwable) {
                        result.postValue(Result.Error("Gagal Terhubung Dengan Database"))
                    }
                })
            }
        }

        return result
    }

    companion object {
        @Volatile
        private var instance: SuratRepository? = null
        fun getInstance(
            apiService: ApiService,
            suratDao: SuratDao,
            appExecutors: AppExec,
            con : Context
        ): SuratRepository =
            instance ?: synchronized(this) {
                instance ?: SuratRepository(apiService, suratDao, appExecutors, con)
            }.also { instance = it }
    }
}