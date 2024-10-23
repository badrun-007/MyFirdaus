package com.badrun.my259firdaus.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.DetailBukuActivity
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.NewsEntity
import com.badrun.my259firdaus.model.Buku
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterBuku (private var books: List<Buku>,val activity: Activity) : RecyclerView.Adapter<AdapterBuku.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.book_title)
        val authorTextView: TextView = view.findViewById(R.id.book_author)
        val coverImageView: ImageView = view.findViewById(R.id.book_cover)
        val layoutCard: CardView = view.findViewById(R.id.layoutBuku)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_buku, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val data = books[position]
        holder.titleTextView.text = data.judul_buku
        holder.authorTextView.text = data.penulis
        val link = "https://${ApiConfig.iplink.ip}/storage/buku/"+data.cover_buku
        Glide.with(holder.coverImageView.context).load(link).into(holder.coverImageView)

        holder.layoutCard.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val codeBukus = checkPinjamSatu(data)
                Log.d("AdapterBuku", "Book at position $position has codeBukus: $codeBukus")
                val intent = Intent(activity, DetailBukuActivity::class.java)
                intent.putExtra("book", data)
                intent.putExtra("codeBukus", codeBukus)
                activity.startActivity(intent)
            }
        }
    }

    private suspend fun checkPinjamSatu(buku: Buku): String {
        var codeBukus = ""
        try {
            val response = withContext(Dispatchers.IO) {
                ApiConfig.create(activity).checkCodeBuku(buku.isbn).execute()
            }

            if (response.isSuccessful) {
                val res = response.body()
                if (res!!.code == 1) {
                    Log.e("bdr", "checkPinjamSatu: codeBukus = ${res.code_buku}")
                    codeBukus = res.code_buku.toString()
                }
            } else {
                Log.e("bdr", "checkPinjamSatu: error")
            }
        } catch (e: Exception) {
            Log.e("bdr", "checkPinjamSatu: ${e.message}")
        }
        return codeBukus
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Buku>) {
        books = newBooks
        notifyDataSetChanged()
    }
    fun getBooks() : List<Buku>{
        return books
    }
}