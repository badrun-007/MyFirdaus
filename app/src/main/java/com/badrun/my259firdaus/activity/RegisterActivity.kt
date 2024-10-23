package com.badrun.my259firdaus.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.model.ResponsModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    private lateinit var dropdown : Spinner
    private lateinit var nama : EditText
    private lateinit var email : EditText
    private lateinit var pass : EditText
    private lateinit var passConfirm : EditText
    private lateinit var load : ProgressBar
    private val context = this
    private lateinit var button : Button
    private lateinit var sendVerifikasi : TextView
    private lateinit var layoutKode : LinearLayout
    private lateinit var authFire: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var tokenDevices = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("BDR", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            tokenDevices = task.result
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    deepLink?.getQueryParameter("oobCode")?.let { oobCode ->
                        Log.d("RegisterActivity", "Deep link found with oobCode: $oobCode")
                        verifyEmail(oobCode)
                    }
                }
            }
            .addOnFailureListener(this) { e ->
                Log.w("RegisterActivity", "getDynamicLink:onFailure", e)
            }

        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Nothing needed here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailText = s.toString()
                if (isValidEmail(emailText)) {
                    sendVerifikasi.visibility = View.VISIBLE
                } else {
                    sendVerifikasi.visibility = View.GONE
                }
            }
        })

        sendVerifikasi.setOnClickListener {
            sendEmailVerifikasi(email.text.toString())
        }

        initButton()
    }

    private fun isValidEmail(emailText: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
    }

    private fun init() {
        dropdown = findViewById(R.id.dropdown_field_reg)
        nama = findViewById(R.id.et_nama_reg)
        email = findViewById(R.id.et_email_reg)
        pass = findViewById(R.id.et_pass_reg)
        passConfirm = findViewById(R.id.et_pass_confirm_reg)
        button = findViewById(R.id.btn_daftar_reg)
        load = findViewById(R.id.pb_reg)
        sendVerifikasi = findViewById(R.id.send_verifikasi)
        layoutKode = findViewById(R.id.field_verif_code)
        authFire = FirebaseAuth.getInstance()
    }

    private fun initButton() {
        //init list user
        val user = listOf("Alumni", "Tamu")
        var role = ""

        val adapterItems = ArrayAdapter(this, R.layout.list_user, user)
        dropdown.adapter = adapterItems

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = parent.getItemAtPosition(position).toString()
                role = item
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing or handle the case when no item is selected
            }
        }

        //akhir init list user

        button.setOnClickListener {
            register(role)
        }
    }

    private fun register(role: String) {
        if (nama.text.isEmpty()) {
            nama.error = "Field nama tidak boleh kosong!"
            nama.requestFocus()
            return
        } else if (email.text.isEmpty()) {
            email.error = "Field email tidak boleh kosong!"
            email.requestFocus()
            return
        } else if (pass.text.isEmpty()) {
            pass.error = "Field password tidak boleh kosong"
            pass.requestFocus()
            return
        } else if (pass.text.toString().length < 8){
            passConfirm.error = "Password harus lebih dari 8 Karakter"
            passConfirm.requestFocus()
            return
        } else if (pass.text.toString() != passConfirm.text.toString()) {
            passConfirm.error = "Password tidak sama!"
            passConfirm.requestFocus()
            return
        } else {
            val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val verificationEmail = sharedPreferences.getString("verificationEmail", "")
            Log.d("RegisterActivity", "SharedPreferences verificationEmail: $verificationEmail") // Debugging log
            if (verificationEmail!!.isNotEmpty()) {
                Log.e("BDR", "register: $verificationEmail")
                load.visibility = View.VISIBLE
                ApiConfig.create(context).register(nama.text.toString(), email.text.toString(), pass.text.toString(), 5, role).enqueue(object : Callback<ResponsModel> {
                    override fun onResponse(call: Call<ResponsModel>, response: Response<ResponsModel>) {
                        load.visibility = View.GONE
                        if (response.isSuccessful) {
                            val res = response.body()!!
                            if (res.code == 0) {
                                Toast.makeText(this@RegisterActivity, "${res.message}", Toast.LENGTH_SHORT).show()
                            } else {
                                val res = response.body()!!
                                if (res.code == 1) {
                                    val i = Intent(this@RegisterActivity, LoginActivity::class.java)
                                    Toast.makeText(this@RegisterActivity, "Silahkan Login Kembali", Toast.LENGTH_SHORT).show()
                                    startActivity(i)
                                    finish()
                                } else{
                                    Toast.makeText(this@RegisterActivity, "${res.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                        load.visibility = View.GONE
                        Log.e("bdr", "${t.message}")
                    }
                })
            } else {
                Toast.makeText(this, "Please verify your email address.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailVerifikasi(email: String) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://fir-crud-622cd.firebaseapp.com/finishSignUp?cartId=1234")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.badrun.my259firdaus",
                false, /* installIfNotAvailable */
                "7"    /* minimumVersion */)
            .build()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent to $email", Toast.LENGTH_SHORT).show()
                    saveEmailForVerification(email) // Save email when sending the verification link
                } else {
                    Toast.makeText(this, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verifyEmail(oobCode: String) {
        FirebaseAuth.getInstance().applyActionCode(oobCode)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email verified successfully.", Toast.LENGTH_SHORT).show()
                    saveEmailVerificationStatus(true) // Save verification status
                } else {
                    Toast.makeText(this, "Failed to verify email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveEmailForVerification(email: String) {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("verificationEmail", email)
            apply()
        }
    }

    private fun saveEmailVerificationStatus(isVerified: Boolean) {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isEmailVerified", isVerified)
            apply()
        }
    }

}