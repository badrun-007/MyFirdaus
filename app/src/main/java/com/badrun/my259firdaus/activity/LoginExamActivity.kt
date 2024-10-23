package com.badrun.my259firdaus.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.model.ResponseLoginExam
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginExamActivity : AppCompatActivity() {

    private lateinit var textJudul : TextView
    private lateinit var buttonMasuk : Button
    private lateinit var username: EditText
    private lateinit var pass : EditText
    private lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_exam)

        init()

        startLockTask()

        buttonMasuk.setOnClickListener {
            val isApp = isAppInLockTaskMode()
            if (isApp){
                pb.visibility = View.VISIBLE
                buttonMasuk.isEnabled =false
                login()
            } else {
                Toast.makeText(this, "Tidak dapat login, aktifkan dulu mode 'App Pinned'", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun init (){
        textJudul = findViewById(R.id.tv_welcome)
        buttonMasuk = findViewById(R.id.btn_masuk_soal)
        username = findViewById(R.id.et_email_log)
        pass = findViewById(R.id.et_pass_log)
        pb = findViewById(R.id.pb)
    }

    private fun showDialogMassage(message: String, judul:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(judul)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun login(){

        if (username.text.isEmpty()){
            username.error = "Username tidak boleh kosong"
            username.requestFocus()
            return
        } else if (pass.text.isEmpty()){
            pass.error = "Password tidak boleh kosong"
            pass.requestFocus()
            return
        } else{
            val email = username.text.toString()
            val pasw = pass.text.toString()
            ApiConfig.create(this).loginExam(email,pasw).enqueue(
                object : Callback<ResponseLoginExam> {
                    override fun onResponse(
                        call: Call<ResponseLoginExam>,
                        response: Response<ResponseLoginExam>
                    ) {
                        pb.visibility = View.GONE
                        buttonMasuk.isEnabled =true
                        if (response.isSuccessful){
                            val res = response.body()!!
                            if (res.code == 1){
                                val data = res.data
                                val i = Intent(this@LoginExamActivity, TokenExamActivity::class.java)
                                i.putExtra("data_exam", data)
                                startActivity(i)
                                finish()
                            } else {
                                Toast.makeText(this@LoginExamActivity, res.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseLoginExam>, t: Throwable) {
                        pb.visibility = View.GONE
                        buttonMasuk.isEnabled =true
                        Toast.makeText(this@LoginExamActivity, "Tidak dapat terhubung dengan server", Toast.LENGTH_SHORT).show()
                    }
                })
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

    override fun onBackPressed() {
        val isAppInLockTask = isAppInLockTaskMode()

        if (isAppInLockTask) {
            Toast.makeText(this, "Tidak Boleh Keluar!!!", Toast.LENGTH_SHORT).show()
        } else {
            // Jika tidak dalam Lock Task Mode, lanjutkan ke default behavior
            super.onBackPressed()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            // Jika aplikasi kehilangan fokus (user menekan Home)
            finish()
        }
    }

}