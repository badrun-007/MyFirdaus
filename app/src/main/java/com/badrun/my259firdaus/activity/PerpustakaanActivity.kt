package com.badrun.my259firdaus.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.ViewPagerAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.fragment.ListBookFragment
import com.badrun.my259firdaus.fragment.PeminjamanFragment
import com.badrun.my259firdaus.helper.BookViewModel
import com.badrun.my259firdaus.model.ResponseBook
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PerpustakaanActivity : AppCompatActivity() {

    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2
    private lateinit var bookViewModel: BookViewModel
    private lateinit var kategory : Spinner
    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perpustakaan)

        initBtnBack()
        getBuku()
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(
            tabLayout, viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.setText("List Buku")
                1 -> tab.setText("Buku Saya")
                2 -> tab.setText("Transaksi")
            }
        }.attach()

        handleIntent(intent)

    }

    /*private fun handleIntent(intent: Intent?) {
        intent?.extras?.let { extras ->
            val action = extras.getString("action")
            val perpanjang = extras.getString("perpanjang")
            if (action != null && action == "show_perpustakaan_fragment") {
                viewPager.setCurrentItem(2, true)
            }
        }
    }*/

    private fun handleIntent(intent: Intent?) {
        intent?.extras?.let { extras ->
            val action = extras.getString("action")
            val perpanjang = extras.getString("perpanjang")
            if (action != null && action == "show_perpustakaan_fragment") {
                val fragment = PeminjamanFragment()
                val bundle = Bundle().apply {
                    putString("perpanjang", perpanjang)
                }
                fragment.arguments = bundle

                // Beralih ke fragment yang sesuai
                viewPager.setCurrentItem(2, true)

                // Asumsikan adapter memiliki daftar fragment
                (viewPager.adapter as ViewPagerAdapter).updateFragment(2, fragment)
            }
        }
    }

    private fun getBuku(){
        showLoadingDialog()
        ApiConfig.create(this).getBuku().enqueue(object : Callback<ResponseBook>{
            override fun onResponse(call: Call<ResponseBook>, response: Response<ResponseBook>) {
                hideLoadingDialog()
                if(response.isSuccessful){
                    val res = response.body()
                    if (res!!.code == 1){
                        bookViewModel.setBooks(res.data)
                    } else{
                        hideLoadingDialog()
                        showDialogError()
                    }
                } else {
                    hideLoadingDialog()
                    showDialogError()
                }
            }

            override fun onFailure(call: Call<ResponseBook>, t: Throwable) {
                hideLoadingDialog()
                showDialogError()
            }

        })
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
        val toolbar: Toolbar = findViewById(R.id.toolbarperpus)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }
}