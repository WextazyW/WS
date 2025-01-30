package com.example.ws.Activityes

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

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsBinding

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

        val sneakerId = intent.getIntExtra("SNEAKER_ID", 0)
        val sneakersImage = intent.getStringExtra("SNEAKER_IMAGE")
        binding.desc.maxLines = 3
        binding.name.text = intent.getStringExtra("SNEAKER_NAME")
        binding.desc.text = intent.getStringExtra("SNEAKER_DESCRIPTION")
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
        binding.btnAddToFavorite.setImageResource(if (isFavorite) R.drawable.favorite_fill else R.drawable.favorite)

        binding.btnAddToFavorite.setOnClickListener {
            val editor = sharedPreferencesFavorite.edit()
            if (isFavorite){
                isFavorite = !isFavorite
                editor.putBoolean(sneakerId.toString(), false)
                binding.btnAddToFavorite.setImageResource(R.drawable.favorite)
            }
            else {
                editor.putBoolean(sneakerId.toString(), true)
                binding.btnAddToFavorite.setImageResource(R.drawable.favorite_fill)
            }
            editor.apply()
        }

        val sharedPreferenceBasket = getSharedPreferences("basket", Context.MODE_PRIVATE)
        var isBasket = sharedPreferenceBasket.getBoolean(sneakerId.toString(), false)
        binding.btnAddBasket.text = if (isBasket) "Убрать из корзины" else "В корзину"

        binding.btnAddBasket.setOnClickListener {
            val editor = sharedPreferenceBasket.edit()
            isBasket = !isBasket
            if (isBasket){
                editor.putBoolean(sneakerId.toString(), true)
                binding.btnAddBasket.text = "Убрать из корзины"
            } else {
                editor.putBoolean(sneakerId.toString(), false)
                binding.btnAddBasket.text = "В корзину"
            }
            editor.apply()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}