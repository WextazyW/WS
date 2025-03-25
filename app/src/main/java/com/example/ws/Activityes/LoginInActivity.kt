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
import com.example.ws.Singleton.ToastUtil
import com.example.ws.ViewModel.AuthViewModel
import com.example.ws.databinding.ActivityLoginInBinding

class LoginInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginInBinding
    private val authViewModel: AuthViewModel by viewModels { SignUpViewModelFactory(RetrofitInstance.authApi, this) }
    private var isVisibility = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        if (isLoggedIn) {
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
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                ToastUtil.showIsNotEmptyToast(this)
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                ToastUtil.showIsValidEmailToast(this)
                return@setOnClickListener
            }

            authViewModel.loginUser(email, password)
        }

        authViewModel.loginStatus.observe(this) { status ->
            if (status == "Успешный вход") {
                ToastUtil.showSuccessToast(this)
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            } else {
                ToastUtil.showFailedToast(this)
            }
        }

        binding.tvVosstanovit.setOnClickListener {
            startActivity(Intent(this@LoginInActivity, SendOTPActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^\\S+@\\S+\\.\\S+$".toRegex()
        return emailPattern.matches(email)
    }

}








