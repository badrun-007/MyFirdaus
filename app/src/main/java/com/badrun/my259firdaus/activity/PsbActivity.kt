package com.badrun.my259firdaus.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.os.LocaleListCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.CustomSpinnerAdapter
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.helper.numberFormat
import com.badrun.my259firdaus.model.Desa
import com.badrun.my259firdaus.model.Kabupaten
import com.badrun.my259firdaus.model.Kecamatan
import com.badrun.my259firdaus.model.MidtransRequest
import com.badrun.my259firdaus.model.Provinsi
import com.badrun.my259firdaus.model.Psb
import com.badrun.my259firdaus.model.ResponseMidtrans
import com.badrun.my259firdaus.model.ResponsePsb
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.util.Calendar
import java.util.UUID


class PsbActivity : AppCompatActivity() {

    companion object {
        private const val PICK_PDF_REQUEST_PRESTASI = 1004
        private const val PICK_PHOTO_REQUEST = 1005
    }

    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    private lateinit var fileFoto: File
    private lateinit var filePrestasi: File

    private lateinit var pb : ProgressBar
    private lateinit var checkboxAlamat: CheckBox
    private lateinit var checkBoxAlamatIbu : CheckBox
    private lateinit var alamatSantri : EditText
    private lateinit var alamatAyah : EditText
    private lateinit var alamatIbu : EditText
    private lateinit var spinnerProvinsiAyah : Spinner
    private lateinit var spinnerKabupatenAyah : Spinner
    private lateinit var spinnerKecamatanAyah : Spinner
    private lateinit var spinnerDesaAyah : Spinner
    private lateinit var spinnerProvinsiIbu : Spinner
    private lateinit var spinnerKabupatenIbu : Spinner
    private lateinit var spinnerKecamatanIbu : Spinner
    private lateinit var spinnerDesaIbu : Spinner

    private lateinit var buttonChooseFoto : Button
    private lateinit var buttonChoosePrestasi : Button
    private lateinit var buttonKirim : Button

    //santri
    private lateinit var namaSantri:EditText
    private lateinit var nikSantri :EditText
    private lateinit var nisnSantri :EditText
    private lateinit var tempatLahirSantri :EditText
    private lateinit var tanggallahirSantri: TextInputEditText
    private lateinit var anakKe : EditText
    private lateinit var saudara : EditText
    private lateinit var gender : RadioGroup
    private lateinit var sekolahAsal : EditText
    private lateinit var alamatSekolahAsal :EditText

    //prestasi
    private lateinit var prestasiSantri : EditText
    private lateinit var prestasiPenyelenggara : EditText
    private lateinit var jenisPrestasi : Spinner
    private lateinit var prestasiTingkat : Spinner
    private lateinit var prestasiJuara : Spinner
    private lateinit var prestasiTanggal : TextInputEditText

    //ayah
    private lateinit var namaAyah : EditText
    private lateinit var nikAyah :EditText
    private lateinit var tempatLahirAyah :EditText
    private lateinit var tanggallahirAyah: TextInputEditText
    private lateinit var noHpAyah : EditText
    private lateinit var pendidikanAyah : Spinner
    private lateinit var pekerjaanAyah : Spinner
    private lateinit var penghasilanAyah :EditText

    //ibu
    private lateinit var namaIbu : EditText
    private lateinit var nikIbu :EditText
    private lateinit var tempatLahirIbu :EditText
    private lateinit var tanggallahirIbu: TextInputEditText
    private lateinit var noHpIbu : EditText
    private lateinit var pendidikanIbu : Spinner
    private lateinit var pekerjaanIbu : Spinner
    private lateinit var penghasilanIbu :EditText

    private var provinsiList = mutableListOf<Provinsi>()
    private var kabupatenList = mutableListOf<Kabupaten>()
    private var kecamatanList = mutableListOf<Kecamatan>()
    private var desaList = mutableListOf<Desa>()
    private lateinit var provinsiAdapter: ArrayAdapter<String>
    private lateinit var kabupatenAdapter: ArrayAdapter<String>
    private lateinit var kecamatanAdapter: ArrayAdapter<String>
    private lateinit var desaAdapter: ArrayAdapter<String>
    private lateinit var spinnerProvinsi: Spinner
    private lateinit var spinnerKabupaten: Spinner
    private lateinit var spinnerKecamatan : Spinner
    private lateinit var spinnerDesa : Spinner
    private val apiAlamat = "https://badrun-007.github.io/api-wilayah-indonesia/api/"

    private lateinit var s : SharedPref

    //pembayaran
    private var tokenPembayaran : String = ""
    private var tokenDevices = ""

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
        setContentView(R.layout.activity_psb)

        s = SharedPref(this)
        initId()
        initView()

