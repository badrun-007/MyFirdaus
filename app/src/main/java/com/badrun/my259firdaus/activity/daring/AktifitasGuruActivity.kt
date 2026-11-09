package com.badrun.my259firdaus.activity.daring

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.GuruAdapter
import com.badrun.my259firdaus.adapter.MatpelGuruAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityAktifitasGuruBinding
import com.badrun.my259firdaus.model.response.ResponseGetGuru
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AktifitasGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAktifitasGuruBinding
    private lateinit var guruAdapter: GuruAdapter
    private val guruList = mutableListOf<ResponseGetGuru>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAktifitasGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        guruAdapter = GuruAdapter(guruList) { selectedGuru ->
            showGuruDetailDialog(selectedGuru)
        }

        binding.rvGuru.apply {
            layoutManager = LinearLayoutManager(this@AktifitasGuruActivity)
            adapter = guruAdapter
        }

        getGuruData()
    }

    private fun showGuruDetailDialog(guru: ResponseGetGuru) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_detail_guru)

        val tvNama = dialog.findViewById<TextView>(R.id.tvNamaGuruDialog)
        val rvMatpel = dialog.findViewById<RecyclerView>(R.id.rvMatpel)

        tvNama.text = guru.nama
        rvMatpel.layoutManager = LinearLayoutManager(this)
        rvMatpel.adapter = MatpelGuruAdapter(guru.matpels)

        dialog.show()
    }


    private fun getGuruData() {
        ApiConfig.create(this).getGuruMateriStatus()
            .enqueue(object : Callback<List<ResponseGetGuru>> {
                override fun onResponse(call: Call<List<ResponseGetGuru>>, response: Response<List<ResponseGetGuru>>) {
                    response.body()?.let {
                        guruList.clear()
                        guruList.addAll(it)
                        guruAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<ResponseGetGuru>>, t: Throwable) {
                    Toast.makeText(this@AktifitasGuruActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            })
    }

}