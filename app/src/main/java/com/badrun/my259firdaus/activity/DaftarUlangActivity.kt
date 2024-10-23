package com.badrun.my259firdaus.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.ResponseDaftarUlang
import com.badrun.my259firdaus.model.ResponsePsb
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class DaftarUlangActivity : AppCompatActivity() {

    companion object {
        private const val PICK_PDF_REQUEST_KK = 1001
        private const val PICK_PDF_REQUEST_AKTE = 1002
        private const val PICK_PDF_REQUEST_IJAZAH = 1003
        private const val PICK_PDF_REQUEST_KIP = 1004
    }

    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    private lateinit var fileKK: File
    private lateinit var fileAkte: File
    private lateinit var fileIjazah: File
    private lateinit var fileKip: File

    private lateinit var buttonChooseKK : Button
    private lateinit var buttonChooseAkte : Button
    private lateinit var buttonChooseIjazah : Button
    private lateinit var buttonChooseKip : Button
    private lateinit var buttonKirim : Button

    private lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_ulang)

        s = SharedPref(this)

        fileKK = createEmptyFile("tidakadaFile")
        fileAkte = createEmptyFile("tidakadaFile")
        fileIjazah = createEmptyFile("tidakadaFile")
        fileKip = createEmptyFile("tidakadaFile")

        buttonChooseKK = findViewById(R.id.button_choose_kk)
        buttonChooseAkte = findViewById(R.id.button_choose_akte)
        buttonChooseIjazah = findViewById(R.id.button_choose_ijazah)
        buttonChooseKip = findViewById(R.id.button_choose_kip)
        buttonKirim = findViewById(R.id.button_kirim)

        initButtonFile()

        initBtnBack()

        buttonKirim.setOnClickListener {
            sendData()
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

    private fun sendData(){
        val user = s.getUser()


        val isValid: Boolean = isFileValid(fileKK, "File KK tidak boleh kosong", this) &&
                isFileValid(fileAkte, "File Akte tidak boleh kosong", this) &&
                isFileValid(fileIjazah, "File Ijazah tidak boleh kosong", this) //Gantri SKL

        if (isValid){
            val idUserRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), user!!.user_id.toString())
            val fileKKRequestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), fileKK)
            val partFileKK = MultipartBody.Part.createFormData("kartu_keluarga", fileKK.name, fileKKRequestBody)
            val fileAkteRequestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), fileAkte)
            val partFileAkte = MultipartBody.Part.createFormData("akte_kelahiran", fileAkte.name, fileAkteRequestBody)
            val fileIjazahRequestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), fileIjazah)
            val partFileIjazah = MultipartBody.Part.createFormData("ijazah", fileIjazah.name, fileIjazahRequestBody)
            val fileKipRequestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), fileKip)
            val partFileKip = MultipartBody.Part.createFormData("kip", fileKip.name, fileKipRequestBody)
            showLoadingDialog()
            ApiConfig.create(this).setDaftarUlang(idUserRequestBody,partFileKK, partFileAkte,partFileIjazah,partFileKip).enqueue(object: Callback<ResponseDaftarUlang>{
                override fun onResponse(call: Call<ResponseDaftarUlang>, response: Response<ResponseDaftarUlang>) {
                    if (response.isSuccessful){
                        hideLoadingDialog()
                        val res = response.body()!!
                        if (res.code == 1){
                            showDialogWithMessage("Sukses Melakukan Daftar Ulang","Tunggu Notifikasi Selanjutnya untuk mendapatkan akun santri")
                            finish()
                        } else {
                            showDialogError()
                        }
                    } else {
                        hideLoadingDialog()
                        showDialogError()
                    }
                }
                override fun onFailure(call: Call<ResponseDaftarUlang>, t: Throwable) {
                    hideLoadingDialog()
                    showDialogError()
                }

            })
        }

    }

    private fun showDialogWithMessage(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
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
                PICK_PDF_REQUEST_KK -> {
                    buttonChooseKK.text = namaFile
                    fileKK = saveFileToInternalStorage(uri, "kk")
                }
                PICK_PDF_REQUEST_AKTE -> {
                    buttonChooseAkte.text = namaFile
                    fileAkte = saveFileToInternalStorage(uri, "akte")
                }
                PICK_PDF_REQUEST_IJAZAH -> {
                    buttonChooseIjazah.text = namaFile
                    fileIjazah = saveFileToInternalStorage(uri, "ijazah")
                }
                PICK_PDF_REQUEST_KIP -> {
                    buttonChooseKip.text = namaFile
                    fileKip = saveFileToInternalStorage(uri, "kip")
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
        val file = File(getExternalFilesDir(null), "$fileNamePrefix-${System.currentTimeMillis()}.$fileExtension")
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
        buttonChooseKK.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf" // Menentukan tipe file yang dapat dipilih
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Pilih kk Santri"),PICK_PDF_REQUEST_KK)
        }
        buttonChooseAkte.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf" // Menentukan tipe file yang dapat dipilih
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Pilih akte Santri"), PICK_PDF_REQUEST_AKTE)
        }
        buttonChooseIjazah.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf" // Menentukan tipe file yang dapat dipilih
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Pilih ijazah Santri"), PICK_PDF_REQUEST_IJAZAH)
        }
        buttonChooseKip.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf" // Menentukan tipe file yang dapat dipilih
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Pilih kip Santri"), PICK_PDF_REQUEST_KIP)
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
}