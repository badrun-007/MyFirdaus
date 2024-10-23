package com.badrun.my259firdaus.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.NewsEntity
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var judul : TextView
    private lateinit var deskr : TextView
    private lateinit var waktu : TextView
    private lateinit var img : ImageView
    private lateinit var btnBack : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        init()
        getData()


    }

    private fun init (){
        judul = findViewById(R.id.tv_news_judul)
        waktu = findViewById(R.id.tv_news_waktu)
        deskr = findViewById(R.id.tv_news_deskripsi)
        img = findViewById(R.id.gambarNews)
        btnBack = findViewById(R.id.img_backNews)

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getData(){

        val data = intent.getStringExtra("news")
        val news = Gson().fromJson(data,NewsEntity::class.java)

        judul.text = news.judul
        val tnggl = news.updated_at!!.substring(0,10)
        val jam = news.updated_at.substring(11,19)
        waktu.text = "$tnggl ($jam)"
        deskr.text = news.deskripsi

        val linkImg = "http://${ApiConfig.iplink.ip}/storage/news/${news.image}"
        Picasso.get()
            .load(linkImg)
            .placeholder(R.drawable.progress_animation)
            .into(img)

    }
}