package com.badrun.my259firdaus.activity.daring

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.ImagePreviewAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityKirimTugasBinding
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.ResponseCheck
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class KirimTugasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKirimTugasBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_SELECT_IMAGE = 2

    private val MAX_IMAGES = 10
    private val PICK_IMAGE_REQUEST = 101
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var adapter: ImagePreviewAdapter
    private lateinit var s : SharedPref

    // Perlu disimpan supaya bisa diakses saat onActivityResult
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKirimTugasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idTugas = intent.getIntExtra("idTugas", 0)
        s = SharedPref(this)
        val user = s.getUser()

        adapter = ImagePreviewAdapter(selectedImageUris) { uriToRemove ->
            selectedImageUris.remove(uriToRemove)
            adapter.notifyDataSetChanged()
        }

        binding.rvImages.layoutManager = GridLayoutManager(this, 3)
        binding.rvImages.adapter = adapter

        binding.btnPilihFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST)
        }

        binding.btnKirimJawaban.setOnClickListener {
            kirimJawaban(user!!.user_id.toString(),idTugas.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {
                val remainingSlots = MAX_IMAGES - selectedImageUris.size
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount
                    for (i in 0 until count.coerceAtMost(remainingSlots)) {
                        selectedImageUris.add(it.clipData!!.getItemAt(i).uri)
                    }
                } else if (it.data != null && selectedImageUris.size < MAX_IMAGES) {
                    selectedImageUris.add(it.data!!)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }



    private fun kirimJawaban(userId: String ,tugasId: String) {

        binding.pb.visibility = View.VISIBLE
        binding.btnKirimJawaban.isEnabled = false

        val teksJawaban = binding.editTextJawaban.text.toString()

        if (teksJawaban.isEmpty() && selectedImageUris.isEmpty()){
            Toast.makeText(this, "Isi Salah satu sebelum mengirimkan jawaban", Toast.LENGTH_SHORT).show()
            binding.pb.visibility = View.GONE
            binding.btnKirimJawaban.isEnabled = true
            return
        }

        val imageParts = selectedImageUris.mapIndexedNotNull { index, uri ->
            val file = getFileFromUri(uri)
            if (file == null || !file.exists()) {
                Toast.makeText(this, "File gambar tidak valid", Toast.LENGTH_SHORT).show()
                return@mapIndexedNotNull null
            }
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            // ✅ GUNAKAN "images[]" sebagai key
            MultipartBody.Part.createFormData("images[$index]", file.name, reqFile)

        }

        val userIdRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
        val tugasIdRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tugasId)
        val teksRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), teksJawaban)

        Log.d("DEBUG_UPLOAD", "Jumlah foto yang dikirim: ${imageParts.size}")
        imageParts.forEachIndexed { index, part ->
            Log.d("DEBUG_UPLOAD", "Foto ke-$index: ${part.body.contentLength()} bytes")
        }

        imageParts.forEachIndexed { index, part ->
            Log.d("DEBUG_UPLOAD", "Part #$index: name=${part.headers}, size=${part.body.contentLength()}")
        }

        ApiConfig.create(this).sendJawaban(
            userIdRequestBody,
            tugasIdRequestBody,
            teksRequestBody,
            imageParts
        ).enqueue(object : Callback<ResponseCheck> {
            override fun onResponse(call: Call<ResponseCheck>, response: Response<ResponseCheck>) {
                binding.pb.visibility = View.GONE
                val res = response.body()
                if (res?.code == 1) {
                    Toast.makeText(this@KirimTugasActivity, "Jawaban berhasil dikirim", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@KirimTugasActivity, "Kamu sudah mengirim jawaban" , Toast.LENGTH_SHORT).show()
                    binding.btnKirimJawaban.isEnabled = true
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseCheck>, t: Throwable) {
                binding.pb.visibility = View.GONE
                binding.btnKirimJawaban.isEnabled = true
                Log.e("KirimTugasActivity", "onFailure: ${t.message}")
            }
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            // Ambil nama file & ekstensi dari uri
            val fileName = getFileName(uri)
            val extension = fileName.substringAfterLast('.', "jpg") // default jpg kalau gagal

            // Buat temp file dengan ekstensi asli
            val tempFile = File.createTempFile("temp_", ".$extension", cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "temp.jpg"
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }


}

