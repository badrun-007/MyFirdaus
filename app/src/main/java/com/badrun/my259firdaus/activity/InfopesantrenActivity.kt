package com.badrun.my259firdaus.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.AdapterNewsFrag
import com.badrun.my259firdaus.database.NewsEntity
import com.badrun.my259firdaus.model.News

class InfopesantrenActivity : AppCompatActivity() {

    private lateinit var rvInfo : RecyclerView
    private val data by lazy { intent?.getSerializableExtra("news") as kotlin.collections.List<NewsEntity> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infopesantren)

        rvInfo = findViewById(R.id.rv_news_infopesantren_Frag)
        setUpRecycler(data)
        initBtnBack()
    }

    private fun setUpRecycler(data : List<NewsEntity>){

        val infoAdapter = AdapterNewsFrag(this, arrayListOf())

        // filter data dan set adapter untuk setiap RecyclerView
        val dataInfo = data.filter { it.kategori == "1" }

        infoAdapter.showData(dataInfo as List<NewsEntity>)

        rvInfo.apply {
            layoutManager = LinearLayoutManager(this@InfopesantrenActivity)
            adapter = infoAdapter

        }
    }
    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarDetails)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }
}