package com.badrun.my259firdaus.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.AnyOrientationCaptureActivity
import com.badrun.my259firdaus.helper.CustomSpinnerAdapter
import com.badrun.my259firdaus.model.Psb
import com.badrun.my259firdaus.model.ResponsePorto
import com.badrun.my259firdaus.model.ResponsePsb
import com.bumptech.glide.Glide
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanTestActivity : AppCompatActivity() {

    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    private lateinit var imgSantri : ImageView
    private lateinit var namaSantri : TextView
    private lateinit var ttlSantri: TextView
    private lateinit var genderSantri : TextView
    private lateinit var alamatSantri : TextView
    private lateinit var ekstrakurikuler : Spinner
    private lateinit var btnKirim : Button
    private lateinit var bacaQuran : EditText
    private lateinit var nulisQuran : EditText
    private lateinit var hafalanQuran : EditText
    private lateinit var pengetahuan : EditText

    private lateinit var datap : Psb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_test)


        initBtnBack()
        imgSantri = findViewById(R.id.imgSantri)
        namaSantri = findViewById(R.id.namaSantri)
        ttlSantri = findViewById(R.id.ttlSantri)
        genderSantri = findViewById(R.id.genderSantri)
        alamatSantri = findViewById(R.id.alamat)
        ekstrakurikuler = findViewById(R.id.spinner_ekstrakurikuler)
        btnKirim = findViewById(R.id.kirimporto)
        bacaQuran = findViewById(R.id.baca_alquran)
        nulisQuran = findViewById(R.id.nulis_alquran)
        pengetahuan = findViewById(R.id.pengetahuan_umum)
        hafalanQuran = findViewById(R.id.hafalan_quran)

        startQRCodeScan()
        initEkstra()
        btnKirim.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.apply {
                setTitle("Konfirmasi")
                setMessage("Apakah Anda yakin ingin mengirim formulir?")
                setPositiveButton("Yakin") { dialog, _ ->
                    //setupload
                    initSend()
                    dialog.dismiss()
                }
                setNegativeButton("Cek Kembali") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }

    private fun startQRCodeScan() {
        val integrator = IntentIntegrator(this)

        // Mengatur orientasi pemindaian ke portrait
        integrator.setOrientationLocked(true)
        integrator.captureActivity = AnyOrientationCaptureActivity::class.java

        // Memulai pemindaian
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val scannedData = result.contents
                initGetPSB(scannedData)
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initGetPSB(orderID:String){
        showLoadingDialog()
        ApiConfig.create(this).getDataPsb(orderID).enqueue(object : Callback<ResponsePsb>{
            override fun onResponse(call: Call<ResponsePsb>, response: Response<ResponsePsb>) {
                hideLoadingDialog()
                if (response.isSuccessful) {
                    val res = response.body()!!
                    when (res.code) {
                        1 -> {
                            val data = res.data
                            val linkImg = "http://${ApiConfig.iplink.ip}/storage/psb/" + data.foto_santri
                            val nama = "Nama : " + data.nama_santri
                            val ttl = "TTL : " + data.tempatlahir_santri + ", " + data.tanggallahir_santri
                            val gender = "Gender : " + data.gender
                            val alamat = "Alamat : " + data.alamat
                            initPhotoSantri(linkImg)
                            namaSantri.text = nama
                            ttlSantri.text = ttl
                            genderSantri.text = gender
                            alamatSantri.text = alamat
                            datap = data
                        }
                        0 -> {
                            val data = res.data
                            val linkImg = "http://${ApiConfig.iplink.ip}/storage/psb/" + data.foto_santri
                            val nama = "Nama : " + data.nama_santri
                            val ttl = "TTL : " + data.tempatlahir_santri + ", " + data.tanggallahir_santri
                            val gender = "Gender : " + data.gender
                            val alamat = "Alamat : " + data.alamat
                            initPhotoSantri(linkImg)
                            namaSantri.text = nama
                            ttlSantri.text = ttl
                            genderSantri.text = gender
                            alamatSantri.text = alamat
                            datap = data
                            showDialogWithMessage("Informasi Portofolio", "Santri ini sudah di tes!")
                        }
                        else -> {
                            showDialogWithMessage("Error", "Terjadi kesalahan: ${res.message}")
                        }
                    }
                } else {
                    hideLoadingDialog()
                    showDialogWithMessage("Informasi Portofolio", "Santri ini sudah di tes!")
                }
            }

            override fun onFailure(call: Call<ResponsePsb>, t: Throwable) {
                hideLoadingDialog()
                showDialogError()
            }

        })
    }

    private fun showDialogWithMessage(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle(title)
            setMessage(message)
            setFinishOnTouchOutside(false)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            setCancelable(false)
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun initPhotoSantri(link:String){

        Glide.with(this)
            .load(link)
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.errorimage)
            .into(imgSantri)

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

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }


    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarTesting)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }

    private fun initEkstra() {
        val listEkstrakurikuler = resources.getStringArray(R.array.ekstrakurikuler).toList()
        val fontPath = "poppinsmedium.ttf"
        val adapterEkstrakurikuler = CustomSpinnerAdapter(this@ScanTestActivity, listEkstrakurikuler, fontPath)
        ekstrakurikuler.adapter = adapterEkstrakurikuler
    }

    private fun initSend(){
        showLoadingDialog()
        ApiConfig.create(this).setPorto(datap.id,bacaQuran.text.toString().toInt(),nulisQuran.text.toString().toInt(),pengetahuan.text.toString().toInt(),hafalanQuran.text.toString().toInt(),ekstrakurikuler.selectedItem.toString()).enqueue(object :Callback<ResponsePorto>{
            override fun onResponse(call: Call<ResponsePorto>, response: Response<ResponsePorto>) {
                if (response.isSuccessful){
                    hideLoadingDialog()
                    Toast.makeText(this@ScanTestActivity, "Upload Berhasil", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                hideLoadingDialog()
            }

            override fun onFailure(call: Call<ResponsePorto>, t: Throwable) {
                hideLoadingDialog()
                showDialogError()
            }

        })
    }

}