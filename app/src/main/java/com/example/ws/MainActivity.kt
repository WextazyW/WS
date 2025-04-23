package com.example.ws

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.ws.Activityes.BasketActivity
import com.example.ws.Activityes.LoginInActivity
import com.example.ws.Fragments.FavoriteSneakersFragment
import com.example.ws.Fragments.HomeFragment
import com.example.ws.Fragments.NotificationFragment
import com.example.ws.Fragments.OrderHistoryFragment
import com.example.ws.Fragments.ProfileFragment
import com.example.ws.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var badgeDrawable: BadgeDrawable

    @ExperimentalBadgeUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val sharedPreferencesFavorite: SharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val sharedPreferencesBasket: SharedPreferences = getSharedPreferences("basket", Context.MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fab = findViewById<FloatingActionButton>(R.id.btn_go_basket)
        badgeDrawable = BadgeDrawable.create(this).apply {
            isVisible = true
            backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.red)
            badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.red)
        }

        BadgeUtils.attachBadgeDrawable(badgeDrawable, fab)

        val sharedPreferenceBasket = getSharedPreferences("basket", MODE_PRIVATE)
        val basketCount = sharedPreferenceBasket.getInt("basket_count", 0)
        updateBasketCounter(basketCount)

        drawerLayout = binding.drawerLayout
        drawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        replaceFragment(HomeFragment())

        binding.btnGoBasket.setOnClickListener {
            startActivity(Intent(this, BasketActivity::class.java))
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_favorite -> replaceFragment(FavoriteSneakersFragment())
                R.id.nav_profile -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                R.id.nav_notification -> replaceFragment(NotificationFragment())
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                }
                R.id.nav_notification -> {
                    replaceFragment(NotificationFragment())
                }
                R.id.nav_favorite -> {
                    replaceFragment(FavoriteSneakersFragment())
                }
                R.id.nav_orders -> {
                    replaceFragment(OrderHistoryFragment())
                }
                R.id.nav_exit -> {
                    AlertDialog.Builder(this)
                        .setTitle("Выход")
                        .setMessage("Вы уверены, что хотите выйти?")
                        .setPositiveButton("Да") { _, _ ->
                            sharedPreferences.edit().clear().apply()
                            sharedPreferencesFavorite.edit().clear().apply()
                            sharedPreferencesBasket.edit().clear().apply()
                            val intent = Intent(this, LoginInActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                            drawerLayout.closeDrawer(GravityCompat.START)
                        }
                        .setNegativeButton("Отмена", null)
                        .show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    fun updateBasketCounter(count: Int) {
        badgeDrawable.number = count
        badgeDrawable.isVisible = count > 0
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}