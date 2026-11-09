package com.badrun.my259firdaus.activity.daring

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.ImageAdapter
import com.badrun.my259firdaus.adapter.JawabanSantriAdapter
import com.badrun.my259firdaus.model.response.JawabanSantri
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class ListJawabanSantriActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_jawaban_santri)

        val jawabanList = intent.getSerializableExtra("jawaban_santri") as? List<JawabanSantri> ?: emptyList()

        val sortedList = jawabanList.sortedByDescending { it.sudah_kirim == true }

        val recyclerView = findViewById<RecyclerView>(R.id.rvListJawabanSantri)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = JawabanSantriAdapter(sortedList) { jawaban ->
            // Tampilkan dialog detail jawaban
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_jawaban_santri)

            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            val tvNama = dialog.findViewById<TextView>(R.id.tvNamaSantri)
            val youTubePlayerView = dialog.findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
            val rvGambar = dialog.findViewById<RecyclerView>(R.id.rvJawabanSantri)

            tvNama.text = jawaban.nama
            val youtubeLink = jawaban.teks_jawaban

            if (!youtubeLink.isNullOrEmpty()) {
                youTubePlayerView.visibility = View.VISIBLE
                lifecycle.addObserver(youTubePlayerView)

                val videoId = extractYoutubeVideoId(youtubeLink)
                youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        if (videoId != null) {
                            youTubePlayer.cueVideo(videoId, 0f)
                        }
                    }
                })
            }

            if (!jawaban.gambar_jawaban.isNullOrEmpty()) {
                rvGambar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rvGambar.adapter = ImageAdapter(jawaban.gambar_jawaban,isLarge = true)
            }

            dialog.show()
        }
    }

    private fun extractYoutubeVideoId(url: String): String? {
        return try {
            val uri = Uri.parse(url)
            when {
                uri.host?.contains("youtu.be") == true -> {
                    uri.lastPathSegment
                }
                uri.host?.contains("youtube.com") == true -> {
                    uri.getQueryParameter("v")
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}