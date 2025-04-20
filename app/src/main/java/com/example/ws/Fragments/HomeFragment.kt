package com.example.ws.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ws.Activityes.SearchActivity
import com.example.ws.Adapters.SneakerAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.databinding.FragmentHomeBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter : SneakerAdapter
    private var listSneakers = mutableListOf<Sneakers>()
    private val viewModel : SneakerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        adapter = SneakerAdapter(listSneakers)
        binding.rvSneakersTwo.layoutManager = GridLayoutManager(context, 2)
        binding.rvSneakersTwo.adapter = adapter

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startActivity(Intent(context, SearchActivity::class.java))
            }
        }

        viewModel.sneakers.observe(viewLifecycleOwner) { sneakers ->
            binding.shimmerLayout.stopShimmer()

            binding.shimmerLayout.visibility = View.GONE
            binding.rvSneakersTwo.visibility = View.VISIBLE

            adapter.updateList(sneakers)
        }

        binding.shimmerLayout.startShimmer()

        viewModel.loadSneakers()

        return binding.root
    }

}