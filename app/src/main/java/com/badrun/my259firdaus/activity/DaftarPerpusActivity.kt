package com.badrun.my259firdaus.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.os.LocaleListCompat
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.PerpusMidtrans
import com.badrun.my259firdaus.model.ResponseMidtrans
import com.badrun.my259firdaus.model.ResponseUser
import com.badrun.my259firdaus.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class DaftarPerpusActivity : AppCompatActivity() {

    private lateinit var tataCaraPendaftaran : TextView
    private lateinit var ketentuanUmum : TextView
    private lateinit var btnDaftar : Button
    private lateinit var setuju : CheckBox

    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null
    private lateinit var s : SharedPref
    private var tokenDevices = ""
    private var tokenPembayaran = ""

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("PembayaranActivity", "Activity result received")
        if (result?.resultCode == RESULT_OK) {
            result.data?.let {
                val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                if (transactionResult?.status == "pending" || transactionResult?.status == "settlement") {
                    val intent = Intent(this, PembayaranDetailActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Log.d("PembayaranActivity", "Result data is null")
                Toast.makeText(this, "Data hasil transaksi null", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.d("PembayaranActivity", "Result code is not RESULT_OK: $result?.resultCode")
            Toast.makeText(this, "Transaksi Gagal atau Dibatalkan", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_perpus)
        initBtnBack()

        tataCaraPendaftaran = findViewById(R.id.tatacara)
        ketentuanUmum = findViewById(R.id.ketentuanUmum)
        btnDaftar = findViewById(R.id.btnDaftar)
        setuju = findViewById(R.id.chekbox_setuju)
        s = SharedPref(this)

        tataCaraPendaftaran.text = getString(R.string.daftarAnggota)
        ketentuanUmum.text = getString(R.string.ketentuanUmum)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("BDR", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            tokenDevices = task.result
        })
        initMidtrans()

        Log.e("BDR", "onCreate Pertama: ${s.getUser()!!.perpustakaan}", )

        btnDaftar.setOnClickListener {
            if (setuju.isChecked) {
                val user = s.getUser()
                initTransaksi(tokenDevices, user!!)
            } else {
                setuju.error = "Sebelum Mendaftar Ceklis terlebih dahulu"
                setuju.requestFocus()
            }
        }
    }

    private fun initTransaksi(tokenDevices : String, data : User) {
        val transactionDetails = PerpusMidtrans.TransactionDetails(
            order_id = "Perpus-${UUID.randomUUID()}",
            gross_amount = 15000
        )
        val customerDetails = PerpusMidtrans.CustomerDetails(
            id_user = data.user_id,
            role = data.role,
            name_user = data.nama,
            gender = data.gender,
            token_hp = tokenDevices
        )
        val requestBody = PerpusMidtrans(
            transaction_type = "Perpus",
            transaction_details = transactionDetails,
            customer_details = customerDetails
        )
        showLoadingDialog()
        ApiConfig.create(this).chargePerpus(requestBody).enqueue(object : Callback<ResponseMidtrans> {
            override fun onResponse(
                call: Call<ResponseMidtrans>,
                response: Response<ResponseMidtrans>
            ) {
                if (response.isSuccessful) {
                    hideLoadingDialog()
                    val snapToken = response.body()!!.token
                    tokenPembayaran = snapToken
                    startPayment(snapToken)
                } else {
                    hideLoadingDialog()
                    Toast.makeText(this@DaftarPerpusActivity, "Failed to get snap token", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseMidtrans>, t: Throwable) {
                hideLoadingDialog()
                t.printStackTrace()
                Log.e("BDR", "Request failed: ${t.message}")
                Toast.makeText(this@DaftarPerpusActivity, "Request failed", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun startPayment(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            this, // activity
            launcher, // ActivityResultLauncher
            snapToken // Snap token
        )
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

    private fun initMidtrans(){
        UiKitApi.Builder()
            .withMerchantClientKey(com.badrun.my259firdaus.BuildConfig.CLIENT_KEY) // client_key is mandatory
            .withContext(applicationContext) // context is mandatory
            .withMerchantUrl(com.badrun.my259firdaus.BuildConfig.BASE_URL) // set transaction finish callback (sdk callback)
            .enableLog(true) // enable sdk log (optional)
            .build()
        setLocaleNew("id")

    }

    private fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarAnggota)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }
}