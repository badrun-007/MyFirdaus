package com.badrun.my259firdaus.activity


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.BookmarkUtil
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.Buku
import com.badrun.my259firdaus.model.ResponseMinjam
import com.badrun.my259firdaus.model.ResponsePeminjaman
import com.badrun.my259firdaus.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinjamBukuActivity : AppCompatActivity() {

    private lateinit var nama : EditText
    private lateinit var judulBuku : EditText
    private lateinit var penulisBuku : EditText
    private lateinit var isbn : EditText
    private lateinit var kodeBuku : EditText
    private lateinit var waktuPeminjaman : EditText
    private lateinit var dataUser : SharedPref
    private lateinit var btnKirim : Button
    private var tokenDevices = ""
    private var errorDialog: AlertDialog? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinjam_buku)

        initBtnBack()
        nama = findViewById(R.id.nama_anggota_formulir)
        judulBuku = findViewById(R.id.judul_buku_formulir)
        isbn = findViewById(R.id.isbn_buku_formulir)
        kodeBuku = findViewById(R.id.kode_buku_formulir)

        penulisBuku = findViewById(R.id.penulis_buku_formulir)
        waktuPeminjaman = findViewById(R.id.peminjaman_buku_formulir)
        btnKirim = findViewById(R.id.btn_pinjam_buku)
        dataUser = SharedPref(this)

        sharedPreferences = getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

        val dataBuku = intent.getSerializableExtra("book") as Buku
        val codeBuku = intent.getStringExtra("code_buku")

        val user = dataUser.getUser()
        nama.setText(user!!.nama)
        judulBuku.setText(dataBuku.judul_buku)
        penulisBuku.setText(dataBuku.penulis)
        isbn.setText(dataBuku.isbn)
        kodeBuku.setText(codeBuku)

        waktuPeminjaman.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validasi panjang teks
                if (s.isNullOrEmpty()) {
                    waktuPeminjaman.error = "Masukkan angka antara 1 hingga 7"
                } else {
                    try {
                        val input = s.toString().toInt()
                        if (input < 1 || input > 7) {
                            waktuPeminjaman.error = "Maksimal Peminjaman 7 Hari"
                        } else {
                            waktuPeminjaman.error = null // Reset error jika valid
                        }
                    } catch (e: NumberFormatException) {
                        waktuPeminjaman.error = "Masukkan angka yang valid"
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //pembayaran
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("BDR", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            tokenDevices = task.result
        })

        btnKirim.setOnClickListener {
            btnKirim.isEnabled = false
            sendData(dataBuku,user, codeBuku.toString())
        }

    }

    private fun someFunctionWhereBookmarkIsNeeded(data: Buku) {
        BookmarkUtil.addBookmark(sharedPreferences, data)
    }

    private fun sendData(buku:Buku, user:User, codeBuku:String){
        ApiConfig.create(this).pinjamBuku(codeBuku,user.user_id,buku.isbn,buku.cover_buku,user.nama,buku.judul_buku,buku.penulis,waktuPeminjaman.text.toString().toInt(),tokenDevices).enqueue(object :Callback<ResponseMinjam>{
            override fun onResponse(
                call: Call<ResponseMinjam>,
                response: Response<ResponseMinjam>
            ) {
                if (response.isSuccessful){
                    btnKirim.isEnabled = true
                    someFunctionWhereBookmarkIsNeeded(buku)
                    Toast.makeText(this@PinjamBukuActivity, "Sukses Pengajuan Peminjaman Buku", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseMinjam>, t: Throwable) {
                Log.e("BDR", "onFailure: ${t.message}", )
                showDialogError()
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

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarFormulir)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}