package com.example.ws.Activityes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.Factoryes.SignUpViewModelFactory
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Model.Users
import com.example.ws.R
import com.example.ws.ViewModel.AuthViewModel
import com.example.ws.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel: AuthViewModel by viewModels { SignUpViewModelFactory(RetrofitInstance.authApi, this) }
    private var isVisibility = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.signUpBtn.isEnabled = false

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            binding.signUpBtn.isEnabled = isChecked
        }

        binding.etPassword.setOnClickListener {
            isVisibility = !isVisibility
            binding.etPassword.inputType = if (isVisibility) {
                129
            } else {
                144
            }
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            if (isVisibility) R.drawable.eye_open else R.drawable.eye_close, 0)
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.signUpBtn.setOnClickListener {
            if (binding.etName.text.isNotEmpty() || binding.etEmail.text.isNotEmpty() || binding.etPassword.text.isNotEmpty()){
                if (isValidEmail(binding.etEmail.text.toString())){
                    val user = Users(
                        name = binding.etName.text.toString(),
                        email = binding.etEmail.text.toString(),
                        password = binding.etPassword.text.toString()
                    )
                    authViewModel.registerUser(user)
                } else {
                    Toast.makeText(this, "Неправильный формат email", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.registrationStatus.observe(this) {
            startActivity(Intent(this, LoginInActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^\\S+@\\S+\\.\\S+$".toRegex()
        return emailPattern.matches(email)
    }
}