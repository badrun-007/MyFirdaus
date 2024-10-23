package com.badrun.my259firdaus.activity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.InfoPendaftaran
import com.badrun.my259firdaus.model.ResponseInfoPendaftaran
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class PendaftaranActivity : AppCompatActivity() {

    private lateinit var judul : TextView
    private lateinit var tatacara : TextView
    private lateinit var materiTest : TextView
    private lateinit var agendaKegiatan : TextView
    private lateinit var buttonBrosur : Button
    private lateinit var buttonFormulir : Button
    private lateinit var load : ProgressBar

    private var downloadID: Long = 0
    private val fileName = "brosur.pdf"

    private lateinit var s : SharedPref

    private lateinit var data : InfoPendaftaran

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pendaftaran)

        judul = findViewById(R.id.judulinfopendaftaran)
        tatacara = findViewById(R.id.tatacara)
        materiTest = findViewById(R.id.materites)
        agendaKegiatan = findViewById(R.id.agendakegiatan)
        buttonBrosur = findViewById(R.id.btnbrosur)
        buttonFormulir = findViewById(R.id.btnformulir)
        load = findViewById(R.id.pb_pendaftaran)

        getInfoPendaftaran()
        initBtnBack()

        buttonFormulir.setOnClickListener {
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val expiryDate = dateFormat.parse(data.expiry_time)
            val openDate = dateFormat.parse(data.open_time)
            s = SharedPref(this)
            val user = s.getUser()
            
            val fullDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            val openDates = dateFormat.parse(data.open_time)
            val formattedDate = fullDateFormat.format(openDates)

            when (user!!.role) {
                5, 2, 4 -> {
                    if (openDate != null && currentDate.before(openDate)) {
                        AlertDialog.Builder(this)
                            .setTitle("Pendaftaran Belum Dibuka")
                            .setMessage("Silakan coba lagi pada Hari ${formattedDate}.")
                            .setPositiveButton("OK", null)
                            .show()
                    } else if (expiryDate != null && currentDate.after(expiryDate)) {
                        AlertDialog.Builder(this)
                            .setTitle("Pendaftaran Ditutup")
                            .setMessage("Pendaftaran telah ditutup.")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        val i = Intent(this, PsbActivity::class.java)
                        i.putExtra("biayapendaftaran", data.biaya_pendaftaran)
                        startActivity(i)
                    }
                }
                else -> {
                    showDialogWithMessage("Dimohon untuk daftar akun baru untuk mengisi pendaftaran", "Maaf Anda Tidak Bisa Mengisi Formulir Pendaftaran")
                }
            }
        }

        buttonBrosur.setOnClickListener {
            startDownload()
        }
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(this, onDownloadComplete, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(onDownloadComplete, intentFilter)
        }
    }

    private fun showDialogWithMessage(message: String, title:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, _ ->
            //belum beres
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") {dial, _ ->
            dial.dismiss()
        }
        builder.show()
    }

    private fun getInfoPendaftaran(){

        load.visibility = View.VISIBLE
        ApiConfig.create(this).getInfoPsb().enqueue(object : Callback<ResponseInfoPendaftaran>{
            override fun onResponse(
                call: Call<ResponseInfoPendaftaran>,
                response: Response<ResponseInfoPendaftaran>
            ) {
                if(response.isSuccessful){
                    load.visibility = View.GONE
                    val res = response.body()!!.data
                    data = res
                    init()
                }
            }

            override fun onFailure(call: Call<ResponseInfoPendaftaran>, t: Throwable) {
                load.visibility = View.GONE
                Log.e("BDR", "onFailure: ${t.message}")
            }

        })
    }

    private fun init(){
        val juduls = getString(R.string.infoPendaftaran) + data.tahun_pelajaran
        judul.text = juduls
        tatacara.text = getString(R.string.tatacara)
        materiTest.text = getString(R.string.materites)
        agendaKegiatan.text = getString(R.string.agendakegiatan)
    }

    private fun startDownload() {
        val url = "http://${ApiConfig.iplink.ip}/storage/brosur/"+data.brosur
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Brosur")
            .setDescription("Downloading $fileName")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    // BroadcastReceiver to receive the download complete intent
    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                openDownloadedFile()
            }
        }
    }

    private fun openDownloadedFile() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        val fileUri = Uri.fromFile(file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Open with")
        try {
            startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarpsb)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }
}