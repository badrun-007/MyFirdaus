package com.badrun.my259firdaus.activity


import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.WelcomePagerAdapter

class WelcomeActivity : AppCompatActivity() {

    private lateinit var button : Button
    private lateinit var viewPager: ViewPager2
    private lateinit var layoutIndicator: LinearLayout
    private lateinit var backgroundImageView : ImageView

    private val backgroundImages = arrayOf(
        R.drawable.welcome1,
        R.drawable.welcome2,
        R.drawable.welcome3
    )
    private var currentBackgroundIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        viewPager = findViewById(R.id.viewPager)
        layoutIndicator = findViewById(R.id.layoutIndicator)
        button = findViewById(R.id.btnNext)
        backgroundImageView = findViewById(R.id.backgroundImageView)

        val adapter = WelcomePagerAdapter(this)
        viewPager.adapter = adapter

        val numPages = adapter.itemCount
        createIndicators(numPages)


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)

                val nextBackgroundIndex = position % backgroundImages.size
                val newImageResource = backgroundImages[nextBackgroundIndex]

                backgroundImageView.setImageResource(newImageResource)

                currentBackgroundIndex = nextBackgroundIndex

                if (position == numPages - 1) {
                    button.text = "Finish"
                    button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                } else {
                    button.text = "Next"
                    button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_right_24, 0)
                }
            }
        })

        button.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < numPages - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                // Action setelah selesai onboarding
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    private fun createIndicators(numPages: Int) {
        val indicators = arrayOfNulls<ImageView>(numPages)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in 0 until numPages) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageResource(R.drawable.indicator_default)
            indicators[i]?.layoutParams = layoutParams
            indicators[i]?.layoutParams?.width = 46
            indicators[i]?.layoutParams?.height = 46
            layoutIndicator.addView(indicators[i])
        }
    }

    private fun updateIndicators(position: Int) {
        val numPages = layoutIndicator.childCount
        for (i in 0 until numPages) {
            val indicator = layoutIndicator.getChildAt(i) as ImageView
            if (i == position) {
                indicator.setImageResource(R.drawable.indicator_default)
            } else {
                indicator.setImageResource(R.drawable.indicator_celected)
            }
        }
    }


}