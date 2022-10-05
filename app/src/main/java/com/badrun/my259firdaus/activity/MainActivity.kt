package com.badrun.my259firdaus.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.fragment.AkunFragment
import com.badrun.my259firdaus.fragment.HomeFragment
import com.badrun.my259firdaus.fragment.NewsFragment
import com.badrun.my259firdaus.fragment.QuranFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class MainActivity : AppCompatActivity() {

    private lateinit var buttonNav : MeowBottomNavigation
    private val fm = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonNav = findViewById(R.id.nav_view)

        buttonNav.add(MeowBottomNavigation.Model(0,R.drawable.ic_home_black_24dp))
        buttonNav.add(MeowBottomNavigation.Model(1,R.drawable.ic_dashboard_black_24dp))
        buttonNav.add(MeowBottomNavigation.Model(2,R.drawable.ic_baseline_menu_book_24))
        buttonNav.add(MeowBottomNavigation.Model(3,R.drawable.ic_baseline_account_circle_24))

        loadFragment(HomeFragment())
        buttonNav.show(0,true)

        buttonNav.setOnClickMenuListener {
            when (it.id) {
                0 -> {
                    loadFragment(HomeFragment())
                }
                1 -> {
                    loadFragment(NewsFragment())
                }
                2 -> {
                    loadFragment(QuranFragment())
                }
                3 -> {
                    loadFragment(AkunFragment())
                }
            }
        }


    }
    private fun loadFragment(frag : Fragment){
            fm.beginTransaction().replace(R.id.container,frag).addToBackStack(Fragment::class.java.simpleName).commit()

    }
}