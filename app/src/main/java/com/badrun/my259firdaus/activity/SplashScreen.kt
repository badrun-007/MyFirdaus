package com.badrun.my259firdaus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.badrun.my259firdaus.R

class SplashScreen : AppCompatActivity() {

    private lateinit var button :Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        button = findViewById(R.id.btn_splscren)

        button.setOnClickListener {
            val i = Intent(this,MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

    }
}