package com.badrun.my259firdaus.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.badrun.my259firdaus.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class IklanActivity : AppCompatActivity() {


    private lateinit var adView: AdView
    private var interstitialAd: InterstitialAd? = null
    private lateinit var bannerAd : AdView
    private val adUnitId = "ca-app-pub-3416538992401399/8883418687"
    private lateinit var button : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iklan)

        // Inisialisasi AdMob
        initBtnBack()
        MobileAds.initialize(this) {}

        // inisialisasi banner ad
        bannerAd = findViewById(R.id.adBannerView)
        val adRequestSatu = AdRequest.Builder().build()
        bannerAd.loadAd(adRequestSatu)



        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        button = findViewById(R.id.button_iklan)

        loadInterstitialAd()

        button.setOnClickListener {
            showInterstitialAd()
        }



    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }
        })


    }

    private fun showInterstitialAd() {
        interstitialAd?.let { ad ->
            ad.show(this)
            interstitialAd = null // Setelah ditampilkan, set null agar bisa reload nanti
            loadInterstitialAd() // Muat iklan baru untuk nanti
        } ?: run {
            // Iklan belum siap, coba load ulang
            loadInterstitialAd()
        }
    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarFormulir)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }

}