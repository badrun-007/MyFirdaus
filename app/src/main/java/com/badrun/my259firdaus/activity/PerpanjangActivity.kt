package com.badrun.my259firdaus.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.Buku
import com.badrun.my259firdaus.model.ResponseCheck
import com.badrun.my259firdaus.model.ResponseMinjam
import com.badrun.my259firdaus.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class PerpanjangActivity : AppCompatActivity() {

    private lateinit var nama : EditText
    private lateinit var judulBuku : EditText
    private lateinit var penulisBuku : EditText
    private lateinit var waktuPeminjaman : EditText
    private lateinit var waktuPerpanjangan : EditText
    private lateinit var waktuPengembalian : EditText
    private lateinit var dataUser : SharedPref
    private lateinit var btnKirim : Button
    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perpanjang)

        initBtnBack()
        nama = findViewById(R.id.nama_anggota_formulir)
        judulBuku = findViewById(R.id.judul_buku_formulir)
        penulisBuku = findViewById(R.id.penulis_buku_formulir)
        waktuPeminjaman = findViewById(R.id.waktu_peminjaman)
        waktuPerpanjangan = findViewById(R.id.peminjaman_buku_formulir)
        waktuPengembalian = findViewById(R.id.waktu_pengembalian)
        btnKirim = findViewById(R.id.btn_pinjam_buku)

        dataUser = SharedPref(this)

        val user = dataUser.getUser()

        val idPeminjaman = intent.getIntExtra("id_peminjaman", 0)
        val penulis = intent.getStringExtra("penulis")
        val judul = intent.getStringExtra("judul")
        val tanggalPeminjaman = intent.getStringExtra("tgl_peminjaman")
        val tanggalPengembalian = intent.getStringExtra("tgl_pengembalian")

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'Jam' HH:mm:ss", Locale("id", "ID"))

        var formattedPeminjaman: String
        var formattedPengembalian: String

        try {
            // Parse tanggal dari string awal
            val datePeminjaman = inputFormat.parse(tanggalPeminjaman.toString())
            val datePengembalian = inputFormat.parse(tanggalPengembalian.toString())

            // Format ke string dengan format baru
            formattedPeminjaman = outputFormat.format(datePeminjaman!!)
            formattedPengembalian = outputFormat.format(datePengembalian!!)
        } catch (e: Exception) {
            e.printStackTrace()
            formattedPeminjaman = "Format tanggal tidak valid"
            formattedPengembalian = "Format tanggal tidak valid"
        }

        nama.setText(user!!.nama)
        judulBuku.setText(judul)
        penulisBuku.setText(penulis)
        waktuPeminjaman.setText(formattedPeminjaman)
        waktuPengembalian.setText(formattedPengembalian)

        waktuPerpanjangan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validasi panjang teks
                if (s.isNullOrEmpty()) {
                    waktuPerpanjangan.error = "Masukkan angka antara 1 hingga 7"
                } else {
                    try {
                        val input = s.toString().toInt()
                        if (input < 1 || input > 7) {
                            waktuPerpanjangan.error = "Maksimal Peminjaman 7 Hari"
                        } else {
                            waktuPerpanjangan.error = null // Reset error jika valid
                        }
                    } catch (e: NumberFormatException) {
                        waktuPerpanjangan.error = "Masukkan angka yang valid"
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // Tidak ada implementasi yang diperlukan di sini
            }
        })

        btnKirim.setOnClickListener {
            btnKirim.isEnabled = false
            showLoadingDialog()
            sendData(idPeminjaman)
        }
    }


    private fun sendData(idPeminjaman:Int){
        val waktu = waktuPerpanjangan.text.toString().toInt()

        ApiConfig.create(this).perpanjangPeminjaman(idPeminjaman,waktu).enqueue(object : Callback<ResponseCheck>{
            override fun onResponse(call: Call<ResponseCheck>, response: Response<ResponseCheck>) {
                hideLoadingDialog()
                if (response.isSuccessful){
                    btnKirim.isEnabled = true
                    Toast.makeText(this@PerpanjangActivity, "Sukses Perpanjangan Peminjaman Buku", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseCheck>, t: Throwable) {
                hideLoadingDialog()
                showDialogError()
            }

        })
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.dialog_loading, null)
            builder.setView(view)
            builder.setCancelable(false)
            loadingDialog = builder.create()
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
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

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarFormulir)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}