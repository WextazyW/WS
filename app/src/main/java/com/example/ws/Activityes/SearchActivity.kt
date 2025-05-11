package com.example.ws.Activityes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ws.Adapters.AllSneakersAdapter
import com.example.ws.R
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: AllSneakersAdapter
    private val viewModel: SneakerViewModel by viewModels()

    private var currentFilterType: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        window.navigationBarColor = Color.parseColor("#F7F7F9")
        window.statusBarColor = Color.parseColor("#F7F7F9")

        adapter = AllSneakersAdapter(mutableListOf())
        binding.rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        binding.rvSearchResults.adapter = adapter

        binding.etSearch.requestFocus()
        openKeyboard(binding.etSearch)

        viewModel.sneakers.observe(this) { sneakers ->
            adapter.updateList(sneakers)
        }

        viewModel.loadSneakers()

        binding.btnMensShoes.setOnClickListener {
            if (currentFilterType == 1) {
                resetFilter()
            } else {
                filterByType(1)
                updateButtonStyles(isMenSelected = true)
                currentFilterType = 1
            }
        }

        binding.btnWomenShoes.setOnClickListener {
            if (currentFilterType == 2) {
                resetFilter()
            } else {
                filterByType(2)
                updateButtonStyles(isMenSelected = false)
                currentFilterType = 2
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterServices(s.toString())
            }
        })
    }

    private fun filterServices(query: String) {
        viewModel.sneakers.value?.let { sneakers ->
            val filteredList = sneakers.filter { it.name.contains(query, ignoreCase = true) }
            adapter.updateList(filteredList)
        }
    }

    private fun filterByType(typeId: Int) {
        viewModel.sneakers.value?.let { sneakers ->
            val filteredList = sneakers.filter { it.typeId == typeId }
            adapter.updateList(filteredList)
        }
    }

    private fun resetFilter() {
        viewModel.sneakers.value?.let { sneakers ->
            adapter.updateList(sneakers)
        }
        updateButtonStyles(isMenSelected = null)
        currentFilterType = null
    }

    private fun updateButtonStyles(isMenSelected: Boolean?) {
        when (isMenSelected) {
            true -> {
                binding.btnMensShoes.setBackgroundColor(Color.parseColor("#48B2E7"))
                binding.btnMensShoes.setTextColor(Color.WHITE)
                binding.btnWomenShoes.setBackgroundColor(Color.parseColor("#F7F7F9"))
                binding.btnWomenShoes.setTextColor(Color.GRAY)
            }
            false -> {
                binding.btnWomenShoes.setBackgroundColor(Color.parseColor("#48B2E7"))
                binding.btnWomenShoes.setTextColor(Color.WHITE)
                binding.btnMensShoes.setBackgroundColor(Color.parseColor("#F7F7F9"))
                binding.btnMensShoes.setTextColor(Color.GRAY)
            }
            null -> {
                binding.btnMensShoes.setBackgroundColor(Color.parseColor("#F7F7F9"))
                binding.btnMensShoes.setTextColor(Color.GRAY)
                binding.btnWomenShoes.setBackgroundColor(Color.parseColor("#F7F7F9"))
                binding.btnWomenShoes.setTextColor(Color.GRAY)
            }
        }
    }

    private fun openKeyboard(editText: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}