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

class LainnyaActivity : AppCompatActivity() {

    private lateinit var rvLainnya : RecyclerView
    private val data by lazy { intent?.getSerializableExtra("lainnya") as kotlin.collections.ArrayList<NewsEntity> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lainnya)

        rvLainnya = findViewById(R.id.rv_news_lainnya_Frag)

        setUpRecycler(data)
        initBtnBack()
    }

    private fun setUpRecycler(data : ArrayList<NewsEntity>){

        val infoAdapter = AdapterNewsFrag(this, arrayListOf())

        // filter data dan set adapter untuk setiap RecyclerView
        val dataInfo = data.filter { it.kategori == "3" }

        infoAdapter.showData(dataInfo as ArrayList<NewsEntity>)

        rvLainnya.apply {
            layoutManager = LinearLayoutManager(this@LainnyaActivity)
            adapter = infoAdapter

        }
    }
    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarLainnya)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }
}