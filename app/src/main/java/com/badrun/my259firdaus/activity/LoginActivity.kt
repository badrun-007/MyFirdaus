package com.badrun.my259firdaus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.Init
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.ResponsModel
import com.badrun.my259firdaus.model.ResponseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var button : Button
    private lateinit var buatAkun : Button
    private lateinit var username : EditText
    private lateinit var load : ProgressBar
    private lateinit var pass : EditText
    private val context = this

    private val RC_SIGN_IN = 9001
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var s : SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        s = SharedPref(this)

        init()
        initListener()

    }

    private fun init(){
        button = findViewById(R.id.btn_masuk_login)
        username = findViewById(R.id.et_email_log)
        pass = findViewById(R.id.et_pass_log)
        load = findViewById(R.id.pb)
        buatAkun = findViewById(R.id.btn_regis)
    }


    private fun initListener(){
        button.setOnClickListener {
            initButton()
        }

        buatAkun.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
    }

    private fun initButton(){
        if(username.text.isEmpty()){
            username.error = "Field email tidak boleh kosong!"
            username.requestFocus()
            return
        } else if(pass.text.isEmpty()){
            pass.error = "Field password tidak boleh kosong"
            pass.requestFocus()
            return
        } else {
            load.visibility = View.VISIBLE
            ApiConfig.create(context).login(username.text.toString(),pass.text.toString()).enqueue(object : Callback<ResponseUser>{
                override fun onResponse(
                    call: Call<ResponseUser>,
                    response: Response<ResponseUser>
                ) {
                    load.visibility = View.GONE
                    if (response.isSuccessful){
                        val res = response.body()!!
                        if( res.code == 1){

                            s.setStatusLogin(true)
                            s.setUser(res.data)

                            Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                            val i = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(i)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, res.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    load.visibility = View.GONE
//                    val dialog = this@LoginActivity as Init
//                    dialog.showCustomAlertDialog(this@LoginActivity,"Tidak dapat terhubung ke server. Silakan coba beberapa saat lagi.")
                    Log.e("bdr", "onFailure: ${t.message}", )
                }

            })
        }

    }

}