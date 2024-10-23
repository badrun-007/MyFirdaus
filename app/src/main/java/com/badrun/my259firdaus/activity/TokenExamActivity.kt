package com.badrun.my259firdaus.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.model.Buku
import com.badrun.my259firdaus.model.JadwalExam
import com.badrun.my259firdaus.model.ResponseJadwalExam
import com.badrun.my259firdaus.model.ResponseSetExam
import com.badrun.my259firdaus.model.ResponseSetToken
import com.badrun.my259firdaus.model.Soal
import com.badrun.my259firdaus.model.SoalExam
import com.badrun.my259firdaus.model.User
import com.badrun.my259firdaus.model.userExam
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TokenExamActivity : AppCompatActivity() {

    private lateinit var nama: TextView
    private lateinit var kelas: TextView
    private lateinit var matpel: TextView
    private lateinit var startExam: TextView
    private lateinit var endExam: TextView
    private lateinit var foto : ImageView
    private lateinit var tokenExam : EditText
    private lateinit var btnKirim : Button
    private var duration : Long = 0L
    private lateinit var pb : ProgressBar

    private lateinit var dataUser: userExam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_exam)

        dataUser = (intent.getSerializableExtra("data_exam") as? userExam)!!

        init()

    }

    private fun init() {
        nama = findViewById(R.id.namaSantri)
        kelas = findViewById(R.id.kelasExam)
        matpel = findViewById(R.id.matpelExam)
        startExam = findViewById(R.id.timeStart)
        endExam = findViewById(R.id.timeEnd)
        foto = findViewById(R.id.imgSantri)
        tokenExam = findViewById(R.id.token_exam)
        btnKirim = findViewById(R.id.kirimtoken)
        pb = findViewById(R.id.pb)

        // Pastikan jadwal sudah terisi sebelum menginisialisasi UI
        nama.text = "Nama : ${dataUser.nama}"
        kelas.text = "Kelas : ${dataUser.class_name}"
        matpel.text = "Matapelajaran : ${dataUser.matpel}"
        startExam.text = "Tanggal Ujian : ${formatDate(dataUser.start_time)}"

        val linkImg = "http://${ApiConfig.iplink.ip}/storage/profile/" + dataUser.image
        initPhotoSantri(linkImg)


        duration = calculateDuration(dataUser.start_time, dataUser.end_time)
        endExam.text = "Waktu Ujian : $duration Menit"

        btnKirim.setOnClickListener {
            pb.visibility = View.VISIBLE
            btnKirim.isEnabled = false
            getSoal()
        }

    }

    

    private fun initPhotoSantri(link:String){

        Glide.with(this)
            .load(link)
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.errorimage)
            .into(foto)

    }

    private fun getSoal() {
        if (tokenExam.text.isEmpty()){
            tokenExam.error = "Isi Dulu Token Soalnya!!"
            tokenExam.requestFocus()
            return
        } else {

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val endTime: Date? = format.parse(dataUser.end_time)

            // Mendapatkan waktu sekarang
            val currentTimeMillis = System.currentTimeMillis()

            // Menghitung selisih dalam milidetik
            val durasi: Long = if (endTime != null) {
                endTime.time - currentTimeMillis
            } else {
                0L // Jika terjadi kesalahan saat parsing, set durasi menjadi 0
            }

            ApiConfig.create(this)
                .setExam(dataUser.schedule_id,tokenExam.text.toString(),dataUser.id,dataUser.matpel_id,dataUser.class_name,dataUser.transaksi_id)
                .enqueue(object : Callback<ResponseSetToken>{
                    override fun onResponse(
                        call: Call<ResponseSetToken>,
                        response: Response<ResponseSetToken>
                    ) {
                        if (response.isSuccessful){
                            pb.visibility = View.GONE
                            btnKirim.isEnabled = true
                            val res = response.body()!!
                            if (res.code == 1) {
                                val i = Intent(this@TokenExamActivity,SoalActivity::class.java)
                                i.putExtra("transaksi_id",res.data)
                                i.putExtra("class_id",dataUser.class_id)
                                i.putExtra("end_time",dataUser.end_time)
                                i.putExtra("durasi",durasi)
                                startActivity(i)
                                finish()
                            } else {
                                Toast.makeText(this@TokenExamActivity, res.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseSetToken>, t: Throwable) {
                        pb.visibility = View.GONE
                        btnKirim.isEnabled = true
                        Toast.makeText(this@TokenExamActivity, "Tidak dapat terhubung dengan server", Toast.LENGTH_SHORT).show()
                    }

                })
        }
    }

    private fun calculateDuration(startTime: String, endTime: String): Long {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return try {
            // Parsing string menjadi date
            val startDate = inputFormat.parse(startTime)
            val endDate = inputFormat.parse(endTime)

            // Menghitung selisih waktu dalam milidetik
            val durationInMillis = endDate.time - startDate.time

            // Mengubah milidetik ke menit
            durationInMillis / (1000 * 60)
        } catch (e: Exception) {
            e.printStackTrace()
            0 // Kembalikan 0 jika terjadi kesalahan
        }
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        /*val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy 'Jam' HH:mm 'WIB'", Locale("id", "ID"))*/

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            dateString // Kembalikan string aslinya jika gagal
        }
    }

    private fun showDialogMassage(message: String, judul:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(judul)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Tidak Boleh Keluar!!!", Toast.LENGTH_SHORT).show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            // Jika aplikasi kehilangan fokus (user menekan Home)
            finish()
        }
    }

}