package com.example.ws.Activityes

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ws.Adapters.BasketTestAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.client
import com.example.ws.databinding.ActivityBasketTestBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasketTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasketTestBinding
    private lateinit var adapter : BasketTestAdapter
    private var listSneakers = mutableListOf<Sneakers>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBasketTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = BasketTestAdapter(listSneakers, ::updatePrice, this)
        binding.rvBasket.layoutManager = LinearLayoutManager(this)
        binding.rvBasket.adapter = adapter

        adapter.swipeToDelete(binding.rvBasket)

        binding.tvCount.text = listSneakers.size.toString()

        loadSneakers()
    }

    fun updatePrice(totalPrice : Double){
        binding.tvSumPrice.text = String.format("%.2f", totalPrice)
        val dostavka = 32.42
        binding.tvDostavka.text = dostavka.toString()
        val itogoPrice = totalPrice + dostavka
        binding.tvItogo.text = String.format("%.2f", itogoPrice)
        binding.tvCount.text = listSneakers.size.toString()
    }

    private fun loadSneakers(){
        CoroutineScope(Dispatchers.IO).launch {
            val allSneakers = getAllSneakers()
            setBasketSneakers(allSneakers)
            binding.tvCount.text = listSneakers.size.toString()
        }
    }

    private suspend fun getAllSneakers() : List<Sneakers>{
        return withContext(Dispatchers.Main){
            client.postgrest["Sneakers"]
                .select()
                .decodeAs()
        }
    }

    private suspend fun setBasketSneakers(allSneakers: List<Sneakers>){
        val sharedPreferencesBasket = getSharedPreferences("basket", Context.MODE_PRIVATE)

        listSneakers.clear()

        allSneakers.forEach {
            if (sharedPreferencesBasket.getBoolean(it.id.toString(), false)){
                listSneakers.add(it)
            }
        }

        withContext(Dispatchers.Main){
            adapter.notifyDataSetChanged()
            updatePrice(adapter.getAllPrice())
            binding.tvCount.text = listSneakers.size.toString()
        }
    }
}