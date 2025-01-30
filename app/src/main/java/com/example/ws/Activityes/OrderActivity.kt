package com.example.ws.Activityes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.Adapters.BasketAdapter
import com.example.ws.Adapters.BasketTestAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderBinding
    private lateinit var adapter : BasketTestAdapter
    private var listSneakers = mutableListOf<Sneakers>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}