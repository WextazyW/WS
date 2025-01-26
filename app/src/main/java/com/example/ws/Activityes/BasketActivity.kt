package com.example.ws.Activityes

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ws.Adapters.BasketAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.client
import com.example.ws.databinding.ActivityBasketBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasketActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBasketBinding
    private var listSneakers = mutableListOf<Sneakers>()
    private lateinit var adapter : BasketAdapter

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

        adapter = BasketAdapter(listSneakers, ::updateTotalPrice, this)
        binding.rvBasket.layoutManager = LinearLayoutManager(this)
        binding.rvBasket.adapter = adapter

        adapter.setupSwipeToDelete(binding.rvBasket)

        loadBasket()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice(totalPrice: Double) {
        binding.tvSumPrice.text = String.format("%.2f",totalPrice)
        val deliveryCost = 34.32
        binding.tvDostavka.text = deliveryCost.toString()
        val finalTotal = totalPrice + deliveryCost
        binding.tvItogo.text = String.format("%.2f",finalTotal)
    }

    private fun loadBasket(){
        CoroutineScope(Dispatchers.IO).launch {
            val allSneakers = getAllSneakers()
            setBasketSneakers(allSneakers)
        }
    }

    private suspend fun getAllSneakers() : List<Sneakers>{
        return withContext(Dispatchers.Main){
            client.postgrest["Sneakers"]
                .select()
                .decodeAs()
        }
    }

    private suspend fun setBasketSneakers(allSneakers : List<Sneakers>){
        val sharedPreferencesBasket = getSharedPreferences("basket", Context.MODE_PRIVATE)

        listSneakers.clear()

        allSneakers.forEach {
            if (sharedPreferencesBasket.getBoolean(it.id.toString(), false))
                listSneakers.add(it)
        }

        withContext(Dispatchers.Main){
            adapter.notifyDataSetChanged()
            updateTotalPrice(adapter.getAllPrice())
            binding.tvCount.text = listSneakers.size.toString() + " товара"
        }
    }
}