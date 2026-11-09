package com.badrun.my259firdaus.activity.daring

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.MateriAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityMateriBinding
import com.badrun.my259firdaus.model.response.Materi
import com.badrun.my259firdaus.model.response.ResponseMateri
import com.badrun.my259firdaus.model.response.ResponseMatpel
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMateriBinding
    private lateinit var dataMatpel: List<ResponseMatpel.MatpelGuru>
    private var selectedMatpel: String? = null
    private var selectedKelas: String? = null
    private lateinit var materiAdapter: MateriAdapter
    private lateinit var dataMateri: List<Materi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data matpel yang diteruskan melalui Intent
        val dataMatpel = intent.getParcelableArrayListExtra<ResponseMatpel.MatpelGuru>("data") ?: listOf()

        // Siapkan RecyclerView dan Adapter
        materiAdapter = MateriAdapter()
        binding.rvMateri.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = materiAdapter
        }

        // Mengisi ChipGroup untuk Mata Pelajaran dan Kelas
        val matpels = dataMatpel.map { it.matpel }
        val kelasList = dataMatpel.map { it.kelas }

        populateMatpelChips(matpels)
        populateKelasChips(kelasList)

        // Memuat Materi awal
        val idguru = dataMatpel[0].guru_id
        getMateri(idguru)

        // Tombol tambah materi
        binding.fabTambahMateri.setOnClickListener {
            val i = Intent(this,InputMateriActivity::class.java)
            i.putParcelableArrayListExtra("data", ArrayList(dataMatpel))
            startActivity(i)
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
        // Hapus duplikat berdasarkan nama
        val uniqueMatpels = matpels.distinctBy { it.name }

        uniqueMatpels.forEach { matpel ->
            val chip = styledChip(matpel.name ?: "-")
            chip.text = matpel.name
            chip.isCheckable = true
            chip.setOnClickListener {
                selectedMatpel = matpel.name
                filterMateri()
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
                filterMateri()
            }
            binding.chipGroupKelas.addView(chip)
        }
    }

    private fun getMateri(id: Int) {
        ApiConfig.create(this).getMateri(id).enqueue(object : Callback<ResponseMateri> {
            override fun onResponse(
                call: Call<ResponseMateri>,
                response: Response<ResponseMateri>
            ) {
                val res = response.body()
                if (res?.code == 1) {
                    dataMateri = res.data // Simpan data materi ke dalam list
                    materiAdapter.submitList(dataMateri) // Update adapter dengan data materi
                } else {
                    Log.e("BDR", "Error: ${res?.message}")
                }
            }

            override fun onFailure(call: Call<ResponseMateri>, t: Throwable) {
                Log.e("BDR", "onFailure: ${t.message}")
            }
        })
    }

    private fun filterMateri() {
        val filteredMateri = dataMateri.filter { materi ->
            val matpelMatch = selectedMatpel.isNullOrEmpty() || materi.matpel?.name == selectedMatpel
            val kelasMatch = selectedKelas.isNullOrEmpty() || materi.kelas?.name == selectedKelas
            matpelMatch && kelasMatch
        }
        materiAdapter.submitList(filteredMateri)
    }
}

