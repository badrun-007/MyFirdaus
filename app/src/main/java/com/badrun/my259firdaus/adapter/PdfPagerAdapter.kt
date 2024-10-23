package com.badrun.my259firdaus.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import java.io.File
import java.io.IOException

class PdfPagerAdapter(
    private val context: Context,
    pdfFile: File
) : RecyclerView.Adapter<PdfPagerAdapter.PdfViewHolder>() {

    private var pdfRenderer: PdfRenderer? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    private var pageCount: Int = 0

    init {
        try {
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
            pageCount = pdfRenderer?.pageCount ?: 0
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pdf_page, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        pdfRenderer?.let {
            val page = it.openPage(position)
            val metrics = context.resources.displayMetrics
            val screenWidth = metrics.widthPixels
            val pageWidth = page.width
            val pageHeight = page.height

            // Calculate the scale factor to fit the width of the screen
            val scaleFactor = screenWidth.toFloat() / pageWidth.toFloat()
            val scaledHeight = (pageHeight * scaleFactor).toInt()

            val bitmap = Bitmap.createBitmap(screenWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            val rect = Rect(0, 60, screenWidth, scaledHeight -90)
            page.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            holder.imageView.setImageBitmap(bitmap)
            page.close()
        }
    }

    override fun getItemCount(): Int = pageCount

    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.pdf_page_image)
    }

    fun closeRenderer() {
        pdfRenderer?.close()
        parcelFileDescriptor?.close()
    }
}