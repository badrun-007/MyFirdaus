package com.badrun.my259firdaus.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import org.w3c.dom.Text

class ProfilPesantrenActivity : AppCompatActivity() {

    private lateinit var profil : TextView
    private lateinit var profildua : TextView
    private lateinit var visi : TextView
    private lateinit var misi : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_pesantren)
        initBtnBack()

        profil = findViewById(R.id.profil_pesantren)
        profildua = findViewById(R.id.profil_pesantrendua)
        visi = findViewById(R.id.visi_pesantren)
        misi = findViewById(R.id.misi_pesantren)

        profil.text = getString(R.string.profilpesantren)
        profildua.text = getString(R.string.profilpesantrendua)
        visi.text = getString(R.string.visiPesantren)
        misi.text = getString(R.string.misiPesantren)

    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarProfilPesantren)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }
}