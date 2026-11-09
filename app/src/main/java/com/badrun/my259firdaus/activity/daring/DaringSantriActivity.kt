package com.badrun.my259firdaus.activity.daring

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityDaringGuruBinding
import com.badrun.my259firdaus.databinding.ActivityDaringSantriBinding
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.response.Materi
import com.badrun.my259firdaus.model.response.ResponseMateri
import com.badrun.my259firdaus.model.response.ResponseMatpel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DaringSantriActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDaringSantriBinding
    private lateinit var s : SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaringSantriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        s = SharedPref(this)
        val user = s.getUser()
        val tamu = s.getTamu()

        val namaLengkap = user!!.nama.trim()
        val namaPertama = namaLengkap.split(" ").firstOrNull()?.capitalize() ?: "User"

        binding.tvWelcome.text = "Selamat datang, $namaPertama \uD83D\uDC4B"
        katakata()

        val kelas = user.kelas ?: tamu?.kelas


        binding.menuMateri.setOnClickListener {
            val i = Intent(this,MateriSantriActivity::class.java)
            i.putExtra("kelas",kelas)
            startActivity(i)
        }

        binding.menuHadir.setOnClickListener {
            showCustomDialog()
        }

    }

    private fun showCustomDialog() {
        val inflater = LayoutInflater.from(this)
        val dialogView: View = inflater.inflate(R.layout.dialog_fitur, null)

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


    private fun katakata(){
        val messages = listOf(
            "Bismillah! Hari ini kesempatan baru untuk menjadi lebih baik 🌟",
            "Setiap ilmu yang kamu pelajari, adalah bekal untuk masa depanmu ✨📖",
            "Jangan lelah belajar, karena ilmu adalah cahaya yang menerangi hidupmu 🕯️",
            "Santri hebat lahir dari niat yang ikhlas dan usaha yang sungguh-sungguh 💪",
            "Satu halaman hari ini, bisa jadi langkah besar untuk cita-citamu nanti 🚀",
            "Belajar bukan hanya untuk nilai, tapi untuk membentuk akhlak dan hati 🤲",
            "Langkah kecil hari ini adalah bagian dari perjalanan besarmu esok ⛰️",
            "Setiap huruf yang kamu baca, semoga menjadi pahala dan keberkahan 💫",
            "Ilmu itu penjaga yang setia, ia akan mengangkat derajatmu, insyaAllah 🕌",
            "Jadilah santri yang tak hanya cerdas, tapi juga berakhlak mulia ❤️"
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

}