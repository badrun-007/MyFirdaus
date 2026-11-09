package com.badrun.my259firdaus.activity.daring

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.MateriAdapter
import com.badrun.my259firdaus.adapter.MateriSantriAdapter
import com.badrun.my259firdaus.adapter.TugasJawabanAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityBankJawabanBinding
import com.badrun.my259firdaus.model.response.Materi
import com.badrun.my259firdaus.model.response.ResponseJawabanSantri
import com.badrun.my259firdaus.model.response.ResponseMateri
import com.badrun.my259firdaus.model.response.ResponseMatpel
import com.badrun.my259firdaus.model.response.TugasJawabanItem
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankJawabanActivity : AppCompatActivity() {

    private  lateinit var binding : ActivityBankJawabanBinding
    private lateinit var dataMatpel: List<ResponseMatpel.MatpelGuru>
    private var selectedMatpel: Int? = null
    private var selectedKelas: String? = null
    private lateinit var dataJawaban: List<Materi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankJawabanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data matpel yang diteruskan melalui Intent
        val dataMatpel = intent.getParcelableArrayListExtra<ResponseMatpel.MatpelGuru>("data") ?: listOf()


        // Mengisi ChipGroup untuk Mata Pelajaran dan Kelas
        val matpels = dataMatpel.map { it.matpel }
        val kelasList = dataMatpel.map { it.kelas }

        populateMatpelChips(matpels)
        populateKelasChips(kelasList)

        binding.btnTampilkanData.setOnClickListener {
            if (selectedMatpel != null && selectedKelas != null) {
                getJawaban(selectedMatpel!!, selectedKelas!!)
            } else {
                Toast.makeText(this, "Pilih dulu mata pelajaran dan kelas", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun styledChip(text: String): Chip {
        return Chip(this).apply {
            this.text = text
            isCheckable = true
            isClickable = true
            chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_background)
            chipStrokeWidth = 2f
            setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text_color))
            chipCornerRadius = 24f
            textSize = 14f
            chipStartPadding = 16f
            chipEndPadding = 16f
            isCheckedIconVisible = false
            rippleColor = ContextCompat.getColorStateList(context, R.color.chip_ripple)
        }
    }

    private fun populateMatpelChips(matpels: List<ResponseMatpel.Matpel>) {
        val uniqueMatpels = matpels.distinctBy { it.name }

        uniqueMatpels.forEach { matpel ->
            val chip = styledChip(matpel.name ?: "-")
            chip.text = matpel.name
            chip.isCheckable = true
            chip.setOnClickListener {
                selectedMatpel = matpel.id  // <- ini penting!
            }
            binding.chipGroupMatpel.addView(chip)
        }
    }

    private fun populateKelasChips(kelasList: List<ResponseMatpel.Kelas>) {
        val sortedUniqueKelas = kelasList.distinctBy { it.name }.sortedBy { it.name }

        sortedUniqueKelas.forEach { kelas ->
            val chip = styledChip(kelas.name ?: "-")
            chip.text = kelas.name
            chip.isCheckable = true
            chip.setOnClickListener {
                selectedKelas = kelas.name
            }
            binding.chipGroupKelas.addView(chip)
        }
    }

    private fun getJawaban(matpelId:Int, kelas:String){
        ApiConfig.create(this).getJawabanTugas(matpelId, kelas).enqueue(object : Callback<ResponseJawabanSantri> {
            override fun onResponse(
                call: Call<ResponseJawabanSantri>,
                response: Response<ResponseJawabanSantri>
            ) {
                val res = response.body()
                if (res != null && res.code == 1){
                    val listTugas = res.data.map {
                        TugasJawabanItem(
                            tugas_id = it.tugas_id,
                            judul = it.judul,
                            deadline = it.deadline,
                            jumlah_jawaban = it.jumlah_jawaban,
                            jawaban_santri = it.jawaban_santri
                        )
                    }

                    val adapter = TugasJawabanAdapter(this@BankJawabanActivity,listTugas) { tugas ->
                        // Aksi ketika diklik, misalnya buka detail jawaban santri
                        val intent = Intent(this@BankJawabanActivity, ListJawabanSantriActivity::class.java)
                        intent.putExtra("judul", tugas.judul)
                        intent.putExtra("tugas_id", tugas.tugas_id)
                        intent.putExtra("jawaban_santri", ArrayList(tugas.jawaban_santri)) // pastikan jawaban_santri Serializable
                        startActivity(intent)
                    }

                    binding.rvMateri.layoutManager = LinearLayoutManager(this@BankJawabanActivity)
                    binding.rvMateri.adapter = adapter

                } else{
                    Toast.makeText(this@BankJawabanActivity, "Data kosong", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseJawabanSantri>, t: Throwable) {
                Log.e("BDR", "onFailure: ${t.message}", )
            }
        })
    }


}