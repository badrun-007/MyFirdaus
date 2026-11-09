package com.badrun.my259firdaus.activity.daring

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityDaringGuruBinding
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.response.ResponseMatpel
import com.badrun.my259firdaus.model.response.ResponseMatpel.MatpelGuru
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DaringGuruActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDaringGuruBinding
    private lateinit var s : SharedPref
    private lateinit var matpelGuru : List<ResponseMatpel.MatpelGuru>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaringGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        s = SharedPref(this)
        val user = s.getUser()

        getMatpel(user!!.id)

        val namaLengkap = user.nama.trim()
        val gelar = listOf("Dra.", "Hj.", "M.Pd", "S.Pd.I", "M.Ag", "S.Kom.I", "H.", "Ayi")
        val namaTanpaGelar = namaLengkap.split(" ")
            .filter { it !in gelar }  // Hapus gelar
            .joinToString(" ")
        val namaPertama = namaTanpaGelar.split(" ").firstOrNull()?.capitalize() ?: "User"

        val sapaan = if (namaPertama.lowercase() in listOf("timi", "konia", "oop", "rini", "dede", "ida", "yuniar", "sayyida", "pipit", "iyan")) {
            "Ibu"
        } else {
            "Pak"
        }

        if (s.getUser()!!.user_id == 142){
            binding.aktifitasGuru.visibility = View.VISIBLE
            binding.aktifitasGuru.setOnClickListener {
                startActivity(Intent(this,AktifitasGuruActivity::class.java))
            }
        } else {
            binding.aktifitasGuru.visibility = View.GONE
        }

        binding.tvWelcome.text = "Selamat datang, $sapaan $namaPertama \uD83D\uDC4B"

        katakata()

        binding.menuHadir.setOnClickListener {
            showCustomDialog()
        }

        binding.menuJadwal.setOnClickListener { showCustomDialog() }



    }

    private fun getMatpel(id:Int){
        ApiConfig.create(this).getMatpel(id).enqueue(object : Callback<ResponseMatpel>{
            override fun onResponse(
                call: Call<ResponseMatpel>,
                response: Response<ResponseMatpel>
            ) {
                val res = response.body()
                if (res?.code == 1){
                    matpelGuru = res.data

                    // Data sudah siap, baru aktifkan tombol
                    binding.menuMateri.setOnClickListener {
                        val i = Intent(this@DaringGuruActivity, MateriActivity::class.java)
                        i.putParcelableArrayListExtra("data", ArrayList(matpelGuru))
                        startActivity(i)
                    }
                    binding.menuJawaban.setOnClickListener {
                        val i = Intent(this@DaringGuruActivity, BankJawabanActivity::class.java)
                        i.putParcelableArrayListExtra("data", ArrayList(matpelGuru))
                        startActivity(i)
                    }
                } else{
                    Log.e("BDR",res?.message ?: "No response message")
                }
            }

            override fun onFailure(call: Call<ResponseMatpel>, t: Throwable) {
                Log.e("BDR", "Gagal ambil Matpel Guru: ${t.message}")
            }
        })
    }


    private fun katakata(){
        val messages = listOf(
            "Bismillah! Hari yang baik untuk menebar ilmu dan kebaikan 🌱",
            "Ayo mulai hari ini dengan semangat berbagi ilmu! 📖✨",
            "Setiap materi yang disampaikan, semoga menjadi amal jariyah yang mengalir! 💫",
            "Siap membimbing generasi hebat hari ini? Yuk mulai! 💡",
            "Berbagi ilmu, membangun peradaban. Semangat mengajar, Ustadz/Ustadzah!",
            "Ilmu yang bermanfaat akan terus hidup, mari mulai hari ini dengan niat terbaik! 🌿",
            "Mengajar hari ini, mencetak pemimpin masa depan! 🚀",
            "Guru bukan sekedar mengajar, tapi sedang membangun peradaban. 🕌📖",
            "Satu jam pelajaran hari ini bisa mengubah hidup seseorang. ✨",
            "Hari ini mungkin terasa biasa, tapi bagi murid kita bisa jadi luar biasa. \uD83C\uDF0D"
        )

        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 (Sunday) - 7 (Saturday)
        val index = (dayOfWeek - 1) % messages.size // agar sesuai index 0-6
        val messageToday = messages[index]

        val locale = Locale("id", "ID") // Bahasa Indonesia
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", locale)
        val formattedDate = formatter.format(calendar.time)

        binding.tvKalWelcome.text = messages.random()
        binding.tvDate.text = formattedDate

    }

    private fun showCustomDialog() {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(this)
        val dialogView: View = inflater.inflate(R.layout.dialog_fitur, null)

        // Create the dialog builder
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)

        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialogButtonError)

        val alertDialog = dialogBuilder.create()
        dialogButton.setOnClickListener {
            // Dismiss the dialog
            alertDialog.dismiss()
        }
        alertDialog.show()
    }



}