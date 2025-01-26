package com.example.ws.Activityes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.chaos.view.PinView
import com.example.ws.R
import com.example.ws.client
import com.example.ws.databinding.ActivityLoginInBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.client.plugins.api.Send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginInBinding
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

        binding.etPassword.setOnClickListener {
            isVisibility = !isVisibility
            if (isVisibility) {
                binding.etPassword.inputType = 129
                binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.eye_open, 0)
            } else{
                binding.etPassword.inputType = 144
                binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.eye_close, 0)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.signUpBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    client.auth.signInWith(Email){
                        email = binding.etEmail.text.toString()
                        password = binding.etPassword.text.toString()
                    }
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@LoginInActivity, "Успешный вход", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginInActivity, SplashActivity::class.java))
                    }
                } catch (e : Exception){
                    Toast.makeText(this@LoginInActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvVosstanovit.setOnClickListener {
            startActivity(Intent(this@LoginInActivity, SendOTPActivity::class.java))
        }

    }
}








