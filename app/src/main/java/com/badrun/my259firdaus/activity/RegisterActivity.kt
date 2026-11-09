package com.badrun.my259firdaus.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.model.ResponsModel
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
    private lateinit var layoutKode : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()

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
        layoutKode = findViewById(R.id.field_verif_code)
    }

    private fun initButton() {
        val user = listOf("Tamu", "Alumni")
        var role = ""

        val adapterItems = ArrayAdapter(this, R.layout.list_user, user)
        dropdown.adapter = adapterItems

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                role = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

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
        } else if (!isValidEmail(email.text.toString())) {
            email.error = "Isi Email dengan yang benar!"
            email.requestFocus()
            return
        }
        else if (pass.text.isEmpty()) {
            pass.error = "Field password tidak boleh kosong"
            pass.requestFocus()
            return
        } else if (pass.text.toString().length < 8) {
            passConfirm.error = "Password harus lebih dari 8 karakter"
            passConfirm.requestFocus()
            return
        } else if (pass.text.toString() != passConfirm.text.toString()) {
            passConfirm.error = "Password tidak sama!"
            passConfirm.requestFocus()
            return
        } else {
            load.visibility = View.VISIBLE
            ApiConfig.create(context).register(
                nama.text.toString(),
                email.text.toString(),
                pass.text.toString(),
                5,
                role
            ).enqueue(object : Callback<ResponsModel> {
                override fun onResponse(call: Call<ResponsModel>, response: Response<ResponsModel>) {
                    load.visibility = View.GONE
                    if (response.isSuccessful) {
                        val res = response.body()!!
                        when (res.code) {
                            0 -> {
                                Toast.makeText(this@RegisterActivity, "${res.message}", Toast.LENGTH_LONG).show()
                            }
                            1 -> {
                                val i = Intent(this@RegisterActivity, LoginActivity::class.java)
                                showVerificationDialog(email.text.toString())
                                startActivity(i)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@RegisterActivity, "${res.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    load.visibility = View.GONE
                    Log.e("RegisterActivity", "${t.message}")
                }
            })
        }
    }


    private fun showVerificationDialog(userEmail: String) {
        val view = layoutInflater.inflate(R.layout.dialog_verifikasi, null)

        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnEmail = view.findViewById<Button>(R.id.btnEmail)
        val btnNanti = view.findViewById<Button>(R.id.btnNanti)

        val message = """
    Akun Anda berhasil dibuat 🎉
    
    Kami telah mengirimkan link verifikasi ke:
    
    $userEmail
    
    Silakan cek email Anda (Inbox / Spam / Promosi).
    
    Link verifikasi berlaku selama 10 menit.
""".trimIndent()

        val spannable = SpannableString(message)

// Cari posisi email
        val start = message.indexOf(userEmail)
        val end = start + userEmail.length

// Set bold
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvMessage.text = spannable

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        btnEmail.setOnClickListener {
            openEmailApp()
            dialog.dismiss()
        }

        btnNanti.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        dialog.show()
    }

    private fun openEmailApp() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Aplikasi email tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

}