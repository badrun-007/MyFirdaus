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
import com.badrun.my259firdaus.model.ResponseCheck
import com.badrun.my259firdaus.model.ResponseMinjam
import com.badrun.my259firdaus.model.ResponsePeminjaman
import com.badrun.my259firdaus.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservasiBukuActivity : AppCompatActivity() {

    private lateinit var nama : EditText
    private lateinit var judulBuku : EditText
    private lateinit var penulisBuku : EditText
    private lateinit var waktuPeminjaman : EditText
    private lateinit var dataUser : SharedPref
    private lateinit var btnKirim : Button
    private var errorDialog: AlertDialog? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservasi_buku)

        initBtnBack()
        nama = findViewById(R.id.nama_anggota_formulir)
        judulBuku = findViewById(R.id.judul_buku_formulir)
        penulisBuku = findViewById(R.id.penulis_buku_formulir)
        waktuPeminjaman = findViewById(R.id.peminjaman_buku_formulir)
        btnKirim = findViewById(R.id.btn_pinjam_buku)
        dataUser = SharedPref(this)

        sharedPreferences = getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

        val dataBuku = intent.getSerializableExtra("buku") as Buku
        val tokenDevice = intent.getStringExtra("hp")
        val tanggal = intent.getStringExtra("tanggal")
        val codeBuku = intent.getStringExtra("code_buku")

        val user = dataUser.getUser()
        nama.setText(user!!.nama)
        judulBuku.setText(dataBuku.judul_buku)
        penulisBuku.setText(dataBuku.penulis)

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


        btnKirim.setOnClickListener {
            btnKirim.isEnabled = false
            sendData(dataBuku,user,tanggal!!,tokenDevice!!,codeBuku!!)
        }

    }

    private fun someFunctionWhereBookmarkIsNeeded(data: Buku) {
        BookmarkUtil.addBookmark(sharedPreferences, data)
    }

    private fun sendData(buku:Buku, user:User, tanggal:String, hp:String, codeBuku:String){
        ApiConfig.create(this).setReservasi(buku.isbn,codeBuku,user.user_id,tanggal,buku.cover_buku,user.nama,buku.judul_buku,buku.penulis,waktuPeminjaman.text.toString().toInt(),hp).enqueue(object :Callback<ResponseCheck>{
            override fun onResponse(
                call: Call<ResponseCheck>,
                response: Response<ResponseCheck>
            ) {
                if (response.isSuccessful){
                    btnKirim.isEnabled = true
                    someFunctionWhereBookmarkIsNeeded(buku)
                    Toast.makeText(this@ReservasiBukuActivity, "Sukses Reservasi Buku", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseCheck>, t: Throwable) {
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