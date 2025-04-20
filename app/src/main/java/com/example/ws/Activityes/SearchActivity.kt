package com.example.ws.Activityes

import android.content.Context
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AllSneakersAdapter(mutableListOf())
        binding.rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        binding.rvSearchResults.adapter = adapter

        binding.etSearch.requestFocus()
        openKeyboard(binding.etSearch)

        viewModel.sneakers.observe(this) { sneakers ->
            adapter.updateList(sneakers)
        }

        viewModel.loadSneakers()

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

    private fun openKeyboard(editText: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}