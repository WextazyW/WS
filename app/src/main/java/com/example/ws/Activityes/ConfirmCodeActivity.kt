package com.example.ws.Activityes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.R
import com.example.ws.databinding.ActivityConfirmCodeBinding

class ConfirmCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmCodeBinding
    private lateinit var email: String
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из SignUpActivity
        email = intent.getStringExtra("email").orEmpty()
        code = intent.getStringExtra("code").orEmpty()

        // Подтверждение кода
        binding.btnConfirm.setOnClickListener {
            val enteredCode = binding.etCode.text.toString()
            if (enteredCode == code) {
                Toast.makeText(this, "Код подтвержден!", Toast.LENGTH_SHORT).show()
                // Переход на следующий экран
                startActivity(Intent(this, LoginInActivity::class.java))
            } else {
                Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show()
            }
        }
    }
}