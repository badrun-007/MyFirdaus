package com.badrun.my259firdaus.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var fabShare: ImageView
    private lateinit var news: NewsEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        init()
        getData()

        fabShare.setOnClickListener {
            shareNews()
        }


    }

    private fun init (){
        judul = findViewById(R.id.tv_news_judul)
        waktu = findViewById(R.id.tv_news_waktu)
        deskr = findViewById(R.id.tv_news_deskripsi)
        img = findViewById(R.id.gambarNews)
        btnBack = findViewById(R.id.img_backNews)
        fabShare = findViewById(R.id.fabShare)

        btnBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun shareNews() {
        val newsId = intent.getStringExtra("newsId")
        val articleTitle = judul.text.toString()
        val articleUrl = "https://my259firdaus.page.link/abjdh" // Deep link URL

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$articleTitle\nBaca selengkapnya: $articleUrl")
            type = "text/plain" // Hanya teks
        }

        sendIntent.setPackage("com.whatsapp") // Arahkan ke WhatsApp
        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(sendIntent)
        } else {
            Toast.makeText(this, "WhatsApp tidak terinstal", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData(){

        val data = intent.getStringExtra("news")
        news = Gson().fromJson(data,NewsEntity::class.java)

        judul.text = news.judul
        val tnggl = news.updated_at!!.substring(0,10)
        val jam = news.updated_at!!.substring(11,19)
        waktu.text = "$tnggl ($jam)"
        deskr.text = news.deskripsi

        val linkImg = "http://${ApiConfig.iplink.ip}/storage/news/${news.image}"
        Picasso.get()
            .load(linkImg)
            .placeholder(R.drawable.progress_animation)
            .into(img)

    }

    private fun openMyFirdausApp() {
        val appPackage = "com.badrun.myfirdaus"
        val intent = packageManager.getLaunchIntentForPackage(appPackage)

        if (intent != null) {
            // Jika aplikasi terinstal, buka aplikasi
            startActivity(intent)
        } else {
            // Jika aplikasi belum terinstal, buka Play Store
            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.badrun.my259firdaus"))
            startActivity(playStoreIntent)
        }
    }
}