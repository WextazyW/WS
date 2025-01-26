package com.example.ws.Activityes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.R
import com.example.ws.client
import com.example.ws.databinding.ActivitySignUpBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var isVisibility = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.signUpBtn.isEnabled = false

        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.signUpBtn.isEnabled = isChecked
        }

        binding.etPassword.setOnClickListener {
            isVisibility = !isVisibility
            if (isVisibility) {
                binding.etPassword.inputType = 129
                binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                    R.drawable.eye_open, 0)
            } else{
                binding.etPassword.inputType = 144
                binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                    R.drawable.eye_close, 0)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.signUpBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    client.auth.signUpWith(Email){
                        email = binding.etEmail.text.toString()
                        password = binding.etPassword.text.toString()
                    }
                    client.postgrest["Users"].insert(mapOf(
                        "Name" to binding.etName.text.toString(),
                        "Email" to binding.etEmail.text.toString(),
                        "id_uuid" to client.auth.currentSessionOrNull()?.user?.id
                    ))
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@SignUpActivity, "Успешная регистрация", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, LoginInActivity::class.java))
                    }
                } catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@SignUpActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}