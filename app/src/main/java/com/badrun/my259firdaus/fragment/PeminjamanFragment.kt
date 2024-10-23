package com.badrun.my259firdaus.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.DaftarPerpusActivity
import com.badrun.my259firdaus.activity.PembayaranDetailActivity
import com.badrun.my259firdaus.activity.PerpanjangActivity
import com.badrun.my259firdaus.adapter.PaymentAdapter
import com.badrun.my259firdaus.adapter.PeminjamanAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.DendaMidtrans
import com.badrun.my259firdaus.model.PeminjamanData
import com.badrun.my259firdaus.model.PerpusMidtrans
import com.badrun.my259firdaus.model.ResponseMidtrans
import com.badrun.my259firdaus.model.ResponsePeminjaman
import com.badrun.my259firdaus.model.User
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PeminjamanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PeminjamanFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var s : SharedPref
    private var dataList : List<PeminjamanData> = mutableListOf()
    private lateinit var peminjamanAdapter: PeminjamanAdapter
    private var errorDialog: AlertDialog? = null
    private var loadingDialog: AlertDialog? = null
    private var tokenPembayaran = ""

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("PembayaranActivity", "Activity result received")
        if (result?.resultCode == AppCompatActivity.RESULT_OK) {
            result.data?.let {
                val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                if (transactionResult?.status == "pending" || transactionResult?.status == "settlement") {
                    val intent = Intent(requireContext(), PembayaranDetailActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Transaction Invalid", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Log.d("PembayaranActivity", "Result data is null")
                Toast.makeText(requireContext(), "Data hasil transaksi null", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.d("PembayaranActivity", "Result code is not RESULT_OK: $result?.resultCode")
            Toast.makeText(requireContext(), "Transaksi Gagal atau Dibatalkan", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_peminjaman, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        s = SharedPref(requireActivity())
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_peminjaman)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        getDataPeminjaman()
        initMidtrans()

        // Inisialisasi adapter (tanpa data awal)
        peminjamanAdapter = PeminjamanAdapter(dataList){ peminjaman ->
            val status = peminjaman.status
            when(status){
                "Bayar"->{
                    bayarData(peminjaman)
                }
                "Diterima"->{
                    val tglPengembalian = peminjaman.tgl_pengembalian
                    val onlyOne = peminjaman.perpanjang
                    if (onlyOne == "Tidak") {
                        if (isTodayHMinusOne(tglPengembalian)) {
                            pengajuanPertambahan(
                                peminjaman.id,
                                peminjaman.judul_buku,
                                peminjaman.penulis,
                                peminjaman.tgl_peminjaman,
                                peminjaman.tgl_pengembalian
                            )
                        } else {
                            showDialog("Pengajuan Perpanjangan Tidak Dapat Dilakukan", "Hanya dapat mengajukan perpanjangan satu hari sebelum tanggal pengembalian.")
                        }
                    } else {
                        showDialog("Anda Sudah Memperpanjang Peminjaman Buku!!", "Perpanjangan Buku Sudah Tidak Bisa")
                    }
                }
            }

        }

        recyclerView.adapter = peminjamanAdapter

    }

    private fun isTodayHMinusOne(tglPengembalian: String): Boolean {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            // Parse tanggal pengembalian
            val datePengembalian = inputFormat.parse(tglPengembalian)

            // Gunakan Calendar untuk mendapatkan tanggal H-1
            val calendar = Calendar.getInstance().apply {
                time = datePengembalian
                add(Calendar.DAY_OF_MONTH, -1)
            }

            // Format H-1
            val hMinusOneDate = currentDateFormat.format(calendar.time)

            // Bandingkan dengan tanggal hari ini
            val todayDate = currentDateFormat.format(Calendar.getInstance().time)
            hMinusOneDate == todayDate
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun bayarData(data : PeminjamanData) {
        val transactionDetails = DendaMidtrans.TransactionDetails(
            order_id = "Denda-${UUID.randomUUID()}",
            gross_amount = data.denda
        )
        val customerDetails = DendaMidtrans.CustomerDetails(
            id_transaksi = data.id,
            name_user = data.nama_user,
            token_hp = data.hp
        )
        val requestBody = DendaMidtrans(
            transaction_type = "Denda",
            transaction_details = transactionDetails,
            customer_details = customerDetails
        )
        showLoadingDialog()
        ApiConfig.create(requireContext()).chargeDenda(requestBody).enqueue(object : Callback<ResponseMidtrans> {
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
                    Toast.makeText(requireContext(), "Failed to get snap token", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseMidtrans>, t: Throwable) {
                hideLoadingDialog()
                t.printStackTrace()
                Log.e("BDR", "Request failed: ${t.message}")
                Toast.makeText(requireContext(), "Request failed", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun startPayment(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            requireActivity(), // activity
            launcher, // ActivityResultLauncher
            snapToken // Snap token
        )
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
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

    private fun initMidtrans(){
        UiKitApi.Builder()
            .withMerchantClientKey(com.badrun.my259firdaus.BuildConfig.CLIENT_KEY) // client_key is mandatory
            .withContext(requireContext())
            .withMerchantUrl(com.badrun.my259firdaus.BuildConfig.BASE_URL) // set transaction finish callback (sdk callback)
            .enableLog(true) // enable sdk log (optional)
            .build()
        setLocaleNew("id")

    }

    private fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun pengajuanPertambahan(idPeminjaman:Int, judul:String, penulis:String, tanggalPeminjaman:String, tanggalPengembalian:String){
        val i = Intent(requireContext(), PerpanjangActivity::class.java)
        i.putExtra("id_peminjaman",idPeminjaman)
        i.putExtra("judul",judul)
        i.putExtra("penulis",penulis)
        i.putExtra("tgl_peminjaman",tanggalPeminjaman)
        i.putExtra("tgl_pengembalian", tanggalPengembalian)
        startActivity(i)
    }

    private fun getDataPeminjaman(){
        val user = s.getUser()
        ApiConfig.create(requireContext()).listPinjamBuku(user!!.user_id).enqueue(object : Callback<ResponsePeminjaman>{
            override fun onResponse(
                call: Call<ResponsePeminjaman>,
                response: Response<ResponsePeminjaman>
            ) {
                if(response.isSuccessful){
                    val res = response.body()
                    if (res!!.code == 1){
                        dataList = res.data
                        peminjamanAdapter.setData(dataList)
                    }
                }
            }

            override fun onFailure(call: Call<ResponsePeminjaman>, t: Throwable) {
                showDialogError()
            }
        })
    }

    private fun showDialogError() {
        if (errorDialog == null) {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
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

    private fun showDialog(message: String, title:String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PeminjamanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PeminjamanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}