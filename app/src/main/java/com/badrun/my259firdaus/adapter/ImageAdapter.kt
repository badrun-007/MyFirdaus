package com.badrun.my259firdaus.adapter

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.badrun.my259firdaus.R
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class ImageAdapter(private val imageUrls: List<String>,private val isLarge: Boolean = false) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val photoView: PhotoView) : RecyclerView.ViewHolder(photoView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val width = if (isLarge) ViewGroup.LayoutParams.MATCH_PARENT else 250
        val height = if (isLarge) 700 else 200

        val photoView = PhotoView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(width, height)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(8, 8, 8, 8)
        }
        return ImageViewHolder(photoView)
    }

    override fun getItemCount(): Int = imageUrls.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imageUrls[position])
            .placeholder(R.drawable.errorimage) // Pastikan ini ada
            .into(holder.photoView)

        holder.photoView.setOnClickListener {
            // Ketika gambar diklik, tampilkan gambar dengan ukuran penuh menggunakan ViewPager2
            showFullScreenImage(holder.itemView.context, imageUrls, position)
        }
    }

    // Fungsi untuk menampilkan gambar dalam ukuran penuh menggunakan Dialog
    private fun showFullScreenImage(context: Context, imageUrls: List<String>, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_full_screen_image)

        // Menambahkan ViewPager2 ke dalam dialog
        val viewPager: ViewPager2 = dialog.findViewById(R.id.viewPager)
        val pagerAdapter = ImageViewPagerAdapter(context, imageUrls)

        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(position, false) // Set posisi gambar yang dipilih

        dialog.show()
    }
}





