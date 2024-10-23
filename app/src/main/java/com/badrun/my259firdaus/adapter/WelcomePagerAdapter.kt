package com.badrun.my259firdaus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R

class WelcomePagerAdapter(private val context: Context) : RecyclerView.Adapter<WelcomePagerAdapter.WelcomeViewHolder>() {

    inner class WelcomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val onboardingLayouts = arrayOf(
        R.layout.slide1,
        R.layout.slide2,
        R.layout.slide3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelcomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return WelcomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: WelcomeViewHolder, position: Int) {
        // Tampilkan tampilan onboarding di sini sesuai dengan posisi
    }

    override fun getItemCount(): Int {
        return onboardingLayouts.size
    }

    override fun getItemViewType(position: Int): Int {
        return onboardingLayouts[position]
    }
}