package com.example.ws.Activityes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.Adapters.BasketAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mapImageView = findViewById<ImageView>(R.id.mapImageView)

        mapImageView.setOnClickListener {
            openGoogleMaps(binding.tvAddress.text.toString().trim())
        }
    }

    private fun openGoogleMaps(query: String) {
        val uri = Uri.parse("geo:0,0?q=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (intent.resolveActivity(packageManager) != null) {
            Log.d("map", "Opening Google Maps app for query: $query")
            startActivity(intent)
        } else {
            Log.d("map", "Google Maps app not found, opening in browser")

            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
        }
    }
}