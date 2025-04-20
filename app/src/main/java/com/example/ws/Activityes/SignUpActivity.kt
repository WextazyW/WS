package com.example.ws.Activityes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ws.Factoryes.SignUpViewModelFactory
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Model.Users
import com.example.ws.R
import com.example.ws.Singleton.UserSession
import com.example.ws.ViewModel.AuthViewModel
import com.example.ws.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel: AuthViewModel by viewModels {
        SignUpViewModelFactory(RetrofitInstance.authApi, RetrofitInstance.notificationApi, this)
    }
    private var isVisibility = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpBtn.isEnabled = false

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            binding.signUpBtn.isEnabled = isChecked
        }

        binding.tvGoSignUp.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginInActivity::class.java))
        }

        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }

        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }

        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateName()
            }
        }

        binding.etPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = binding.etPassword.compoundDrawables[2]
                if (drawableRight != null && event.rawX >= binding.etPassword.right - drawableRight.bounds.width() - 16.dpToPx()) {
                    binding.etPassword.performClick()

                    isVisibility = !isVisibility
                    binding.etPassword.inputType = if (isVisibility) {
                        129
                    } else {
                        144
                    }
                    binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        if (isVisibility) ContextCompat.getDrawable(this, R.drawable.eye_close) else ContextCompat.getDrawable(this, R.drawable.eye_open), // Правая иконка
                        null
                    )
                    binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.signUpBtn.setOnClickListener {
            if (validateFields()) {
                val user = Users(
                    name = binding.etName.text.toString(),
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    address = binding.etEmail.text.toString()
                )
                authViewModel.registerUser(user)
                startActivity(Intent(this, LoginInActivity::class.java))
            } else {
                Toast.makeText(this, "Заполните все поля корректно", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun validateFields(): Boolean {
        val isNameValid = validateName()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()

        return isNameValid && isEmailValid && isPasswordValid
    }

    private fun validateName(): Boolean {
        val name = binding.etName.text.toString()
        return if (name.isEmpty()) {
            binding.tvNameError.visibility = View.VISIBLE
            false
        } else {
            binding.tvNameError.visibility = View.GONE
            binding.etName.setBackgroundResource(R.drawable.custom_et)
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString()
        val isValid = isValidEmail(email)
        return if (!isValid) {
            binding.tvEmailError.visibility = View.VISIBLE
            false
        } else {
            binding.tvEmailError.visibility = View.GONE
            binding.etEmail.setBackgroundResource(R.drawable.custom_et)
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString()
        val isValid = password.length >= 8 && password.any { it.isLetter() } && password.any { it.isDigit() }
        return if (!isValid) {
            binding.tvPasswordError.visibility = View.VISIBLE
            false
        } else {
            binding.tvPasswordError.visibility = View.GONE
            binding.etPassword.setBackgroundResource(R.drawable.custom_et)
            true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^\\S+@\\S+\\.\\S+$".toRegex()
        return emailPattern.matches(email)
    }
}