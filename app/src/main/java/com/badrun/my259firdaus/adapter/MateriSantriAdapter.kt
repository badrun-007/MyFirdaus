package com.badrun.my259firdaus.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.activity.BacaActivity
import com.badrun.my259firdaus.activity.daring.KirimTugasActivity
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ItemMateriBinding
import com.badrun.my259firdaus.model.response.Materi
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MateriSantriAdapter (private val context: Context) : RecyclerView.Adapter<MateriSantriAdapter.MateriViewHolder>() {

    private val listMateri = mutableListOf<Materi>()

    fun submitList(newList: List<Materi>) {
        listMateri.clear()
        listMateri.addAll(newList)
        notifyDataSetChanged()
    }

    inner class MateriViewHolder(val binding: ItemMateriBinding) : RecyclerView.ViewHolder(binding.root) {
        var currentYouTubePlayer: YouTubePlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMateriBinding.inflate(inflater, parent, false)
        return MateriViewHolder(binding)
    }

    override fun getItemCount(): Int = listMateri.size


    override fun onBindViewHolder(holder: MateriViewHolder, position: Int) {
        val item = listMateri[position]
        with(holder.binding) {

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val waktuMulai = format.parse(item.mulai)
            val waktuSelesai = format.parse(item.deadline)
            val waktuSekarang = format.parse(item.server_time)

            if (waktuSekarang.before(waktuMulai)) {
                tvJudul.text = item.judul
                tvGuruKelas.text = "${item.matpel?.name} - ${item.kelas?.name}"
                tvDeadline.text = "Materi ini bisa dibuka pada: ${formatTanggalWaktu(item.mulai)}"

                tvLinkFile.visibility = View.GONE
                ivThumbnail.visibility = View.GONE
                youtubePlayerView.visibility = View.GONE
                rvImages.visibility = View.GONE
                btnBacaMateri.visibility = View.GONE
                btnKirimTugas.visibility = View.GONE

                return
            }

            tvJudul.text = item.judul
            tvGuruKelas.text = "${item.matpel?.name} - ${item.kelas?.name}"
            tvDeadline.text = "Deadline: ${formatTanggalWaktu(item.deadline)}"
            tvLinkFile.text = item.deskripsi

            // YOUTUBE: Tampilkan thumbnail jika ada
            val videoId = item.link?.let { extractYoutubeVideoId(it) }

            if (videoId.isNullOrBlank()) {
                Log.e("YouTubePlayer", "Invalid video ID: $videoId")
            } else {
                Log.d("YouTubePlayer", "Video ID: $videoId")
            }

            if (!videoId.isNullOrBlank()) {
                ivThumbnail.visibility = View.VISIBLE
                youtubePlayerView.visibility = View.GONE

                val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"
                Glide.with(holder.itemView.context)
                    .load(thumbnailUrl)
                    .into(ivThumbnail)

                ivThumbnail.setOnClickListener {
                    ivThumbnail.visibility = View.GONE
                    youtubePlayerView.visibility = View.VISIBLE

                    holder.currentYouTubePlayer = null

                    // Cek apakah player sudah ada
                    if (holder.currentYouTubePlayer == null) {
                        val lifecycleOwner = holder.itemView.context as? LifecycleOwner
                        lifecycleOwner?.lifecycle?.addObserver(youtubePlayerView)

                        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                holder.currentYouTubePlayer = youTubePlayer
                                youTubePlayer.loadVideo(videoId, 0f)
                            }

                            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                                super.onError(youTubePlayer, error)
                                Log.e("YouTubePlayer", "Error: ${error.name}")

                                // Coba ulangi pemutaran video jika terjadi error
                                if (error == PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST) {
                                    // Coba kembali muat video setelah beberapa detik
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        youTubePlayer.loadVideo(videoId, 0f)
                                    }, 2000)
                                }
                            }
                        })

                    } else {
                        holder.currentYouTubePlayer?.loadVideo(videoId, 0f)
                    }
                }
            } else {
                ivThumbnail.visibility = View.GONE
                youtubePlayerView.visibility = View.GONE
            }

            // GAMBAR
            item.image_urls?.takeIf { it.isNotEmpty() }?.let { images ->
                rvImages.visibility = View.VISIBLE
                rvImages.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = ImageAdapter(images)
                }
            } ?: run {
                rvImages.visibility = View.GONE
            }

            // PDF
            if (!item.file_url.isNullOrBlank()) {
                btnBacaMateri.visibility = View.VISIBLE
                btnBacaMateri.setOnClickListener {
                    btnBacaMateri.isEnabled = false
                    val context = holder.itemView.context
                    val encodedUrl = Uri.encode(item.file_url, ":/-_.!~#^*()")
                    val fileName = Uri.parse(item.file_url).lastPathSegment ?: "materi.pdf"
                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

                    if (file.exists()) {
                        openPdf(context, file)
                        btnBacaMateri.isEnabled = true
                    } else {
                        btnBacaMateri.text = "Mengunduh..."
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val downloadedFile = downloadPdfDirectly(encodedUrl, fileName, context)
                                openPdf(context, downloadedFile)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                btnBacaMateri.isEnabled = true
                                btnBacaMateri.text = "Baca Sekarang"
                            }
                        }
                    }
                }
            } else {
                btnBacaMateri.visibility = View.GONE
            }


            btnKirimTugas.visibility = View.VISIBLE
            btnKirimTugas.isEnabled = false
            btnKirimTugas.alpha = 0.5f

            if (waktuSekarang.after(waktuMulai) && waktuSekarang.before(waktuSelesai)) {
                // Dalam rentang waktu
                btnKirimTugas.isEnabled = true
                btnKirimTugas.alpha = 1f // optional untuk tampilan
                btnKirimTugas.setOnClickListener {
                    val i = Intent(context, KirimTugasActivity::class.java)
                    i.putExtra("idTugas", item.id)
                    context.startActivity(i)
                }
            }
        }
    }

    private fun formatTanggalWaktu(deadline: String?): String {
        if (deadline.isNullOrEmpty()) return "-"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(deadline)

            val hariFormat = SimpleDateFormat("EEEE", Locale("id")) // "Senin"
            val tanggalFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id")) // "12 April 2025"
            val jamFormat = SimpleDateFormat("HH:mm", Locale("id")) // "12:00"

            val hari = hariFormat.format(date!!)
            val tanggal = tanggalFormat.format(date)
            val jam = jamFormat.format(date)

            "$hari, $tanggal (Jam $jam)"
        } catch (e: Exception) {
            "-"
        }
    }


    private fun openPdf(context: Context, file: File) {
        val intent = Intent(context, BacaActivity::class.java)
        intent.putExtra("pdf_file", file.absolutePath)
        context.startActivity(intent)
    }

    private suspend fun downloadPdfDirectly(encodedUrl: String, fileName: String, context: Context): File {
        return withContext(Dispatchers.IO) {
            val url = URL(encodedUrl)
            val connection = url.openConnection()
            connection.connect()

            val inputStream = BufferedInputStream(url.openStream())
            val outputFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            val outputStream = FileOutputStream(outputFile)

            val data = ByteArray(1024)
            var count: Int
            while (inputStream.read(data).also { count = it } != -1) {
                outputStream.write(data, 0, count)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            outputFile
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



