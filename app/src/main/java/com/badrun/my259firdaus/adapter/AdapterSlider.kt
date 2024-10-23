package com.badrun.my259firdaus.adapter

import android.app.Activity
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.api.ApiConfig
import com.squareup.picasso.Picasso
import java.util.Timer
import java.util.TimerTask
import kotlin.math.log

class AdapterSlider(var data: ArrayList<String>, var context: Activity?) : PagerAdapter() {
    private lateinit var layoutInflater: LayoutInflater

    private var currentPosition = 0
    private var timer: Timer? = null
    private val delay = 10000L // Delay dalam milidetik (10 detik)

    fun startAutoSlider(vpSlider: ViewPager) {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                context?.runOnUiThread {
                    currentPosition = (currentPosition + 1) % data.size
                    vpSlider.setCurrentItem(currentPosition, true)
                }
            }
        }, delay, delay)
    }

    fun stopAutoSlider() {
        timer?.cancel()
        timer = null
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_slider, container, false)

        //Init
        val imageView = view.findViewById<ImageView>(R.id.image)
        val imageSlide = "https://${ApiConfig.iplink.ip}/storage/slide/" + data[position]
        Picasso.get().load(imageSlide).into(imageView)
        container.addView(view)

        return view
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}