package com.badrun.my259firdaus.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.databinding.ItemImageBinding
import com.bumptech.glide.Glide

class ImagePreviewAdapter(
    private val uris: MutableList<Uri>,
    private val onRemove: (Uri) -> Unit
) : RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPreview: ImageView = itemView.findViewById(R.id.imageView)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = uris.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = uris[position]
        Glide.with(holder.itemView.context).load(uri).into(holder.imgPreview)
        holder.btnDelete.setOnClickListener {
            onRemove(uri)
        }
    }
}


