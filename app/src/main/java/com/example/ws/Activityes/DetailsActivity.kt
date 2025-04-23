package com.example.ws.Activityes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.ws.MainActivity
import com.example.ws.R
import com.example.ws.databinding.ActivityDetailsBinding
import com.google.gson.Gson

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        val sneakerId = intent.getIntExtra("SNEAKER_ID", 0)
        val sneakersImage = intent.getStringExtra("SNEAKER_IMAGE")
        binding.desc.maxLines = 6
        binding.name.text = intent.getStringExtra("SNEAKER_NAME")
        binding.desc.text = intent.getStringExtra("SNEAKER_DESCRIPTION")
        binding.type.text = when (intent.getIntExtra("SNEAKER_TYPE", 0)) {
            1 -> "Мужские кроссовки"
            2 -> "Женские кроссовки"
            else -> "Неизвестный тип"
        }
        binding.price.text = "₽"+intent.getFloatExtra("SNEAKER_PRICE", 0f).toString()

        Glide.with(this)
            .load(sneakersImage)
            .into(binding.sneakerImage)

        binding.tvVisib.setOnClickListener {
            binding.desc.maxLines = Integer.MAX_VALUE
            binding.tvVisib.visibility = View.GONE
        }

        val sharedPreferencesFavorite = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        var isFavorite = sharedPreferencesFavorite.getBoolean(sneakerId.toString(), false)
        binding.btnAddToFavorite.setImageResource(if (isFavorite) R.drawable.favorite_fill_fix else R.drawable.favorite_fix)

        binding.btnAddToFavorite.setOnClickListener {
            val editor = sharedPreferencesFavorite.edit()
            isFavorite = !isFavorite
            editor.putBoolean(sneakerId.toString(), isFavorite)
            binding.btnAddToFavorite.setImageResource(if (isFavorite) R.drawable.favorite_fill_fix else R.drawable.favorite_fix)
            editor.apply()
        }

        val sharedPreferenceBasket = getSharedPreferences("basket", Context.MODE_PRIVATE)
        var isBasket = sharedPreferenceBasket.getBoolean(sneakerId.toString(), false)
        binding.btnAddBasket.text = if (isBasket) "Убрать из корзины" else "В корзину"

        binding.btnAddBasket.setOnClickListener {
            val editor = sharedPreferenceBasket.edit()
            val basketJson = sharedPreferenceBasket.getString("cart", "{}")
            val basketMap = Gson().fromJson(basketJson, Map::class.java) as MutableMap<String, Map<String, Any>>

            if (basketMap.containsKey(sneakerId.toString())) {
                basketMap.remove(sneakerId.toString())
            } else {
                basketMap[sneakerId.toString()] = mapOf("quantity" to 1)
            }

            editor.putString("cart", Gson().toJson(basketMap))
            editor.putBoolean(sneakerId.toString(), basketMap.containsKey(sneakerId.toString()))
            editor.apply()

            isBasket = basketMap.containsKey(sneakerId.toString())
            binding.btnAddBasket.text = if (isBasket) "Убрать из корзины" else "В корзину"
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}