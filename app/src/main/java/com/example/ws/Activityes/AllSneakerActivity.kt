package com.example.ws.Activityes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ws.Adapters.AllSneakersAdapter
import com.example.ws.R
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.databinding.ActivityAllSneakerBinding

class AllSneakerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAllSneakerBinding
    private lateinit var adapter: AllSneakersAdapter
    private val viewModel: SneakerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllSneakerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = AllSneakersAdapter(mutableListOf())
        binding.rvSneakers.layoutManager = GridLayoutManager(this, 2)
        binding.rvSneakers.adapter = adapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        viewModel.sneakers.observe(this) { sneakers ->
            adapter.updateList(sneakers)
        }

        viewModel.loadSneakers()
    }
}