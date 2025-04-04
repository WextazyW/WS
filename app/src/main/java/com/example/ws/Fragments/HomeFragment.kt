package com.example.ws.Fragments

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
import com.example.ws.Adapters.SneakerAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.client
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

        binding.btnAll.setOnClickListener {
            adapter.filterList("Все")
        }

        binding.btnOutdoor.setOnClickListener {
            adapter.filterList("Nike")
        }

        binding.btnTennis.setOnClickListener {
            adapter.filterList("Adidas")
        }
        
        adapter = SneakerAdapter(listSneakers)
        binding.rvSneakersTwo.layoutManager = GridLayoutManager(context, 2)
        binding.rvSneakersTwo.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) { filterServices(s.toString()) }
        })

        viewModel.sneakers.observe(viewLifecycleOwner) { sneakers ->
            adapter.updateList(sneakers)
        }

        viewModel.loadSneakers()

        return binding.root
    }

    private fun filterServices(s : String){
        val filterList = listSneakers.filter {
            it.name.contains(s, ignoreCase = true)
        }
        adapter.updateList(filterList)
    }

}