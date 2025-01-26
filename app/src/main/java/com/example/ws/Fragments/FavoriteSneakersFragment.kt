package com.example.ws.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ws.Adapters.AllSneakersAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.client
import com.example.ws.databinding.FragmentFavoriteSneakersBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteSneakersFragment : Fragment() {

    private lateinit var binding : FragmentFavoriteSneakersBinding
    private lateinit var adapter: AllSneakersAdapter
    private var listSneakers = mutableListOf<Sneakers>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteSneakersBinding.inflate(inflater, container, false)

        adapter = AllSneakersAdapter(listSneakers)
        binding.rvFavorites.layoutManager = GridLayoutManager(context, 2)
        binding.rvFavorites.adapter = adapter

        loadSneakers()

        return binding.root
    }

    private fun loadSneakers(){
        CoroutineScope(Dispatchers.IO).launch {
            val allSneakers = getAllSneakers()
            getFavoritesSneakers(allSneakers)
        }

    }

    private suspend fun getAllSneakers() : List<Sneakers>{
        return withContext(Dispatchers.Main){
            client.postgrest["Sneakers"]
                .select()
                .decodeAs<List<Sneakers>>()
        }
    }

    private suspend fun getFavoritesSneakers(allSneakers : List<Sneakers>){
        val sharedPreferencesFavorites = requireActivity().getSharedPreferences("favorites", Context.MODE_PRIVATE)

        listSneakers.clear()

        allSneakers.forEach {
            if(sharedPreferencesFavorites.getBoolean(it.id.toString(), false)){
                listSneakers.add(it)
            }
        }
        withContext(Dispatchers.Main){
            adapter.notifyDataSetChanged()
        }
    }

}