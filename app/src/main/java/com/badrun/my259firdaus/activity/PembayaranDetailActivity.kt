package com.badrun.my259firdaus.activity

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.PaymentAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.DatabaseHelper
import com.badrun.my259firdaus.model.Payment
import com.badrun.my259firdaus.model.ResponseCheck
import com.badrun.my259firdaus.model.ResponseCheckPayment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PembayaranDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper : DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var paymentList: MutableList<Payment>
    private var bool : Boolean = false
    private var booldua : Boolean = false

    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("PembayaranActivity", "Activity result received")
        if (result?.resultCode == RESULT_OK) {
            Log.d("PembayaranActivity", "Result code is RESULT_OK")
            result.data?.let {
                val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                Log.d("PembayaranActivity", "Transaction result: ${transactionResult?.transactionId}, status: ${transactionResult?.status}")
                if (transactionResult != null) {

                } else {
                    Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
                    Log.d("PembayaranActivity", "Transaction result is null")
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
        setContentView(R.layout.activity_pembayaran_detail)

        initMidtrans()
        val sharedPreferences = getSharedPreferences("daftarUlang", MODE_PRIVATE)
        val aksi = sharedPreferences.getString("action", null)
        val statusPSB = sharedPreferences.getString("status", null)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.rv_payment)
        paymentList = mutableListOf()
        paymentAdapter = PaymentAdapter(paymentList) { payment ->
            if (payment.status == "Belum Dibayar") {
                continuePayment(payment.snapToken)
            } else if (payment.status == "Telah Dibayar"){
                showLoadingDialog()
                getPayment(payment) {isSucces ->
                    hideLoadingDialog()
                    if (payment.orderId.startsWith("PSB-")) {
                        if (!isSucces){
                            Toast.makeText(this, "Gagal mendapatkan informasi pembayaran", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }

        initBtnBack()
        loadPayments()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = paymentAdapter
    }

    private fun getPayment(payment: Payment, callback: (Boolean) -> Unit) {
        val idOrder = payment.orderId
        ApiConfig.create(this).getPayment(idOrder).enqueue(object : Callback<ResponseCheckPayment> {
            override fun onResponse(call: Call<ResponseCheckPayment>, response: Response<ResponseCheckPayment>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    if (res.code == 1) { // Payment found
                        val formulirExists = res.formulir_exists
                        val portoExists = res.porto_exists

                        if (formulirExists) {
                            showDialogWithMessage("Anda Sudah Melakukan Daftar Ulang","Silahkan Login ke akun santri yang telah dikirimkan")
                        } else if (portoExists) {
                            val i = Intent(this@PembayaranDetailActivity, DaftarUlangActivity::class.java)
                            startActivity(i)
                        } else  {
                            generateQRCode(payment)
                        }
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ResponseCheckPayment>, t: Throwable) {
                Log.e("bdr", "onFailure: Gagal", t)
                callback(false)
            }
        })
    }

    private fun showDialogWithMessage(title: String, message: String) {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle(title)
            setIcon(R.drawable.icon_succes)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun generateQRCode(payment: Payment) {
        val qrCodeContent = payment.orderId // You can use any content here
        val width = 500
        val height = 500
        val bitmap = createQRCodeBitmap(qrCodeContent, width, height)

        // Assuming you have an ImageView to display the QR code
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)

        // Display QR code in a dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("QR Code For Testing")
        builder.setMessage("Perlihatkan Qr Code ini ke Penguji nanti")
        builder.setView(imageView)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.dialog_loading, null)
            builder.setView(view)
            builder.setCancelable(false)
            loadingDialog = builder.create()
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private fun createQRCodeBitmap(content: String, width: Int, height: Int): Bitmap {
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    private fun continuePayment(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            this,
            resultLauncher,
            snapToken
        )
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


    private fun loadPayments() {
        val cursor: Cursor = dbHelper.allPayments
        paymentList.clear()
        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ID))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS))
                val grossAmount = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GROSS_AMOUNT))
                val paymentType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_TYPE))
                val timeOrder = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME_ORDER))
                val expiryOrder = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPIRY_ORDER))
                val snapToken = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLOMN_SNAP_TOKEN))
                val payment = Payment(orderId, status, grossAmount,paymentType,timeOrder,expiryOrder, snapToken)
                paymentList.add(payment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        paymentAdapter.notifyDataSetChanged()
    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarFormulir)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }
}