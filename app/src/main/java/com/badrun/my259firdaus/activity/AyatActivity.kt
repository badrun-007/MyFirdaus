package com.badrun.my259firdaus.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.AdapterAyat
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.model.Ayat
import com.badrun.my259firdaus.model.ResponseAyat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AyatActivity : AppCompatActivity() {

    private lateinit var rvAyat: RecyclerView
    private lateinit var mainAdapter : AdapterAyat
    private lateinit var load : ProgressBar
    private lateinit var bismillah : CardView

    private lateinit var judulSurah : TextView
    private val context = this
    private val REQUEST_CODE = 707

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayat)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        }


        rvAyat = findViewById(R.id.rvAyathaha)
        judulSurah = findViewById(R.id.tvHeader)
        load = findViewById(R.id.pb_list)
        bismillah = findViewById(R.id.card_bismillah)

        getSurah()



        setUpRecycler()
        initBtnBack()


    }

    private fun getSurah(){
        judulSurah.text = intent.getStringExtra("namaSurat")
        val no = intent.getIntExtra("nosurat",1)

        val api = "https://equran.id/"
        load.visibility = View.VISIBLE
        ApiConfig.create(context,api).getAyat(no).enqueue(object : Callback<ResponseAyat> {
            override fun onResponse(call: Call<ResponseAyat>, response: Response<ResponseAyat>) {
                load.visibility = View.GONE
                if(no == 1 || no == 9){
                    bismillah.visibility = View.GONE
                } else {
                    bismillah.visibility = View.VISIBLE
                }

                if (response.isSuccessful){
                    val res = response.body()!!
                    showData(res)

                }
            }

            override fun onFailure(call: Call<ResponseAyat>, t: Throwable) {
                load.visibility = View.GONE
                Toast.makeText(this@AyatActivity, "Failed connection to Database", Toast.LENGTH_SHORT).show()
                Log.e("bdr", "onFailure: ${t.message}", )
            }

        })
    }


    private fun setUpRecycler(){
        mainAdapter = AdapterAyat(this,arrayListOf())
        rvAyat.apply {
            layoutManager = LinearLayoutManager(this@AyatActivity)
            adapter = mainAdapter
        }
    }

    private fun showData(data : ResponseAyat){
        val surah = data.data.ayat
        mainAdapter.showData(surah)
    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarDetail)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }


}