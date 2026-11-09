package com.badrun.my259firdaus.activity



import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import androidx.viewpager.widget.ViewPager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.QuestionPagerAdapter
import com.badrun.my259firdaus.adapter.SoalNoAdapter
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.OnFontSizeChangeListener
import com.badrun.my259firdaus.model.AnswerRequest
import com.badrun.my259firdaus.model.QuestionResponse
import com.badrun.my259firdaus.model.Soal
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SoalActivity : AppCompatActivity(), OnFontSizeChangeListener {

    private val takenQuestionIds = mutableSetOf<Int>()
    lateinit var soalNoAdapter: SoalNoAdapter
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button
    private val studentAnswers = mutableMapOf<Int, String>()
    private val serverAnswers: MutableMap<Int, String> = mutableMapOf() // For server
    private var currentSoalIndex = 0
    private var totalSoal = 0
    private var currentFontSize: Float = 18f
    private lateinit var questionAdapter: QuestionPagerAdapter
    private var transaksiId = 0
    private lateinit var timerTextView: TextView
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 900000L
    private lateinit var pb : ProgressBar
    private lateinit var buttonShow : Button
    private lateinit var rvListSoal: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soal)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Mencegah screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        transaksiId = intent.getIntExtra("transaksi_id", 0)
        val kelasId = intent.getStringExtra("class_id")
        timeLeftInMillis = intent.getLongExtra("durasi",0L)
        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
        btnSubmit = findViewById(R.id.btn_submit)
        pb = findViewById(R.id.pbs)

        Log.e("BDR", "onCreate soal : $timeLeftInMillis", )

        timerTextView = findViewById(R.id.timerTextView)


        getSoal(kelasId!!.toInt(),transaksiId)

        // Set listener untuk SeekBar
        val seekBar = findViewById<SeekBar>(R.id.font_size_seekbar)
        seekBar.progress = currentFontSize.toInt() // Set default progress
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newSize = progress.toFloat() // Atau logika untuk mengubah ukuran sesuai kebutuhan
                questionAdapter.updateFontSize(newSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Tidak perlu melakukan apa-apa di sini
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Tidak perlu melakukan apa-apa di sini
            }
        })

        rvListSoal = findViewById(R.id.recyclerViewSoal)
        buttonShow = findViewById(R.id.btn_hide_show)

        buttonShow.setOnClickListener {
            if (rvListSoal.visibility == View.GONE){
                rvListSoal.visibility = View.VISIBLE
            } else {
                rvListSoal.visibility = View.GONE
            }
        }

    }

    private fun startCountdownTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished

                // Update UI untuk menampilkan sisa waktu
                val hoursLeft = (millisUntilFinished / (1000 * 60 * 60)).toInt() // Menghitung jam
                val minutesLeft = (millisUntilFinished % (1000 * 60 * 60) / (1000 * 60)).toInt() // Menghitung menit
                val secondsLeft = (millisUntilFinished % (1000 * 60) / 1000).toInt() // Menghitung detik

                btnSubmit.isEnabled = millisUntilFinished <= 600_000

                // Format jam:menit:detik
                timerTextView.text = String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft) // Format: hh:mm:ss
            }

            override fun onFinish() {
                // Waktu habis, panggil submitAnswers
                pb.visibility = View.VISIBLE
                btnSubmit.isEnabled = false
                disableInteractions()
                submitAnswers(transaksiId)
            }
        }.start()
    }

    private fun disableInteractions() {
        // Nonaktifkan geser dan sentuhan pada ViewPager
        findViewById<ViewPager>(R.id.viewPager).setOnTouchListener { _, _ -> true }

        // Nonaktifkan tombol
        btnPrevious.isEnabled = false
        btnNext.isEnabled = false
        btnSubmit.isEnabled = false
    }

    private fun enableInteractions() {
        // Aktifkan kembali geser dan sentuhan pada ViewPager
        findViewById<ViewPager>(R.id.viewPager).setOnTouchListener(null)

        // Aktifkan kembali tombol sesuai dengan kondisi saat ini
        btnPrevious.isEnabled = true
        btnNext.isEnabled = true
        btnSubmit.isEnabled = true
    }

    private fun getSoal(kelasId:Int, transaksiID:Int){

        pb.visibility = View.VISIBLE

        ApiConfig.create(this)
            .getExam(kelasId,transaksiID)
            .enqueue(object : Callback<QuestionResponse>{
                override fun onResponse(
                    call: Call<QuestionResponse>,
                    response: Response<QuestionResponse>
                ) {
                    pb.visibility = View.GONE
                    if (response.isSuccessful){
                        val res = response.body()!!
                        if (res.code == 1){

                            val soal = res.questions

                            soal.forEach { question ->
                                takenQuestionIds.add(question.id)
                            }

                            saveQuestionsLocally(soal)
                            totalSoal = soal.size

                            setupViewPager(soal)
                        }
                    }
                }

                override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                    pb.visibility = View.GONE
                    Toast.makeText(this@SoalActivity, "Tidak dapat terhubung dengan server", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun saveQuestionsLocally(questions: List<Soal>) {
        val sharedPreferences = getSharedPreferences("exam_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert questions to JSON
        val gson = Gson()
        val json = gson.toJson(questions)

        editor.putString("questions", json)
        editor.apply()
    }

    private fun updateButtonVisibility() {
        btnPrevious.visibility = if (currentSoalIndex > 0) View.VISIBLE else View.GONE
        btnNext.visibility = if (currentSoalIndex < totalSoal - 1) View.VISIBLE else View.GONE
        btnSubmit.visibility = if (currentSoalIndex == totalSoal - 1) View.VISIBLE else View.GONE
    }

    private fun setupViewPager(questions: List<Soal>) {
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val rv: RecyclerView = findViewById(R.id.recyclerViewSoal)

        questionAdapter = QuestionPagerAdapter(this, questions, studentAnswers) { position, selectedLabel,  selectedtext->
            studentAnswers[questions[position].id] = selectedtext
            serverAnswers[questions[position].id] = selectedLabel // Save ID for server
            soalNoAdapter.markSoalAnswered(position + 1)
        }

        viewPager.adapter = questionAdapter

        // Pasangkan ViewPager ke SoalNoAdapter
        soalNoAdapter = SoalNoAdapter(questions, viewPager) { _ ->
            // Tidak perlu memanggil lagi di sini karena sudah dihandle di dalam SoalNoAdapter
        }

        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = soalNoAdapter

        // Tambahkan listener untuk mendeteksi perubahan posisi
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Tidak perlu melakukan apa-apa di sini
            }

            override fun onPageSelected(position: Int) {
                currentSoalIndex = position
                updateButtonVisibility() // Update visibility tombol saat halaman berubah
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Tidak perlu melakukan apa-apa di sini
            }
        })

        // Perbarui tombol saat aplikasi dibuka
        updateButtonVisibility()

        btnPrevious.setOnClickListener {
            if (currentSoalIndex > 0) {
                currentSoalIndex--
                viewPager.currentItem = currentSoalIndex
                updateButtonVisibility()
            }
        }

        btnNext.setOnClickListener {
            if (currentSoalIndex < totalSoal - 1) {
                currentSoalIndex++
                viewPager.currentItem = currentSoalIndex
                updateButtonVisibility()
            } else {
                // Jika sudah di soal terakhir, ganti tombol Next dengan Kirim
                btnNext.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
            }
        }

        btnSubmit.setOnClickListener {
            if (timeLeftInMillis > 600_000) {
                // Jika waktu masih lebih dari 10 menit
                Toast.makeText(this, "Jawaban hanya bisa dikirim saat sisa waktu kurang dari 10 menit!", Toast.LENGTH_LONG).show()
            } else if (studentAnswers.size < totalSoal) {
                Toast.makeText(this, "Harap jawab semua soal sebelum mengirimkan jawaban!", Toast.LENGTH_LONG).show()
            } else {
                pb.visibility = View.VISIBLE
                btnSubmit.isEnabled = false
                submitAnswers(transaksiId)
            }
        }

        startCountdownTimer()

    }

    private fun resetAnswers() {
        studentAnswers.clear() // Clear the student answers map
        serverAnswers.clear()  // Clear the server answers map

        // Optionally, you can also update the UI if needed
        // For example, if you want to reset the highlighted answered questions in the UI
        soalNoAdapter.resetAnsweredStatus()
    }

    private fun submitAnswers(idTrans:Int) {
        val transaksiId = idTrans
        val answers = serverAnswers.map {
            AnswerRequest.UserAnswer(it.key, it.value)
        }

        val answerRequest = AnswerRequest(transaksiId, answers, 0f)

        ApiConfig.create(this).answerStore(answerRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                pb.visibility = View.GONE
                enableInteractions()
                btnSubmit.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@SoalActivity, "Jawaban berhasil disimpan.", Toast.LENGTH_SHORT).show()
                    resetAnswers()
                    stopLockTask()
                    finish()
                } else {
                    Toast.makeText(this@SoalActivity, "Gagal menyimpan jawaban: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                pb.visibility = View.GONE
                btnSubmit.isEnabled = true
                Toast.makeText(this@SoalActivity, "Tidak dapat terhubung dengan server: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Tidak Boleh Keluar!!!", Toast.LENGTH_LONG).show()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d("Exam", "User is leaving the app (Home / Recent pressed)")
        finishAffinity() // Tutup semua activity
    }

    override fun onResume() {
        super.onResume()
        val isAppSatu = isAppInLockTaskMode()
        if(isAppSatu){
            Log.e("BDR", "onCreate: Sudah pinned", )
        } else {
            finish()
        }
    }

    private fun isAppInLockTaskMode(): Boolean {
        val activityManager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For SDK version 23 and above.
            return (activityManager.lockTaskModeState
                    != ActivityManager.LOCK_TASK_MODE_NONE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activityManager.isInLockTaskMode
        }

        return false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            // Jika aplikasi kehilangan fokus (user menekan Home)
            finish()
        }
    }

    override fun onFontSizeChanged(fontSize: Float) {
        questionAdapter.updateFontSize(fontSize)
    }


}