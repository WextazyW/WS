package com.example.ws.Activityes

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ws.Adapters.BasketAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.databinding.ActivityBasketBinding

class BasketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasketBinding
    private var listSneakers = mutableListOf<Sneakers>()
    private lateinit var adapter: BasketAdapter
    private val viewModel: SneakerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = Color.parseColor("#F7F7F9")
        window.navigationBarColor = Color.parseColor("#FFFFFF")

        binding.btnBack.setOnClickListener {
            finish()
        }

        adapter = BasketAdapter(listSneakers, ::updateTotalPrice, this)
        binding.rvBasket.layoutManager = LinearLayoutManager(this)
        binding.rvBasket.adapter = adapter

        adapter.setupSwipeToDelete(binding.rvBasket)

        binding.btnCreateOrder.setOnClickListener {
            if (listSneakers.isEmpty()) {
                Toast.makeText(this@BasketActivity, "Корзина пуста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this@BasketActivity, OrderActivity::class.java).apply {
                putExtra("totalPrice", adapter.getAllPrice())
                putExtra("deliveryCost", 34.32)
                putExtra("finalTotal", adapter.getAllPrice() + 34.32)
            }
            startActivity(intent)
        }

        viewModel.sneakers.observe(this) { allSneakers ->
            val basketSneakers = viewModel.getSneakersInBasket(this)
            updateBasket(basketSneakers)
        }

        viewModel.loadSneakers()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateTotalPrice(totalPrice: Double) {
        binding.tvSumPrice.text = String.format("%.2f", totalPrice)
        val deliveryCost = 34.32
        binding.tvDostavka.text = deliveryCost.toString()
        val finalTotal = totalPrice + deliveryCost
        binding.tvItogo.text = String.format("%.2f", finalTotal)
    }

    private fun updateBasket(newList: List<Sneakers>) {
        listSneakers.clear()
        listSneakers.addAll(newList)
        adapter.notifyDataSetChanged()
        updateTotalPrice(adapter.getAllPrice())
    }

}