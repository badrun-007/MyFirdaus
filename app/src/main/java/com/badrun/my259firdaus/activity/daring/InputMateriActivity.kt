package com.badrun.my259firdaus.activity.daring

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.badrun.my259firdaus.adapter.ImagePreviewAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.databinding.ActivityInputMateriBinding
import com.badrun.my259firdaus.model.ResponseCheck
import com.badrun.my259firdaus.model.response.ResponseMatpel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Calendar

class InputMateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputMateriBinding
    private var selectedFileUri: Uri? = null

    private val FILE_PICKER_REQUEST_CODE = 1001

    private val MAX_IMAGES = 10
    private val PICK_IMAGE_REQUEST = 101
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var adapter: ImagePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataMatpel = intent.getParcelableArrayListExtra<ResponseMatpel.MatpelGuru>("data") ?: listOf()

        // Ambil list unik matpel
        val matpelList = dataMatpel.map { it.matpel.name }.distinct()

        // Set spinner matpel
        binding.spinnerMatpel.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            matpelList
        )

        binding.spinnerMatpel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMatpel = matpelList[position]

                // Ambil nama-nama kelas yang sesuai matpel
                val kelasNamaList = dataMatpel
                    .filter { it.matpel.name.equals(selectedMatpel) }
                    .map { it.kelas.name }
                    .distinct()

                // Ambil level-nya dari nama (misal "9A" → 9)
                val levelSet = kelasNamaList
                    .mapNotNull { it.filter { c -> c.isDigit() }.toIntOrNull() }
                    .distinct()
                    .sorted()

                // Buat opsi gabungan "Kelas 7", "Kelas 8", dll
                val kelasGabungan = levelSet.map { "Kelas $it" }

                // Gabungkan opsi gabungan + list kelas asli (disortir)
                val sortedKelas = kelasNamaList.sortedWith(compareBy(
                    { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 },
                    { it.filter { c -> c.isLetter() } }
                ))

                val finalList = kelasGabungan + sortedKelas

                // Tampilkan di spinner
                val kelasAdapter = ArrayAdapter(
                    this@InputMateriActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    finalList
                )
                binding.spinnerKelas.adapter = kelasAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        binding.btnPilihFile.setOnClickListener {
            openFilePicker()
        }

        binding.etmulai.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Setelah user pilih tanggal, lanjut pilih waktu
                val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                    val formattedDateTime = String.format(
                        "%04d-%02d-%02d %02d:%02d",
                        selectedYear, selectedMonth + 1, selectedDay,
                        hourOfDay, minute
                    )
                    binding.etmulai.setText(formattedDateTime)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

                timePickerDialog.show()

            }, year, month, day)

            datePickerDialog.show()
        }

        binding.etDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Setelah user pilih tanggal, lanjut pilih waktu
                val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                    val formattedDateTime = String.format(
                        "%04d-%02d-%02d %02d:%02d",
                        selectedYear, selectedMonth + 1, selectedDay,
                        hourOfDay, minute
                    )
                    binding.etDeadline.setText(formattedDateTime)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

                timePickerDialog.show()

            }, year, month, day)

            datePickerDialog.show()
        }

        adapter = ImagePreviewAdapter(selectedImageUris) { uriToRemove ->
            selectedImageUris.remove(uriToRemove)
            adapter.notifyDataSetChanged()
        }
        binding.rvPreviewGambar.layoutManager = GridLayoutManager(this, 3)
        binding.rvPreviewGambar.adapter = adapter

        binding.btnPilihGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST)
        }

        binding.btnSimpan.setOnClickListener {
            sendDataServer(dataMatpel)
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUris.clear()

            data?.let {
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount.coerceAtMost(MAX_IMAGES)
                    for (i in 0 until count) {
                        selectedImageUris.add(it.clipData!!.getItemAt(i).uri)
                    }
                } else if (it.data != null) {
                    selectedImageUris.add(it.data!!)
                }
            }

            adapter.notifyDataSetChanged()
        }
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            selectedFileUri?.let {
                val fileName = getFileName(it)
                binding.tvNamaFile.text = fileName  // Ganti TextView dengan nama file
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        val path = uri.path ?: return "file.pdf"
        val cut = path.lastIndexOf('/')
        return if (cut != -1) path.substring(cut + 1) else path
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("temp_", null, cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendDataServer(dataMatpel: List<ResponseMatpel.MatpelGuru>) {
        binding.btnSimpan.isClickable = false
        binding.pb.visibility = View.VISIBLE
        val judul = binding.etJudul.text.toString()
        val deskripsi = binding.etDeskripsi.text.toString()
        val linkVideo = binding.linkvideo.text.toString()

        val matpelId = binding.spinnerMatpel.selectedItem.toString()
        val kelasId = binding.spinnerKelas.selectedItem.toString()

        val guruId = dataMatpel[0].guru_id.toString()
        val deadlineText = binding.etDeadline.text.toString()
        val mulaiText = binding.etmulai.text.toString()

        if (judul.isBlank() || deskripsi.isBlank() ) {
            Toast.makeText(this, "Judul dan Deskripsi wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val judulPart = judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val deskripsiPart = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
        val guruIdPart = guruId.toRequestBody("text/plain".toMediaTypeOrNull())
        val matpelIdPart = matpelId.toRequestBody("text/plain".toMediaTypeOrNull())
        val kelasIdPart = kelasId.toRequestBody("text/plain".toMediaTypeOrNull())
        val linkPart = linkVideo.toRequestBody("text/plain".toMediaTypeOrNull())
        val mulaiPart = if (mulaiText.isNotBlank()) {
            mulaiText.toRequestBody("text/plain".toMediaTypeOrNull())
        } else null
        val deadlinePart = if (deadlineText.isNotBlank()) {
            deadlineText.toRequestBody("text/plain".toMediaTypeOrNull())
        } else null

        // Pastikan ada file PDF yang dipilih
        val filePart = selectedFileUri?.let { uri ->
            val mimeType = contentResolver.getType(uri)
            if (mimeType != "application/pdf") {
                Toast.makeText(this, "File yang dipilih bukan PDF!", Toast.LENGTH_SHORT).show()
                return
            }
            val file = getFileFromUri(uri)
            if (file == null || !file.exists()) {
                Toast.makeText(this, "File PDF tidak valid", Toast.LENGTH_SHORT).show()
                return@let null
            }
            val reqFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", file.name, reqFile)
        }

        // Gambar-gambar dan data lainnya
        val imageParts = selectedImageUris.mapNotNull { uri ->
            val file = getFileFromUri(uri)
            if (file == null || !file.exists()) {
                Toast.makeText(this, "File gambar tidak valid", Toast.LENGTH_SHORT).show()
                return@mapNotNull null
            }
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images[]", file.name, reqFile)
        }

        Log.d("BDR", "Judul: $judul, Deskripsi: $deskripsi, Deadline: $deadlineText")
        Log.d("BDR", "Jumlah gambar: ${imageParts.size}")
        Log.d("BDR", "File PDF: ${filePart != null}")

        // Kirim
        ApiConfig.create(this).uploadMateri(
            guruIdPart,
            matpelIdPart,
            kelasIdPart,
            judulPart,
            deskripsiPart,
            mulaiPart,
            deadlinePart,
            linkPart,
            filePart,
            imageParts
        ).enqueue(object : Callback<ResponseCheck> {
            override fun onResponse(call: Call<ResponseCheck>, response: Response<ResponseCheck>) {
                val res = response.body()
                binding.btnSimpan.isClickable = true
                binding.pb.visibility = View.GONE
                if (res!!.code == 1){
                    Toast.makeText(this@InputMateriActivity, "Upload tugas berhasil", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@InputMateriActivity, "Gagal Terhubung dengan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseCheck>, t: Throwable) {
                Log.e("ERROR", t.message ?: "Unknown error")
                binding.btnSimpan.isClickable = true
                binding.pb.visibility = View.VISIBLE
            }
        })
    }
}