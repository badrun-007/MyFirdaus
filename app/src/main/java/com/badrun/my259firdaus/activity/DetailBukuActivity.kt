package com.badrun.my259firdaus.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.BookmarkUtil
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.Buku
import com.badrun.my259firdaus.model.ResponseCheck
import com.badrun.my259firdaus.model.ResponseJadwalBuku
import com.badrun.my259firdaus.model.ResponseUser
import com.badrun.my259firdaus.model.User
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class DetailBukuActivity : AppCompatActivity() {

    private lateinit var judul : TextView
    private lateinit var penulis : TextView
    private lateinit var isbn : TextView
    private lateinit var kodeBuku : TextView
    private lateinit var bukuKe : TextView
    private lateinit var harga : TextView
    private lateinit var deskripsi : TextView
    private lateinit var genre : TextView
    private lateinit var jenisBuku : TextView
    private lateinit var stokBuku : TextView
    private lateinit var tahunTerbit : TextView
    private lateinit var sisaStok : TextView
    private lateinit var stokTersedia : TextView
    private lateinit var tanggalTersedia : TextView
    private lateinit var cover : ImageView
    private lateinit var btnAction : Button
    private lateinit var btnBack : ImageButton
    private lateinit var btnBookmark : ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var s : SharedPref

    private var tangglTersedia : String = ""

    private lateinit var dataUser : User

    private var codeBukus : String = ""
    private var kode : String = ""

    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null
    private var anggota = ""
    private var tokenDevices = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_buku)

        val data = intent.getSerializableExtra("book") as Buku
        kode = intent.getStringExtra("codeBukus").toString()
        sharedPreferences = getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        judul = findViewById(R.id.book_title_detail)
        isbn = findViewById(R.id.isbn_book_detail)
        kodeBuku = findViewById(R.id.kode_book_detail)
        bukuKe = findViewById(R.id.ke_book_detail)
        harga = findViewById(R.id.harga_book_detail)
        penulis = findViewById(R.id.book_author_detail)
        cover = findViewById(R.id.book_cover_detail)
        tahunTerbit = findViewById(R.id.book_year_publisher_detail)
        genre = findViewById(R.id.book_genre_detail)
        deskripsi = findViewById(R.id.book_description_detail)
        btnAction = findViewById(R.id.btn_pinjam_sekarang)
        btnBack = findViewById(R.id.back_button)
        btnBookmark = findViewById(R.id.bookmark_button)
        stokBuku = findViewById(R.id.stok_buku)
        jenisBuku = findViewById(R.id.jenis_buku)
        sisaStok = findViewById(R.id.sisa)
        stokTersedia = findViewById(R.id.yang_dipinjam)
        tanggalTersedia = findViewById(R.id.tanggal_tersedia)
        s = SharedPref(this)
        dataUser = s.getUser()!!


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("BDR", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            tokenDevices = task.result
        })

        initButtonBookmark(data)

        cekAnggota(object : CekAnggotaCallback {
            override fun onResult(result: String) {
                anggota = result
                btnAction.isEnabled = true
            }
        })

        initButtonAction(data)
        initButtonBookmark(data)
        initData(data)

    }
    private suspend fun getCodeBuku(buku: Buku): String {
        var codeBukus = ""
        try {
            val response = withContext(Dispatchers.IO) {
                ApiConfig.create(this@DetailBukuActivity).checkCodeBuku(buku.isbn).execute()
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

    private fun initData(data:Buku){
        judul.text = data.judul_buku
        penulis.text = data.penulis
        isbn.text = "ISBN : "+data.isbn
        genre.text = "Kategori : "+ data.genre_buku
        jenisBuku.text = "Jenis Buku : "+ data.jenis
        tahunTerbit.text = "Tahun Terbit : "+data.tahun_terbit
        deskripsi.text = "Deskripsi Buku :\n\n" +data.deskripsi_buku

        if (data.jenis == "Offline") {
            harga.visibility = View.VISIBLE
            stokBuku.visibility = View.VISIBLE
            stokTersedia.visibility = View.VISIBLE
            sisaStok.visibility = View.VISIBLE
            kodeBuku.text = "Kode Buku : ${data.code_buku.dropLast(4)}"
            bukuKe.text = "Buku Ke : ${kode.takeLast(1)}"

            val hargaSaparator = formatNumberWithSeparator(data.harga.toInt())
            val numberToWord = numberToWords(data.harga.toLong())
            harga.text = "Harga Buku : Rp $hargaSaparator,00\n($numberToWord Rupiah)"

            stokBuku.text = "Stok Buku : "+data.jumlah_buku
            stokTersedia.text = "Buku yang dipinjam : "+data.stok_buku

            val dipinjam = data.jumlah_buku - data.stok_buku

            sisaStok.text = "Buku yang tersedia : " + dipinjam
            if (dipinjam == 0){
                btnAction.text = "Reservasi Sekarang"
            }

        } else {
            kodeBuku.text = "Kode Buku : ${data.code_buku}"
            bukuKe.visibility = View.GONE
        }

        val link = "https://${ApiConfig.iplink.ip}/storage/buku/"+data.cover_buku
        Glide.with(this).load(link).into(cover)

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initButtonAction(data: Buku) {
        if (data.jenis == "Online") {
            btnAction.text = "Baca Sekarang"
            btnAction.setOnClickListener {
                btnAction.isEnabled = false  // Disable button
                val links = "https://${ApiConfig.iplink.ip}/storage/buku/" + data.buku
                val encodedUrl = Uri.encode(links, ":/-_.!~#^*()")
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), data.buku!!)
                if (file.exists()) {
                    openPdf(file)
                    addBookmark(data)
                    btnAction.isEnabled = true  // Re-enable button
                } else {
                    btnAction.text = "Mengunduh..."
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val downloadedFile = downloadPdfDirectly(encodedUrl, data.buku)
                            openPdf(downloadedFile)
                            addBookmark(data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Handle error case here
                        } finally {
                            btnAction.isEnabled = true  // Re-enable button
                            btnAction.text = "Baca Sekarang"
                        }
                    }
                }
            }
        } else {
            btnAction.text = "Pinjam Sekarang"
            btnAction.setOnClickListener {
                btnAction.isEnabled = false  // Disable button
                when (anggota) {
                    "Anggota" -> {
                        lifecycleScope.launch {
                            val codeBuku = checkPinjamSatu(data).await()

                            if (codeBuku){
                                val pinjamBuku = checkPinjamBuku(dataUser).await()
                                val pinjamBukuDua = checkPinjamBukuDua(data, dataUser).await()

                                if (!pinjamBuku && !pinjamBukuDua) {
                                    showDialogWithMessage("Peminjaman Buku Tidak Bisa", "Anda Sudah Meminjam / Mengajukan Peminjaman Buku Ini & Sudah Meminjam 4 Buku")
                                } else if (!pinjamBuku) {
                                    showDialogWithMessage("Anda Sudah Meminjam 4 Buku", "Sebelum Meminjam Kembali, Silahkan Kembalikan Dulu Buku Yang Anda Pinjam")
                                } else if (!pinjamBukuDua) {
                                    showDialogWithMessage("Peminjaman Buku Tidak Bisa", "Anda Sudah Meminjam Buku Ini")
                                } else {
                                    val intent = Intent(this@DetailBukuActivity, PinjamBukuActivity::class.java)
                                    intent.putExtra("book", data)
                                    intent.putExtra("code_buku", codeBukus)
                                    startActivity(intent)
                                }
                            }  else {
                                lifecycleScope.launch {
                                    val pinjamBuku = checkPinjamBuku(dataUser).await()
                                    val pinjamBukuDua = checkPinjamBukuDua(data, dataUser).await()

                                    Log.e("BDR", "initButtonAction: $pinjamBuku", )
                                    Log.e("BDR", "initButtonAction: $pinjamBukuDua", )

                                    if (!pinjamBuku && !pinjamBukuDua) {
                                        showDialogWithMessage("Peminjaman Buku Tidak Bisa", "Anda Sudah Meminjam / Mengajukan Peminjaman Buku Ini & Sudah Meminjam 4 Buku")
                                    } else if (!pinjamBuku) {
                                        showDialogWithMessage("Anda Sudah Meminjam 4 Buku", "Sebelum Meminjam Kembali, Silahkan Kembalikan Dulu Buku Yang Anda Pinjam")
                                    } else if (!pinjamBukuDua) {
                                        showDialogWithMessage("Peminjaman Buku Tidak Bisa", "Anda Sudah Meminjam Buku Ini")
                                    } else {
                                        val reservasiBuku = checkReservasi(data, dataUser).await()
                                        if (!reservasiBuku) {
                                            showDialogWithMessage("Reservasi", "Anda Sudah Membooking Buku Ini")
                                        } else {
                                            jadwalBuku(data)
                                        }
                                    }
                                    btnAction.isEnabled = true  // Re-enable button
                                }
                            }
                            btnAction.isEnabled = true  // Re-enable button
                        }
                    }
                    "Bukan Anggota" -> {
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.apply {
                            setTitle("Pendaftaran Anggota Perpustakaan")
                            setMessage("Sebelum meminjam buku, Anda harus menjadi Anggota Perpustakaan terlebih dahulu")
                            setPositiveButton("Daftar") { dialog, _ ->
                                val intent = Intent(this@DetailBukuActivity, DaftarPerpusActivity::class.java)
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            setNegativeButton("Kembali") { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                        btnAction.isEnabled = true  // Re-enable button
                    }
                    else -> {
                        showDialogError()
                    }
                }

            }
        }
    }

    private fun jadwalBuku(buku:Buku){
        ApiConfig.create(this).getJadwalBuku(buku.isbn).enqueue(object : Callback<ResponseJadwalBuku>{
            override fun onResponse(
                call: Call<ResponseJadwalBuku>,
                response: Response<ResponseJadwalBuku>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    if (res!!.code == 1) {
                        val data = res.data
                        val format = formatDate(data)
                        showDialog(format, buku, dataUser,data,tokenDevices, res.codeBuku)
                    } else if (res.code == 0) {
                        showDialogWithAction(dataUser.user_id,buku,tokenDevices,"Mau mengaktifkan Notifikasi Reservasi setelah buku tersedia ?","Buku ini sudah direservasi oleh oranglain")
                    }
                } else {
                    showDialogError()
                }
            }

            override fun onFailure(call: Call<ResponseJadwalBuku>, t: Throwable) {
                showDialogError()
            }

        })
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return try {
            val date = inputFormat.parse(dateString)
            if (date != null) {
                "Buku yang anda Pinjam akan Tersedia pada Hari ${outputFormat.format(date)}, Mau Reservasi Buku ?"
            } else {
                "Tanggal tidak valid"
            }
        } catch (e: ParseException) {
            "Tanggal tidak valid"
        }
    }

    private fun showDialog(message: String, buku: Buku, user: User, tanggal: String, hp: String, code:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Informasi Buku")
        builder.setMessage(message)
        builder.setPositiveButton("Iya") { _, _ ->
            val intent = Intent(this@DetailBukuActivity, ReservasiBukuActivity::class.java).apply {
                putExtra("buku", buku)
                putExtra("tanggal", tanggal)
                putExtra("hp", hp)
                putExtra("code_buku",code)
            }
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showDialogWithAction(idUser:Int,buku:Buku, hp:String, message: String, title:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, _ ->
            insertNotifikasiBuku(idUser,buku,hp)
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") {dial, _ ->
            dial.dismiss()
        }
        builder.show()
    }

    private fun insertNotifikasiBuku (idUser:Int, buku: Buku, hp:String){
        ApiConfig.create(this).insertNotifikasiBuku(idUser,buku.judul_buku,buku.isbn,hp).enqueue(object:Callback<ResponseCheck>{
            override fun onResponse(call: Call<ResponseCheck>, response: Response<ResponseCheck>) {
                if (response.isSuccessful){
                    val res = response.body()!!
                    if (res.code == 1){
                        showDialogWithMessage("Notifikasi Berhasil Diaktifkan","Silahkan Tunggu Notifikasi Untuk Melakukan Reservasi.")
                    } else {
                        showDialogWithMessage("Mengaktifkan Notifiksi Tidak Berhasil",res.message)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseCheck>, t: Throwable) {
                showDialogError()
            }

        })
    }

    private fun checkPinjamSatu(buku:Buku):Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
        var booleanPertama = false
        try{
            val response = ApiConfig.create(this@DetailBukuActivity).checkCodeBuku(buku.isbn).execute()

            if (response.isSuccessful){
                val res = response.body()
                booleanPertama = res?.code == 1
                if (res!!.code == 1){
                    codeBukus = res.code_buku.toString()
                }
            } else {
                showDialogError()
            }
        } catch (e : Exception){
            showDialogError()
        }
        booleanPertama
    }

    private suspend fun checkPinjamBuku(user: User): Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
        var booleanSatu = false
        try {
            val responseSatu = ApiConfig.create(this@DetailBukuActivity)
                .checkPinjamBuku(user.user_id)
                .execute()

            if (responseSatu.isSuccessful) {
                val res = responseSatu.body()
                booleanSatu = res?.code == 1
            } else {
                showDialogError()
            }
        } catch (e: Exception) {
            showDialogError()
        }
        booleanSatu
    }

    private suspend fun checkPinjamBukuDua(data: Buku, user: User): Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
        var booleanDua = false
        try {
            val responseDua = ApiConfig.create(this@DetailBukuActivity)
                .checkBuku(user.user_id, data.isbn)
                .execute()

            if (responseDua.isSuccessful) {
                val res = responseDua.body()
                booleanDua = res?.code == 1
            } else {
                showDialogError()
            }
        } catch (e: Exception) {
            showDialogError()
        }
        booleanDua
    }

    private suspend fun checkReservasi(data: Buku, user: User): Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
        var booleanDua = false
        try {
            val responseDua = ApiConfig.create(this@DetailBukuActivity)
                .checkReservasi(user.user_id, data.isbn)
                .execute()

            if (responseDua.isSuccessful) {
                val res = responseDua.body()
                booleanDua = res?.code == 1
            } else {
                showDialogError()
            }
        } catch (e: Exception) {
            showDialogError()
        }
        booleanDua
    }

    private fun showDialogWithMessage(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    interface CekAnggotaCallback {
        fun onResult(result: String)
    }

    private suspend fun downloadPdfDirectly(fileUrl: String, fileName: String): File = withContext(Dispatchers.IO) {
        val url = URL(fileUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        val inputStream = connection.inputStream
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        connection.disconnect()

        Log.d("DetailBukuActivity", "File downloaded: ${file.absolutePath}")
        return@withContext file
    }

    private fun cekAnggota(callback: CekAnggotaCallback) {
                ApiConfig.create(this).waduh(dataUser!!.email).enqueue(object : Callback<ResponseUser> {
            override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                if (response.isSuccessful) {
                    val dataUsers = response.body()
                    if (dataUsers!!.code == 1) {
                        callback.onResult(dataUsers.data.perpustakaan)
                    } else {
                        callback.onResult("")
                    }
                } else {
                    callback.onResult("")
                }
            }

            override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                showDialogError()
                callback.onResult("")
            }
        })
    }

    private fun showDialogError() {
        if (errorDialog == null) {
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.dialog_custom, null)

            // Inisialisasi elemen dalam dialog custom jika diperlukan
            val dismissButton: Button = view.findViewById(R.id.dialogButtonError)
            dismissButton.setOnClickListener {
                errorDialog?.dismiss()
            }

            builder.setView(view)
            builder.setCancelable(true)
            errorDialog = builder.create()
        }
        errorDialog?.show()
    }

    private suspend fun downloadPdf(context: Context, data: Buku, fileUrl: String): File = withContext(Dispatchers.IO) {
        val response = ApiConfig.create(context).downloadBuku(fileUrl).await()
        return@withContext savePdf(response, data.buku.toString())
    }

    private fun savePdf(body: ResponseBody?, fileName: String): File {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        try {
            body?.let {
                val inputStream = it.byteStream()
                val outputStream = FileOutputStream(file)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            }
            Log.d("DetailBukuActivity", "File saved: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("DetailBukuActivity", "Save file error: ${e.message}")
        }
        return file
    }

    private fun openPdf(file: File) {
        Log.d("DetailBukuActivity", "Opening PDF: ${file.absolutePath}")
        if (file.exists()) {
            val intent = Intent(this, BacaActivity::class.java)
            intent.putExtra("pdf_file", file.absolutePath)
            startActivity(intent)
        } else {
            Log.e("DetailBukuActivity", "File not found: ${file.absolutePath}")
        }
    }

    private fun initButtonBookmark(data: Buku) {
        val isBookmarked = BookmarkUtil.isBookmarked(sharedPreferences, data)
        updateBookmarkButton(isBookmarked)

        btnBookmark.setOnClickListener {
            val currentStatus = BookmarkUtil.isBookmarked(sharedPreferences, data)
            val newStatus = !currentStatus
            sharedPreferences.edit().putBoolean(data.id.toString(), newStatus).apply()
            updateBookmarkButton(newStatus)
        }
    }

    private fun addBookmark(data: Buku) {
        sharedPreferences.edit().putBoolean(data.id.toString(), true).apply()
        updateBookmarkButton(true)
    }

    private fun updateBookmarkButton(isBookmarked: Boolean) {
        if (isBookmarked) {
            btnBookmark.setColorFilter(ContextCompat.getColor(this, R.color.Green))
        } else {
            btnBookmark.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
    }

    private fun numberToWords(number: Long): String {
        if (number == 0L) return "Nol"

        val ones = arrayOf("", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan")
        val teens = arrayOf("Sepuluh", "Sebelas", "Dua Belas", "Tiga Belas", "Empat Belas", "Lima Belas", "Enam Belas", "Tujuh Belas", "Delapan Belas", "Sembilan Belas")
        val tens = arrayOf("", "Sepuluh", "Dua Puluh", "Tiga Puluh", "Empat Puluh", "Lima Puluh", "Enam Puluh", "Tujuh Puluh", "Delapan Puluh", "Sembilan Puluh")
        val hundreds = arrayOf("", "Seratus", "Dua Ratus", "Tiga Ratus", "Empat Ratus", "Lima Ratus", "Enam Ratus", "Tujuh Ratus", "Delapan Ratus", "Sembilan Ratus")

        fun convertTens(n: Long): String {
            if (n < 10) return ones[n.toInt()]
            if (n < 20) return teens[(n - 10).toInt()]
            val ten = n / 10
            val one = n % 10
            return if (one == 0L) {
                tens[ten.toInt()]
            } else {
                "${tens[ten.toInt()]} ${ones[one.toInt()]}"
            }
        }

        fun convertHundreds(n: Long): String {
            if (n == 0L) return ""
            val hundred = n / 100
            val rest = n % 100
            return if (hundred > 0) {
                "${hundreds[hundred.toInt()]} ${convertTens(rest)}".trim()
            } else {
                convertTens(rest)
            }
        }

        fun convertThousand(n: Long): String {
            if (n < 1000) return convertHundreds(n)
            val thousand = n / 1000
            val rest = n % 1000
            return if (rest == 0L) {
                "${convertHundreds(thousand)} Ribu"
            } else {
                "${convertHundreds(thousand)} Ribu ${convertHundreds(rest)}"
            }
        }

        fun convertMillion(n: Long): String {
            if (n < 1000000) return convertThousand(n)
            val million = n / 1000000
            val rest = n % 1000000
            return if (rest == 0L) {
                "${convertHundreds(million)} Juta"
            } else {
                "${convertHundreds(million)} Juta ${convertThousand(rest)}"
            }
        }

        fun convertBillion(n: Long): String {
            if (n < 1000000000) return convertMillion(n)
            val billion = n / 1000000000
            val rest = n % 1000000000
            return if (rest == 0L) {
                "${convertHundreds(billion)} Miliar"
            } else {
                "${convertHundreds(billion)} Miliar ${convertMillion(rest)}"
            }
        }

        return convertBillion(number)
    }

    private fun formatNumberWithSeparator(number: Int): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }
}