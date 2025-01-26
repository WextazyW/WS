package com.example.ws

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.ws.Activityes.BasketActivity
import com.example.ws.Activityes.BasketTestActivity
import com.example.ws.Fragments.FavoriteSneakersFragment
import com.example.ws.Fragments.HomeFragment
import com.example.ws.Fragments.ProfileAllFragment
import com.example.ws.Fragments.ProfileFragment
import com.example.ws.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        binding.btnGoBasket.setOnClickListener {
            startActivity(Intent(this, BasketTestActivity::class.java))
        }
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_favorite -> replaceFragment(FavoriteSneakersFragment())
                R.id.nav_profile -> replaceFragment(ProfileAllFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}