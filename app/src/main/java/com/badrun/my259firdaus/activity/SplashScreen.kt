package com.badrun.my259firdaus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.helper.SharedPref

class SplashScreen : AppCompatActivity() {

    private val SPLASH_DURATION: Long = 2000
    private lateinit var s : SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        s = SharedPref(this)

        Handler().postDelayed({
            if (s.getStatusLogin()) {
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            } else {
                // Pengguna belum login, arahkan ke halaman welcome
                val mainIntent = Intent(this, WelcomeActivity::class.java)
                startActivity(mainIntent)
            }
            finish()
        }, SPLASH_DURATION)

    }
}