        noHpAyah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (!text.startsWith("08")) {
                    noHpAyah.setText("08")
                    noHpAyah.setSelection(noHpAyah.text.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        noHpIbu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (!text.startsWith("08")) {
                    noHpIbu.setText("08")
                    noHpIbu.setSelection(noHpIbu.text.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        fileFoto = createEmptyFile("tidakadaFile")
        filePrestasi = createEmptyFile("tidakadaFile")
        initBtnBack()
        initButtonFile()
        initTingkatPrestasi()

        // Memuat provinsi dari API
        getProvinsi()
        initPendidikan()
        initPekerjaan()
        initCheckBox()

        buttonKirim.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.apply {
                setTitle("Konfirmasi")
                setMessage("Apakah Anda yakin ingin mengirim formulir?")
                setPositiveButton("Yakin") { dialog, _ ->
                    //setupload
                    setFormulirPsb()
                    dialog.dismiss()
                }
                setNegativeButton("Cek Kembali") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        //pembayaran
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("BDR", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            tokenDevices = task.result
        })
        initMidtrans()

    }

    private fun initView(){
        val layoutBiodataSantri = findViewById<LinearLayout>(R.id.layout_biodata_santri)
        val layoutPrestasiSantri = findViewById<LinearLayout>(R.id.layout_prestasi_santri)
        val layoutBiodataAyah = findViewById<LinearLayout>(R.id.layout_biodata_ayah)
        val layoutBiodataIbu = findViewById<LinearLayout>(R.id.layout_biodata_ibu)

        val buttonNext = findViewById<Button>(R.id.button_next)
        val buttonPrevious = findViewById<Button>(R.id.button_previous)

        buttonNext.setOnClickListener {
            when {
                layoutBiodataSantri.visibility == View.VISIBLE -> {
                    val selectedID = gender.checkedRadioButtonId
                    var bool = true
                    if (selectedID != -1) {
                        bool = true
                    } else {
                        Toast.makeText(this, "Gender Calon santri tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        gender.requestFocus()
                        bool = false
                    }
                    val isValid = isNotEmpty(namaSantri, "Nama Santri tidak boleh kosong") &&
                            isNotEmptyNik(nikSantri, "NIK Santri tidak boleh kosong") &&
                            isNotEmpty(nisnSantri, "NISN Santri tidak boleh kosong") &&
                            isNotEmpty(tempatLahirSantri, "Tempat Lahir Santri tidak boleh kosong") &&
                            isNotEmptyEdit(tanggallahirSantri, "Tanggal Lahir Santri tidak boleh kosong", this) &&
                            isNotEmpty(anakKe, "Anak Ke tidak boleh kosong") &&
                            isNotEmpty(saudara, "Saudara tidak boleh kosong") &&
                            bool && //validasi gender
                            isNotEmpty(alamatSantri, "Alamat Santri tidak boleh kosong") &&
                            isSpinnerValid(spinnerProvinsi, "PROVINSI", "Provinsi Santri tidak boleh kosong", this) &&
                            isSpinnerValid(spinnerKabupaten, "KABUPATEN", "Kabupaten Santri tidak boleh kosong", this) &&
                            isSpinnerValid(spinnerKecamatan, "KECAMATAN", "Kecamatan Santri tidak boleh kosong", this) &&
                            isSpinnerValid(spinnerDesa, "DESA", "Desa Santri tidak boleh kosong", this) &&
                            isNotEmpty(sekolahAsal, "Sekolah Asal tidak boleh kosong") &&
                            isNotEmpty(alamatSekolahAsal, "Alamat Sekolah Asal tidak boleh kosong") &&
                            isFileValid(fileFoto, "File foto tidak boleh kosong", this)
                    if (isValid){
                        layoutBiodataSantri.visibility = View.GONE
                        layoutPrestasiSantri.visibility = View.VISIBLE
                        buttonPrevious.visibility = View.VISIBLE
                    }

                }
                layoutPrestasiSantri.visibility == View.VISIBLE -> {
                    layoutPrestasiSantri.visibility = View.GONE
                    layoutBiodataAyah.visibility = View.VISIBLE
                    buttonPrevious.visibility = View.VISIBLE
                }
                layoutBiodataAyah.visibility == View.VISIBLE -> {
                    val isValid = isNotEmpty(namaAyah, "Nama Ayah tidak boleh kosong") &&
                            isNotEmptyNik(nikAyah, "NIK Ayah tidak boleh kosong") &&
                            isNotEmpty(tempatLahirAyah, "Tempat Lahir Ayah tidak boleh kosong") &&
                            isNotEmptyEdit(tanggallahirAyah, "Tanggal Lahir Ayah tidak boleh kosong", this) &&
                            isSpinnerValid(pendidikanAyah,"Pendidikan Terakhir","Pendidikan Ayah Tidak Boleh Kosong", this) &&
                            isSpinnerValid(pekerjaanAyah,"Pekerjaan","Pekerjaan Ayah Tidak Boleh Kosong", this) &&
                            isNotEmpty(alamatAyah, "Alamat Santri tidak boleh kosong") &&
                            isSpinnerValid(spinnerProvinsiAyah, "PROVINSI", "Provinsi Ayah tidak boleh kosong", this) &&
                            isSpinnerValid(spinnerKabupatenAyah, "KABUPATEN", "Kabupaten Ayah tidak boleh kosong", this) &&
                            isSpinnerValid(spinnerKecamatanAyah, "KECAMATAN","Kecamatan Ayah tidak boleh kosong", this)&&
                            isSpinnerValid(spinnerDesaAyah, "DESA", "Desa Ayah tidak boleh kosong", this)

                    if(isValid){
                        layoutBiodataAyah.visibility = View.GONE
                        layoutBiodataIbu.visibility = View.VISIBLE
                        buttonPrevious.visibility = View.VISIBLE
                    }
                }
                layoutBiodataIbu.visibility == View.VISIBLE -> {
                    var bool = true

                    if(noHpAyah.text.isEmpty() && noHpIbu.text.isEmpty()){
                        noHpAyah.error = "Nomor HP Ayah dan Ibu tidak boleh kosong minimal isi salah satu"
                        noHpIbu.requestFocus()
                        bool = false
                    } else {
                        if(noHpAyah.text.isEmpty()){
                            noHpAyah.setText("0")
                        }
                        if(noHpIbu.text.isEmpty()){
                            noHpIbu.setText("0")
                        }
                    }

                    if (penghasilanAyah.text.isEmpty() && penghasilanIbu.text.isEmpty()) {
                        penghasilanAyah.error = "Penghasilan Ayah dan Ibu tidak boleh kosong minimal isi salah satu"
                        penghasilanAyah.requestFocus()
                        bool = false
                    } else {
                        if (penghasilanAyah.text.isEmpty()) {
                            penghasilanAyah.setText("0")
                        }
                        if (penghasilanIbu.text.isEmpty()) {
                            penghasilanIbu.setText("0")
                        }
                    }

                    val isValid = isNotEmpty(namaIbu, "Nama Ibu tidak boleh kosong") &&
                            isNotEmptyNik(nikIbu, "NIK Ibu tidak boleh kosong") &&
                            isNotEmpty(tempatLahirIbu, "Tempat Lahir Ibu tidak boleh kosong") &&
                            isNotEmptyEdit(tanggallahirIbu, "Tanggal Lahir Ibu tidak boleh kosong", this) &&
                            isSpinnerValid(pendidikanIbu,"Pilih Pendidikan Terakhir Ayah","Pendidikan Ayah Tidak Boleh Kosong", this) &&
                            isSpinnerValid(pekerjaanIbu,"Pilih Pekerjaan Ayah","Pekerjaan Ayah Tidak Boleh Kosong", this) &&
                            bool && //Pekerjaan dan No HP Ayah dan Ibu
                            isSpinnerValid(spinnerProvinsiIbu, "Pilih Provinsi", "Provinsi Ibu tidak boleh kosong", this)&&
                            isSpinnerValid(spinnerKabupatenIbu, "Pilih Kabupaten", "Kabupaten Ibu tidak boleh kosong", this) &&
                            isSpinnerValid(spinnerKecamatanIbu, "Pilih Kecamatan","Kecamatan Ibu tidak boleh kosong", this)&&
                            isSpinnerValid(spinnerDesaIbu, "Pilih Desa", "Desa Ibu tidak boleh kosong", this)


                    if (isValid){
                        buttonPrevious.visibility = View.GONE
                        buttonNext.visibility = View.GONE
                        buttonKirim.visibility = View.VISIBLE
                    }
                }
            }
        }

        buttonPrevious.setOnClickListener {
            when {
                layoutPrestasiSantri.visibility == View.VISIBLE -> {
                    layoutPrestasiSantri.visibility = View.GONE
                    layoutBiodataSantri.visibility = View.VISIBLE
                    buttonPrevious.visibility = View.GONE
                }
                layoutBiodataAyah.visibility == View.VISIBLE -> {
                    layoutBiodataAyah.visibility = View.GONE
                    layoutPrestasiSantri.visibility = View.VISIBLE
                    buttonNext.visibility = View.VISIBLE
                }
                layoutBiodataIbu.visibility == View.VISIBLE -> {
                    layoutBiodataIbu.visibility = View.GONE
                    layoutBiodataAyah.visibility = View.VISIBLE
                    buttonPrevious.visibility = View.VISIBLE
                    buttonKirim.visibility = View.GONE
                }
            }
        }
    }

    private fun initId (){
        buttonKirim = findViewById(R.id.button_kirim)
        tanggallahirSantri = findViewById(R.id.tgl_lahir_santri_formulir)
        tanggallahirSantri.setOnClickListener {
            showDatePickerDialogSantri(tanggallahirSantri)
        }
        tanggallahirAyah = findViewById(R.id.tgl_lahir_ayah_formulir)
        tanggallahirAyah.setOnClickListener {
            showDatePickerDialog(tanggallahirAyah)
        }
        tanggallahirIbu = findViewById(R.id.tgl_lahir_ibu_formulir)
        tanggallahirIbu.setOnClickListener {
            showDatePickerDialog(tanggallahirIbu)
        }

        prestasiTanggal = findViewById(R.id.tgl_prestasi_formulir)
        prestasiTanggal.setOnClickListener {
            showDatePickerDialog(prestasiTanggal)
        }

        jenisPrestasi = findViewById(R.id.jenis_prestasi)
        prestasiSantri = findViewById(R.id.prestasi_santri_formulir)
        prestasiPenyelenggara = findViewById(R.id.penyelenggara_prestasi_santri)
        prestasiTingkat = findViewById(R.id.spinner_tingkat_prestasi)
        prestasiJuara = findViewById(R.id.spinner_juara_prestasi)

        penghasilanAyah = findViewById(R.id.penghasilan_ayah_formulir)
        penghasilanIbu = findViewById(R.id.penghasilan_ibu_formulir)
        penghasilanAyah.addTextChangedListener(numberFormat(penghasilanAyah))
        penghasilanIbu.addTextChangedListener(numberFormat(penghasilanIbu))
        checkboxAlamat = findViewById(R.id.checkbox_alamat_sama)
        checkBoxAlamatIbu = findViewById(R.id.checkbox_alamat_sama_ibu)
        alamatSantri = findViewById(R.id.alamat_santri_formulir)
        alamatAyah = findViewById(R.id.alamat_ayah_formulir)
        alamatIbu = findViewById(R.id.alamat_ibu_formulir)

        spinnerProvinsi = findViewById(R.id.spinner_provinsi)
        spinnerProvinsiAyah = findViewById(R.id.spinner_provinsi_ayah)
        spinnerProvinsiIbu = findViewById(R.id.spinner_provinsi_ibu)
        spinnerKabupaten = findViewById(R.id.spinner_kabupaten)
        spinnerKabupatenAyah = findViewById(R.id.spinner_kabupaten_ayah)
        spinnerKabupatenIbu = findViewById(R.id.spinner_kabupaten_ibu)
        spinnerKecamatan = findViewById(R.id.spinner_kecamatan)
        spinnerKecamatanAyah = findViewById(R.id.spinner_kecamatan_ayah)
        spinnerKecamatanIbu = findViewById(R.id.spinner_kecamatan_ibu)
        spinnerDesa = findViewById(R.id.spinner_desa)
        spinnerDesaAyah = findViewById(R.id.spinner_desa_ayah)
        spinnerDesaIbu = findViewById(R.id.spinner_desa_ibu)

        //santri
        namaSantri = findViewById(R.id.nama_santri_formulir)
        nikSantri = findViewById(R.id.nik_santri_formulir)
        nisnSantri = findViewById(R.id.nisn_santri_formulir)
        tempatLahirSantri = findViewById(R.id.tmpt_lahir_santri_formulir)
        anakKe = findViewById(R.id.anak_ke_formulir)
        saudara = findViewById(R.id.saudara_formulir)
        gender = findViewById(R.id.genderSantri)
        sekolahAsal = findViewById(R.id.sekolah_asal_formulir)
        alamatSekolahAsal= findViewById(R.id.alamat_sekolah_asal_formulir)

        //ayah
        namaAyah = findViewById(R.id.nama_ayah_formulir)
        nikAyah = findViewById(R.id.nik_ayah_formulir)
        tempatLahirAyah = findViewById(R.id.tmpt_lahir_ayah_formulir)
        noHpAyah = findViewById(R.id.no_ayah_formulir)
        pendidikanAyah = findViewById(R.id.spinner_pendidikan_ayah)
        pekerjaanAyah = findViewById(R.id.spinner_pekerjaan_ayah)

        //ibu
        namaIbu = findViewById(R.id.nama_ibu_formulir)
        nikIbu = findViewById(R.id.nik_ibu_formulir)
        tempatLahirIbu = findViewById(R.id.tmpt_lahir_ibu_formulir)
        noHpIbu = findViewById(R.id.no_ibu_formulir)
        pendidikanIbu = findViewById(R.id.spinner_pendidikan_ibu)
        pekerjaanIbu =findViewById(R.id.spinner_pekerjaan_ibu)

        //button kirim file
        buttonChooseFoto = findViewById(R.id.button_choose_foto)
        buttonChoosePrestasi = findViewById(R.id.button_choose_prestasi)
    }

    private fun isNotEmpty(editText: EditText, errorMessage: String): Boolean {
        return if (editText.text.toString().trim().isEmpty()) {
            editText.error = errorMessage
            editText.requestFocus()
            false
        } else {
            true
        }
    }

    private fun isNotEmptyEdit(editText: EditText, errorMessage: String, context: Context): Boolean {
        return if (editText.text.toString().trim().isEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            editText.error = errorMessage
            editText.requestFocus()
            false
        } else {
            true
        }
    }

    private fun isNotEmptyNik(editText: EditText, errorMessage: String,): Boolean {
        return if (editText.text.toString().trim().isEmpty()) {
            editText.error = errorMessage
            editText.requestFocus()
            false
        } else if(editText.text.toString().count() > 16){
            editText.error = "NIK terlalu panjang"
            editText.requestFocus()
            false
        } else if(editText.text.toString().count() < 16){
            editText.error = "NIK terlalu pendek"
            editText.requestFocus()
            false
        } else {
            true
        }
    }

    private fun isFileValid(file: File?, errorMessage: String, context: Context): Boolean {
        return if (file == null || !file.exists() || file.name == "tidakadaFile") {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun isSpinnerValid(spinner: Spinner, defaultValue: String, errorMessage: String, context: Context): Boolean {
        return if (spinner.selectedItem.toString() == defaultValue) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            spinner.requestFocus()
            false
        } else {
            true
        }
    }

    private fun capitalizeWords(input: String): String {
        return input.toLowerCase().split(" ").joinToString(" ") { it.capitalize() }
    }

    private fun setFormulirPsb(){
        var isValid = true

        val user = s.getUser()
        val selectedID = gender.checkedRadioButtonId
        val genderSantri : String

        if (selectedID != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedID)
            genderSantri = selectedRadioButton.text.toString()
        } else {
            Toast.makeText(this, "Gender Calon santri tidak boleh kosong", Toast.LENGTH_SHORT).show()
            gender.requestFocus()
            genderSantri = ""
            isValid = false
        }

        if(noHpAyah.text.isEmpty() && noHpIbu.text.isEmpty()){
            noHpAyah.error = "Nomor HP Ayah dan Ibu tidak boleh kosong minimal isi salah satu"
            noHpIbu.requestFocus()
            isValid = false
        } else {
            if(noHpAyah.text.isEmpty()){
                noHpAyah.setText("0")
            }
            if(noHpIbu.text.isEmpty()){
                noHpIbu.setText("0")
            }
        }

        if (penghasilanAyah.text.isEmpty() && penghasilanIbu.text.isEmpty()) {
            penghasilanAyah.error = "Penghasilan Ayah dan Ibu tidak boleh kosong minimal isi salah satu"
            penghasilanAyah.requestFocus()
            isValid = false
        } else {
            if (penghasilanAyah.text.isEmpty()) {
                penghasilanAyah.setText("0")
            }
            if (penghasilanIbu.text.isEmpty()) {
                penghasilanIbu.setText("0")
            }
        }

        val desaSantri = capitalizeWords(spinnerDesa.selectedItem.toString())
        val desaAyah = capitalizeWords(spinnerDesaAyah.selectedItem.toString())
        val desaIbu = capitalizeWords(spinnerDesaIbu.selectedItem.toString())
        val kabupatenSantri = capitalizeWords(spinnerKabupaten.selectedItem.toString())
        val kabupatenAyah = capitalizeWords(spinnerKabupatenAyah.selectedItem.toString())
        val kabupatenIbu = capitalizeWords(spinnerKabupatenIbu.selectedItem.toString())
        val provinsiSantri = capitalizeWords(spinnerProvinsi.selectedItem.toString())
        val provinsiAyah = capitalizeWords(spinnerProvinsiAyah.selectedItem.toString())
        val provinsiIbu = capitalizeWords(spinnerProvinsiIbu.selectedItem.toString())
        val alamatSantriText = "${alamatSantri.text} Desa $desaSantri $kabupatenSantri Provinsi $provinsiSantri"
        val alamatAyah = "${alamatAyah.text} Desa $desaAyah $kabupatenAyah Provinsi $provinsiAyah"
        val alamatIbu = "${alamatIbu.text} Desa $desaIbu $kabupatenIbu Provinsi $provinsiIbu"
        val penghasilanAyahServer = penghasilanAyah.text.toString().replace(".", "")
        val penghasilanIbuServer = penghasilanIbu.text.toString().replace(".", "")

        isValid = isNotEmpty(namaSantri, "Nama Santri tidak boleh kosong") &&
                isNotEmptyNik(nikSantri, "NIK Santri tidak boleh kosong") &&
                isNotEmpty(nisnSantri, "NISN Santri tidak boleh kosong") &&
                isNotEmpty(tempatLahirSantri, "Tempat Lahir Santri tidak boleh kosong") &&
                isNotEmptyEdit(tanggallahirSantri, "Tanggal Lahir Santri tidak boleh kosong",this) &&
                isNotEmpty(anakKe, "Anak Ke tidak boleh kosong") &&
                isNotEmpty(saudara, "Saudara tidak boleh kosong") &&
                isNotEmpty(alamatSantri, "Alamat Santri tidak boleh kosong")&&
                isSpinnerValid(spinnerProvinsi, "Pilih Provinsi", "Provinsi Santri tidak boleh kosong", this) &&
                isSpinnerValid(spinnerKabupaten, "Pilih Kabupaten", "Kabupaten Santri tidak boleh kosong", this) &&
                isSpinnerValid(spinnerKecamatan, "Pilih Kecamatan", "Kecamatan Santri tidak boleh kosong",this)&&
                isSpinnerValid(spinnerDesa, "Pilih Desa", "Desa Santri tidak boleh kosong", this) &&
                isNotEmpty(sekolahAsal, "Sekolah Asal tidak boleh kosong") &&
                isNotEmpty(alamatSekolahAsal, "Alamat Sekolah Asal tidak boleh kosong") &&
                isNotEmpty(namaAyah, "Nama Ayah tidak boleh kosong") &&
                isNotEmptyNik(nikAyah, "NIK Ayah tidak boleh kosong") &&
                isNotEmpty(tempatLahirAyah, "Tempat Lahir Ayah tidak boleh kosong") &&
                isNotEmptyEdit(tanggallahirAyah, "Tanggal Lahir Ayah tidak boleh kosong", this) &&
                isSpinnerValid(pendidikanAyah,"Pilih Pendidikan Terakhir Ayah","Pendidikan Ayah Tidak Boleh Kosong", this) &&
                isSpinnerValid(pekerjaanAyah,"Pilih Pekerjaan Ayah","Pekerjaan Ayah Tidak Boleh Kosong", this) &&
                isSpinnerValid(spinnerProvinsiAyah, "Pilih Provinsi", "Provinsi Ayah tidak boleh kosong", this) &&
                isSpinnerValid(spinnerKabupatenAyah, "Pilih Kabupaten", "Kabupaten Ayah tidak boleh kosong", this) &&
                isSpinnerValid(spinnerKecamatanAyah, "Pilih Kecamatan","Kecamatan Ayah tidak boleh kosong", this)&&
                isSpinnerValid(spinnerDesaAyah, "Pilih Desa", "Desa Ayah tidak boleh kosong", this) &&
                isValid &&
                isNotEmpty(namaIbu, "Nama Ibu tidak boleh kosong") &&
                isNotEmptyNik(nikIbu, "NIK Ibu tidak boleh kosong") &&
                isNotEmpty(tempatLahirIbu, "Tempat Lahir Ibu tidak boleh kosong") &&
                isNotEmptyEdit(tanggallahirIbu, "Tanggal Lahir Ibu tidak boleh kosong", this) &&
                isSpinnerValid(pendidikanIbu,"Pilih Pendidikan Terakhir Ayah","Pendidikan Ayah Tidak Boleh Kosong", this) &&
                isSpinnerValid(pekerjaanIbu,"Pilih Pekerjaan Ayah","Pekerjaan Ayah Tidak Boleh Kosong", this) &&
                isSpinnerValid(spinnerProvinsiIbu, "Pilih Provinsi", "Provinsi Ibu tidak boleh kosong", this)&&
                isSpinnerValid(spinnerKabupatenIbu, "Pilih Kabupaten", "Kabupaten Ibu tidak boleh kosong", this) &&
                isSpinnerValid(spinnerKecamatanIbu, "Pilih Kecamatan","Kecamatan Ibu tidak boleh kosong", this)&&
                isSpinnerValid(spinnerDesaIbu, "Pilih Desa", "Desa Ibu tidak boleh kosong", this) &&
                isFileValid(fileFoto, "File foto tidak boleh kosong", this)
        if(isValid){
            val idUserRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), user!!.user_id.toString())
            val namaSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), namaSantri.text.toString())
            val nikSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nikSantri.text.toString())
            val nisnSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nisnSantri.text.toString())
            val tempatLahirSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tempatLahirSantri.text.toString())
            val tanggallahirSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tanggallahirSantri.text.toString())
            val anakKeRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), anakKe.text.toString())
            val saudaraRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), saudara.text.toString())
            val genderSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), genderSantri)
            val alamatSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), alamatSantriText)
            val sekolahAsalRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), sekolahAsal.text.toString())
            val alamatSekolahAsalRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), alamatSekolahAsal.text.toString())
            val namaAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), namaAyah.text.toString())
            val nikAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nikAyah.text.toString())
            val tempatLahirAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tempatLahirAyah.text.toString())
            val tanggallahirAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tanggallahirAyah.text.toString())
            val noHpAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), noHpAyah.text.toString())
            val pendidikanAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), pendidikanAyah.selectedItem.toString())
            val pekerjaanAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), pekerjaanAyah.selectedItem.toString())
            val penghasilanAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), penghasilanAyahServer)
            val alamatAyahRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), alamatAyah)
            val namaIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), namaIbu.text.toString())
            val nikIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nikIbu.text.toString())
            val tempatLahirIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tempatLahirIbu.text.toString())
            val tanggallahirIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tanggallahirIbu.text.toString())
            val noHpIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), noHpIbu.text.toString())
            val pendidikanIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), pendidikanIbu.selectedItem.toString())
            val pekerjaanIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), pekerjaanIbu.selectedItem.toString())
            val penghasilanIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), penghasilanIbuServer)
            val alamatIbuRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), alamatIbu)

            val prestasiSantriText = prestasiSantri.text.toString().takeIf { it.isNotEmpty() } ?: "Tidak Ada"
            val jenisPrestasiText = jenisPrestasi.selectedItem?.toString()?.takeIf { it.isNotEmpty() } ?: ""
            val penyelenggaraPrestasiText = prestasiPenyelenggara.text.toString().takeIf { it.isNotEmpty() } ?: " "
            val tingkatPrestasiText = prestasiTingkat.selectedItem?.toString()?.takeIf { it.isNotEmpty() } ?: " "
            val juaraPrestasiText = prestasiJuara.selectedItem?.toString()?.takeIf { it.isNotEmpty() } ?: " "
            val tanggalPrestasiText = prestasiTanggal.text.toString().takeIf { it.isNotEmpty() } ?: " "

            val prestasiSantriRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), prestasiSantriText)
            val jenisPrestasiRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), jenisPrestasiText)
            val penyelenggaraPrestasiRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), penyelenggaraPrestasiText)
            val tingkatPrestasiRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tingkatPrestasiText)
            val juaraPrestasiRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), juaraPrestasiText)
            val tanggalPrestasiRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tanggalPrestasiText)

            val fileFotoRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), fileFoto)
            val partFileFoto = MultipartBody.Part.createFormData("foto_santri", fileFoto.name, fileFotoRequestBody)

            val filePrestasiRequestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), filePrestasi)
            val partFilePrestasi = MultipartBody.Part.createFormData("file_prestasi", filePrestasi.name, filePrestasiRequestBody)

            showLoadingDialog()
            ApiConfig.create(this).setPsb(
                idUserRequestBody,
                namaSantriRequestBody,
                nikSantriRequestBody,
                nisnSantriRequestBody,
                tempatLahirSantriRequestBody,
                tanggallahirSantriRequestBody,
                anakKeRequestBody,
                saudaraRequestBody,
                genderSantriRequestBody,
                alamatSantriRequestBody,
                sekolahAsalRequestBody,
                alamatSekolahAsalRequestBody,
                namaAyahRequestBody,
                nikAyahRequestBody,
                tempatLahirAyahRequestBody,
                tanggallahirAyahRequestBody,
                noHpAyahRequestBody,
                pendidikanAyahRequestBody,
                pekerjaanAyahRequestBody,
                penghasilanAyahRequestBody,
                alamatAyahRequestBody,
                namaIbuRequestBody,
                nikIbuRequestBody,
                tempatLahirIbuRequestBody,
                tanggallahirIbuRequestBody,
                noHpIbuRequestBody,
                pendidikanIbuRequestBody,
                pekerjaanIbuRequestBody,
                penghasilanIbuRequestBody,
                alamatIbuRequestBody,
                prestasiSantriRequestBody,
                jenisPrestasiRequestBody,
                penyelenggaraPrestasiRequestBody,
                tingkatPrestasiRequestBody,
                juaraPrestasiRequestBody,
                tanggalPrestasiRequestBody,
                partFileFoto,
                partFilePrestasi
            ).enqueue(object : Callback<ResponsePsb> {
                override fun onResponse(call: Call<ResponsePsb>, response: Response<ResponsePsb>) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        if (res != null && res.code == 1) {
                            hideLoadingDialog()
                            Toast.makeText(this@PsbActivity, "Upload Data Berhasil!", Toast.LENGTH_SHORT).show()
                            initTransaksi(tokenDevices, res.data)
//                        val i = Intent(this@PsbActivity,PembayaranActivity::class.java)
//                        val dataPsb = res.data
//                        i.putExtra("dataPSB", dataPsb)
//                        startActivity(i)
//                        finish()
                            fileFoto.delete()

                        } else {
                            hideLoadingDialog()
                            Log.e("BDR", "onResponse code != 1: ${response.message()}")
                        }
                    } else {
                        hideLoadingDialog()
                        showDialogError()
                        Log.e("BDR", "onResponse unsuccessful: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponsePsb>, t: Throwable) {
                    hideLoadingDialog()
                    showDialogError()
                    Log.e("BDR", "onFailure: ${t.message}")
                }
            })
        }
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


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri = data.data!!
            val namaFile = getFileName(uri)

            when (requestCode) {
                PICK_PHOTO_REQUEST -> {
                    buttonChooseFoto.text = namaFile
                    fileFoto = saveFileToInternalStorage(uri, "foto")
                }
                PICK_PDF_REQUEST_PRESTASI -> {
                    buttonChoosePrestasi.text = namaFile
                    filePrestasi = saveFileToInternalStorage(uri, "prestasi")
                }
            }
        }
    }

    private fun createEmptyFile(fileName: String): File {
        val file = File(filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    private fun saveFileToInternalStorage(uri: Uri, fileNamePrefix: String): File {
        val inputStream = contentResolver.openInputStream(uri)
        val fileExtension = getFileExtension(uri)
        val file = File(getExternalFilesDir(null), "$fileNamePrefix-${System.currentTimeMillis()}.$fileExtension") // File disimpan dengan ekstensi yang sama seperti file aslinya
        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }

    private fun getFileExtension(uri: Uri): String {
        val contentTypeResolver = contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentTypeResolver)
            ?: uri.path?.substringAfterLast('.') ?: "tmp" // Jika tidak dapat menemukan ekstensi, gunakan "tmp" sebagai default
    }

    private fun getFileName(uri: Uri): String {
        var result = ""
        if (uri.scheme.equals("content")) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex("_display_name")
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        return result
    }

    private fun initButtonFile(){
        buttonChooseFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" // Menentukan tipe file gambar yang dapat dipilih
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Pilih Foto Santri"), PICK_PHOTO_REQUEST)
        }
        buttonChoosePrestasi.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Pilih File Prestasi"), PICK_PDF_REQUEST_PRESTASI)
        }

    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarFormulir)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }

    private fun initCheckBox(){
        checkboxAlamat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (alamatSantri.text.isNotEmpty() && spinnerProvinsi.selectedItemPosition != 0) {
                    alamatAyah.text = alamatSantri.text
                    spinnerProvinsiAyah.setSelection(spinnerProvinsi.selectedItemPosition)
                    spinnerKabupatenAyah.setSelection(spinnerKabupaten.selectedItemPosition)
                    spinnerKecamatanAyah.setSelection(spinnerKecamatan.selectedItemPosition)
                    spinnerDesaAyah.setSelection(spinnerDesa.selectedItemPosition)

                    alamatAyah.isEnabled = false
                    spinnerProvinsiAyah.isEnabled = false
                    spinnerKabupatenAyah.isEnabled = false
                    spinnerKecamatanAyah.isEnabled = false
                    spinnerDesaAyah.isEnabled = false
                } else {
                    checkboxAlamat.isChecked = false
                    Toast.makeText(this, "Silakan isi alamat dan provinsi santri terlebih dahulu.", Toast.LENGTH_SHORT).show()
                }
            } else {
                spinnerProvinsiAyah.setSelection(0)
                spinnerKabupatenAyah.setSelection(0)
                spinnerKecamatanAyah.setSelection(0)
                spinnerDesaAyah.setSelection(0)
            }
        }

        checkBoxAlamatIbu.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (alamatSantri.text.isNotEmpty() && spinnerProvinsi.selectedItemPosition != 0) {
                    alamatIbu.text = alamatSantri.text
                    spinnerProvinsiIbu.setSelection(spinnerProvinsi.selectedItemPosition)
                    spinnerKabupatenIbu.setSelection(spinnerKabupaten.selectedItemPosition)
                    spinnerKecamatanIbu.setSelection(spinnerKecamatan.selectedItemPosition)
                    spinnerDesaIbu.setSelection(spinnerDesa.selectedItemPosition)

                    alamatIbu.isEnabled = false
                    spinnerProvinsiIbu.isEnabled = false
                    spinnerKabupatenIbu.isEnabled = false
                    spinnerKecamatanIbu.isEnabled = false
                    spinnerDesaIbu.isEnabled = false
                } else {
                    checkBoxAlamatIbu.isChecked = false
                    Toast.makeText(this, "Silakan isi alamat dan provinsi santri terlebih dahulu.", Toast.LENGTH_SHORT).show()
                }
            } else {
                spinnerProvinsiIbu.setSelection(0)
                spinnerKabupatenIbu.setSelection(0)
                spinnerKecamatanIbu.setSelection(0)
                spinnerDesaIbu.setSelection(0)
            }
        }
    }

    fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newMonth = monthOfYear + 1
                val formattedDate = "$dayOfMonth-${if (newMonth < 10) "0$newMonth" else newMonth}-$year"
                editText.setText(formattedDate)
                editText.error = null
            }, year, month, day
        )

        // Set max date to today
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun showDatePickerDialogSantri(editText: EditText) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Set minimum date to 14 years ago from today
        calendar.set(currentYear - 14, month, day)
        val minDate = calendar.timeInMillis

        // Set maximum date to 12 years ago from today
        calendar.set(currentYear - 12, month, day)
        val maxDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val newMonth = monthOfYear + 1
                val formattedDate = "$dayOfMonth-${if (newMonth < 10) "0$newMonth" else newMonth}-$year"
                editText.setText(formattedDate)
                editText.error = null
            },
            currentYear, month, day
        )

        // Set the minimum and maximum date
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }

    private fun getProvinsi() {
        showLoadingDialog()
        ApiConfig.create(this, apiAlamat).getProvinsi().enqueue(object : Callback<List<Provinsi>> {
            override fun onResponse(call: Call<List<Provinsi>>, response: Response<List<Provinsi>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        provinsiList.clear()
                        provinsiList.addAll(responseBody)
                        runOnUiThread {

                            val fontPath = "poppinsmedium.ttf"
                            val listProvinsi = mutableListOf("PROVINSI")
                            listProvinsi.addAll(provinsiList.map { it.name })
                            provinsiAdapter = CustomSpinnerAdapter(this@PsbActivity, listProvinsi, fontPath)
                            spinnerProvinsi.adapter = provinsiAdapter
                            spinnerProvinsiAyah.adapter = provinsiAdapter
                            spinnerProvinsiIbu.adapter = provinsiAdapter

                            // Mencegah pengguna untuk mengklik AutoCompleteTextView kabupaten sebelum memilih provinsi
                            spinnerKabupaten.isClickable = false

                            spinnerProvinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    if (position > 0) {
                                        val selectedProvinsi = provinsiList[position - 1]
                                        getKabupaten(selectedProvinsi.id)
                                    } else {
                                        val selectedProvinsi = provinsiList[position]
                                        getKabupaten(selectedProvinsi.id)
                                    }

                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                        }
                    }
                } else {
                    Log.e("BDR", "Respon tidak berhasil: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Provinsi>>, t: Throwable) {
                Log.e("BDR", "Gagal: ${t.message}", t)
            }
        })
    }

    private fun getKabupaten(provinsi: String) {
        ApiConfig.create(this, apiAlamat).getKabupaten(provinsi).enqueue(object : Callback<List<Kabupaten>> {
            override fun onResponse(call: Call<List<Kabupaten>>, response: Response<List<Kabupaten>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        kabupatenList.clear()
                        kabupatenList.addAll(responseBody)
                        runOnUiThread {
                            val fontPath = "poppinsmedium.ttf"
                            val listKabupaten = mutableListOf("KABUPATEN")
                            listKabupaten.addAll(kabupatenList.map { it.name })
                            kabupatenAdapter = CustomSpinnerAdapter(this@PsbActivity, listKabupaten, fontPath)
                            spinnerKabupaten.adapter = kabupatenAdapter
                            spinnerKabupatenAyah.adapter = kabupatenAdapter
                            spinnerKabupatenIbu.adapter = kabupatenAdapter
                            kabupatenAdapter.notifyDataSetChanged()
                            // Aktifkan spinner kabupaten setelah memuat data
                            spinnerKabupaten.isClickable = true

                            spinnerKabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    if (position > 0) {
                                        val selectedKabupaten = kabupatenList[position - 1]
                                        getKecamatan(selectedKabupaten.id)
                                    } else {
                                        val selectedKabupaten = kabupatenList[position]
                                        getKecamatan(selectedKabupaten.id)
                                    }
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }
                        }

                    }
                } else {
                    Log.e("BDR", "Respon tidak berhasil: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Kabupaten>>, t: Throwable) {
                Log.e("BDR", "Gagal: ${t.message}", t)
            }
        })
    }

    private fun getKecamatan(kabupatenId:String){
        ApiConfig.create(this,apiAlamat).getKecamatan(kabupatenId).enqueue(object :Callback<List<Kecamatan>>{
            override fun onResponse(
                call: Call<List<Kecamatan>>,
                response: Response<List<Kecamatan>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        kecamatanList.clear()
                        kecamatanList.addAll(responseBody)
                        runOnUiThread {
                            val fontPath = "poppinsmedium.ttf"
                            val listKecamatan = mutableListOf("KECAMATAN")
                            listKecamatan.addAll(kecamatanList.map { it.name })
                            kecamatanAdapter = CustomSpinnerAdapter(this@PsbActivity, listKecamatan, fontPath)
                            spinnerKecamatan.adapter = kecamatanAdapter
                            spinnerKecamatanAyah.adapter = kecamatanAdapter
                            spinnerKecamatanIbu.adapter = kecamatanAdapter

                            // Mencegah pengguna untuk mengklik AutoCompleteTextView kabupaten sebelum memilih provinsi
                            spinnerKecamatan.isClickable = false

                            spinnerKecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    if (position > 0) {
                                        val selectedKecamatan = kecamatanList[position - 1]
                                        getDesa(selectedKecamatan.id)
                                    } else {
                                        val selectedKecamatan = kecamatanList[position]
                                        getDesa(selectedKecamatan.id)
                                    }

                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                        }
                    }
                } else {
                    Log.e("BDR", "Respon tidak berhasil: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Kecamatan>>, t: Throwable) {
                Log.e("BDR", "Gagal: ${t.message}", t)
            }

        })
    }

    private fun getDesa(kecamatanId:String){
        ApiConfig.create(this,apiAlamat).getDesa(kecamatanId).enqueue(object :Callback<List<Desa>>{
            override fun onResponse(
                call: Call<List<Desa>>,
                response: Response<List<Desa>>
            ) {
                if (response.isSuccessful) {
                    hideLoadingDialog()
                    val responseBody = response.body()
                    if (responseBody != null) {
                        desaList.clear()
                        desaList.addAll(responseBody)
                        runOnUiThread {
                            val fontPath = "poppinsmedium.ttf"
                            val listDesa = mutableListOf("DESA")
                            listDesa.addAll(desaList.map { it.name })
                            desaAdapter = CustomSpinnerAdapter(this@PsbActivity, listDesa, fontPath)
                            spinnerDesa.adapter = desaAdapter
                            spinnerDesaAyah.adapter = desaAdapter
                            spinnerDesaIbu.adapter = desaAdapter

                            spinnerDesa.isClickable = true

                        }
                    }
                } else {
                    hideLoadingDialog()
                    showDialogError()
                    Log.e("BDR", "Respon tidak berhasil: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Desa>>, t: Throwable) {
                hideLoadingDialog()
                showDialogError()
                Log.e("BDR", "Gagal: ${t.message}", t)
            }

        })
    }

    private fun initPendidikan(){
        val fontPath = "poppinsmedium.ttf"
        val pendidikan = resources.getStringArray(R.array.pendidikan)
        val listPendidikan = mutableListOf("Pendidikan Terakhir")
        listPendidikan.addAll(pendidikan.toList())

        pendidikanAyah.adapter = CustomSpinnerAdapter(this@PsbActivity, listPendidikan, fontPath)
        pendidikanIbu.adapter = CustomSpinnerAdapter(this@PsbActivity, listPendidikan, fontPath)

    }

    private fun initPekerjaan(){
        val fontPath = "poppinsmedium.ttf"
        val pekerjaan = resources.getStringArray(R.array.pekerjaan)
        val listPekerjaan = mutableListOf("Pekerjaan")
        listPekerjaan.addAll(pekerjaan.toList())

        pekerjaanAyah.adapter = CustomSpinnerAdapter(this@PsbActivity, listPekerjaan, fontPath)
        pekerjaanIbu.adapter = CustomSpinnerAdapter(this@PsbActivity, listPekerjaan, fontPath)
    }

    private fun initTingkatPrestasi(){
        val fontPath = "poppinsmedium.ttf"
        val tingkat = resources.getStringArray(R.array.tingkat_prestasi)
        val listTingkat = mutableListOf("TINGKAT KEGIATAN")
        listTingkat.addAll(tingkat.toList())

        val juara = resources.getStringArray(R.array.juara_prestasi)
        val listJuara = mutableListOf("JUARA PRESTASI")
        listJuara.addAll(juara.toList())

        val jenis = resources.getStringArray(R.array.jenis_prestasi)
        val listJenis = mutableListOf("JENIS PRESTASI")
        listJenis.addAll(jenis.toList())

        jenisPrestasi.adapter = CustomSpinnerAdapter(this@PsbActivity, listJenis, fontPath)
        prestasiTingkat.adapter = CustomSpinnerAdapter(this@PsbActivity, listTingkat, fontPath)
        prestasiJuara.adapter = CustomSpinnerAdapter(this@PsbActivity, listJuara, fontPath)

    }

    private fun initTransaksi(tokenDevices : String, dataPSB : Psb) {
        val dataBiaya = intent.getStringExtra("biayapendaftaran")

        val transactionDetails = MidtransRequest.TransactionDetails(
            order_id = "PSB-${UUID.randomUUID()}",
            gross_amount = dataBiaya!!.toInt()
        )
        val customerDetails = MidtransRequest.CustomerDetails(
            id_pendaftaran = dataPSB.id ?: 1,
            name_santri = dataPSB.nama_santri ?: "Badrun",
            gender = dataPSB.gender.toString(),
            ayah = dataPSB.nama_ayah ?: "Ayah",
            ibu = dataPSB.nama_ibu ?: "Ibu",
            token_hp = tokenDevices
        )
        val requestBody = MidtransRequest(
            transaction_type = "PSB",
            transaction_details = transactionDetails,
            customer_details = customerDetails
        )
        showLoadingDialog()
        ApiConfig.create(this).charge(requestBody).enqueue(object : Callback<ResponseMidtrans> {
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
                    Toast.makeText(this@PsbActivity, "Failed to get snap token", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseMidtrans>, t: Throwable) {
                hideLoadingDialog()
                t.printStackTrace()
                Log.e("BDR", "Request failed: ${t.message}")
                Toast.makeText(this@PsbActivity, "Request failed", Toast.LENGTH_LONG).show()
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


}