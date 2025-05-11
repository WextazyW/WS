package com.example.ws.Activityes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.Factoryes.SignUpViewModelFactory
import com.example.ws.Http.RetrofitInstance
import com.example.ws.MainActivity
import com.example.ws.R
import com.example.ws.Singleton.UserSession
import com.example.ws.ViewModel.AuthViewModel
import com.example.ws.databinding.ActivityLoginInBinding

class LoginInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginInBinding
    private val authViewModel: AuthViewModel by viewModels { SignUpViewModelFactory(RetrofitInstance.authApi, RetrofitInstance.notificationApi, this) }
    private var isVisibility = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSession.init(this)
        enableEdgeToEdge()
        binding = ActivityLoginInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn && UserSession.userId != 0) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.tvGoSignUp.setOnClickListener {
            startActivity(Intent(this@LoginInActivity, SignUpActivity::class.java))
        }

        binding.etPassword.setOnClickListener {
            isVisibility = !isVisibility
            if (isVisibility) {
                binding.etPassword.inputType = 129
                binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0)
            } else {
                binding.etPassword.inputType = 144
                binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.signUpBtn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            binding.etEmail.error = null
            binding.etPassword.error = null

            var isValid = true

            if (email.isEmpty()) {
                binding.etEmail.error = "Введите email"
                isValid = false
            } else if (!isValidEmail(email)) {
                binding.etEmail.error = "Некорректный email"
                isValid = false
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Введите пароль"
                isValid = false
            }

            if (!isValid) {
                return@setOnClickListener
            }

            authViewModel.loginUser(email, password)
        }

        authViewModel.loginStatus.observe(this) { status ->
            if (status == "Успешный вход") {
                authViewModel.userId.let {
                    if (it != null) {
                        UserSession.userId = it
                    }
                }
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            } else {
                binding.etEmail.error = "Неверный email или пароль"
                binding.etPassword.error = "Неверный email или пароль"
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^\\S+@\\S+\\.\\S+$".toRegex()
        return emailPattern.matches(email)
    }
}