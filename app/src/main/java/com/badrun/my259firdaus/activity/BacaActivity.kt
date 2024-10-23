package com.badrun.my259firdaus.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.PdfPagerAdapter
import java.io.File

class BacaActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pageInfo: TextView
    private lateinit var pageNumberInput: EditText
    private lateinit var gotoPageButton: Button
    private var currentPageIndex: Int = 0
    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baca)

        viewPager = findViewById(R.id.viewPager)
        pageInfo = findViewById(R.id.page_info)
        pageNumberInput = findViewById(R.id.page_number_input)
        gotoPageButton = findViewById(R.id.goto_page_button)

        // Mendapatkan file PDF yang akan dibuka dari intent
        filePath = intent.getStringExtra("pdf_file")

        // Memuat PDF ke ViewPager jika filePath tidak null
        filePath?.let {
            val file = File(it)
            val adapter = PdfPagerAdapter(this, file)
            viewPager.adapter = adapter

            // Menentukan halaman terakhir yang dibaca
            val lastPage = getLastPage(filePath)
            val initialPage = if (lastPage > 0) lastPage else 0
            viewPager.setCurrentItem(initialPage, false)
            updatePageInfo(initialPage + 1, adapter.itemCount)

            // Mengatur callback saat halaman berubah
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // Memperbarui indeks halaman terakhir yang dibaca
                    currentPageIndex = position
                    updatePageInfo(position + 1, adapter.itemCount)
                }
            })

            // Listener untuk tombol gotoPageButton
            gotoPageButton.setOnClickListener {
                val pageNumber = pageNumberInput.text.toString().toIntOrNull()
                if (pageNumber != null && pageNumber in 1..adapter.itemCount) {
                    viewPager.setCurrentItem(pageNumber - 1, false)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Menyimpan halaman terakhir yang dibaca saat keluar dari aktivitas
        saveLastPage(currentPageIndex, filePath) // Menambah 1 ke currentPageIndex
    }

    override fun onResume() {
        super.onResume()
        // Memulihkan halaman terakhir yang dibaca saat masuk kembali ke aktivitas
        filePath?.let {
            val lastPage = getLastPage(filePath)
            viewPager.post {
                viewPager.setCurrentItem(lastPage, false)
                updatePageInfo(lastPage + 1, (viewPager.adapter?.itemCount ?: 0))
            }
        }
    }

    private fun updatePageInfo(currentPage: Int, totalPages: Int) {
        // Memperbarui teks informasi halaman
        pageInfo.text = "$currentPage/$totalPages"
    }

    private fun saveLastPage(pageIndex: Int, filePath: String?) {
        // Menyimpan indeks halaman terakhir ke SharedPreferences
        val sharedPreferences = getSharedPreferences("pdf_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(filePath ?: "last_page", pageIndex).apply()
    }

    private fun getLastPage(filePath: String?): Int {
        // Mendapatkan halaman terakhir yang dibaca dari SharedPreferences
        val sharedPreferences = getSharedPreferences("pdf_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(filePath ?: "last_page", 0)
    }
}