package com.example.ws.Activityes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.MainActivity
import com.example.ws.R
import com.example.ws.Singleton.UserSession
import com.example.ws.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        UserSession.userId = 7

        binding.viewSwitcher2.visibility = View.GONE

        binding.btn1.setOnClickListener {
            binding.viewSwitcher1.showNext()
        }

        binding.btn2.setOnClickListener {
            binding.viewSwitcher2.showNext()
            binding.viewSwitcher2.visibility = View.VISIBLE
            binding.viewSwitcher1.visibility = View.GONE
        }

        binding.btn3.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
        }
    }
}