package com.badrun.my259firdaus.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.NewsDetailActivity
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.NewsEntity
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class AdapterNewsFrag(val activity: Activity, val data:ArrayList<NewsEntity>, private var maxItemCount: Int = Int.MAX_VALUE): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class viewPertama(itemView: View) : RecyclerView.ViewHolder(itemView){

        var judul : TextView
        var img : ImageView
        var layt : CardView

        init {
            judul = itemView.findViewById(R.id.tv_judulNews)
            img = itemView.findViewById(R.id.img_news)
            layt = itemView.findViewById(R.id.layoutNews)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_news,parent,false)
        return viewPertama(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val s = holder as viewPertama
        s.judul.text = data[position].judul
        val imageSoal = "https://${ApiConfig.iplink.ip}/storage/news/"+data[position].image

        Picasso.get()
            .load(imageSoal)
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.errorimage)
            .into(s.img)

        s.layt.setOnClickListener {
            val i = Intent(activity, NewsDetailActivity::class.java)

            val str = Gson().toJson(data[position],NewsEntity::class.java)
            i.putExtra("news",str)
            activity.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return if (data.size > maxItemCount) maxItemCount else data.size
    }

    // Fungsi untuk membatasi jumlah data yang ditampilkan
    fun setMaxItemCount(maxItemCount: Int) {
        this.maxItemCount = maxItemCount
        notifyDataSetChanged()
    }

    fun showData(dataNews : List<NewsEntity>){
        data.clear()
        data.addAll(dataNews)
        notifyDataSetChanged()
    }
}