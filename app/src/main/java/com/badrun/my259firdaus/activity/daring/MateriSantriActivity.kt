package com.badrun.my259firdaus.activity.daring

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.MateriAdapter
import com.badrun.my259firdaus.adapter.MateriSantriAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityDaringSantriBinding
import com.badrun.my259firdaus.databinding.ActivityMateriSantriBinding
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.response.Materi
import com.badrun.my259firdaus.model.response.ResponseMateri
import com.badrun.my259firdaus.model.response.ResponseMatpel
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MateriSantriActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMateriSantriBinding
    private lateinit var dataMateri: List<Materi>
    private var selectedMatpel: String? = null
    private var selectedKelas: String? = null
    private lateinit var materiAdapter: MateriSantriAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMateriSantriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kelas = intent.getStringExtra("kelas")
        getMateri(kelas.toString()) // panggil API

        materiAdapter = MateriSantriAdapter(this)
        binding.rvMateri.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = materiAdapter
        }
    }


    private fun getMateri(kelas: String) {
        binding.pb.visibility = View.VISIBLE
        ApiConfig.create(this).getMateriSantri(kelas).enqueue(object : Callback<ResponseMateri> {
            override fun onResponse(
                call: Call<ResponseMateri>,
                response: Response<ResponseMateri>
            ) {
                binding.pb.visibility = View.GONE
                val res = response.body()
                if (res?.code == 1) {
                    dataMateri = res.data

                    // Tampilkan materi ke adapter
                    materiAdapter.submitList(dataMateri)

                    // Ambil semua matpel dari materi dan populate chip
                    val matpelPopulated = dataMateri.mapNotNull { it.matpel } // pastikan tidak null
                    populateMatpelChips(matpelPopulated)
                } else {
                    Log.e("BDR", "Error: ${res?.message}")
                }
            }

            override fun onFailure(call: Call<ResponseMateri>, t: Throwable) {
                binding.pb.visibility = View.GONE
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

    private fun populateMatpelChips(matpels: List<Materi.Matpel>) {
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



}