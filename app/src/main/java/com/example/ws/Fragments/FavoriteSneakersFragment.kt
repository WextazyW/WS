package com.example.ws.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ws.Adapters.AllSneakersAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.databinding.FragmentFavoriteSneakersBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteSneakersFragment : Fragment() {

    private lateinit var binding : FragmentFavoriteSneakersBinding
    private lateinit var adapter: AllSneakersAdapter
    private var listSneakers = mutableListOf<Sneakers>()
    private val viewModel: SneakerViewModel by lazy { SneakerViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteSneakersBinding.inflate(inflater, container, false)

        adapter = AllSneakersAdapter(listSneakers)
        binding.rvFavorites.layoutManager = GridLayoutManager(context, 2)
        binding.rvFavorites.adapter = adapter

        viewModel.sneakers.observe(viewLifecycleOwner) { sneakers ->
            val sharedPreferencesFavorites = requireActivity().getSharedPreferences("favorites", Context.MODE_PRIVATE)
            val favorites = sneakers.filter {
                sharedPreferencesFavorites.getBoolean(it.id.toString(), false)
            }
            adapter.updateList(favorites)
        }

        viewModel.loadSneakers()

        return binding.root
    }

}