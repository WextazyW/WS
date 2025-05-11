package com.example.ws.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ws.Activityes.DetailsActivity
import com.example.ws.MainActivity
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.databinding.ItemSneakerBinding
import com.google.gson.Gson

class AllSneakersAdapter(
    private var listSneakers: List<Sneakers>
) : RecyclerView.Adapter<AllSneakersAdapter.AllSneakersViewHolder>() {

    inner class AllSneakersViewHolder(private var binding : ItemSneakerBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(sneaker: Sneakers){
            binding.tvName.text = sneaker.name
            binding.tvPrice.text = sneaker.price.toString()
            Glide.with(binding.root.context)
                .load(sneaker.imageUrl)
                .into(binding.sneakerImage)

            val sharedPreferenceFavorite = binding.root.context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
            var isFavorite = sharedPreferenceFavorite.getBoolean(sneaker.id.toString(), false)
            binding.btnAddFavorite.setImageResource(if (isFavorite) R.drawable.favorite_fill else R.drawable.favorite)

            binding.btnAddFavorite.setOnClickListener {
                val editor = sharedPreferenceFavorite.edit()
                isFavorite = !isFavorite
                editor.putBoolean(sneaker.id.toString(), isFavorite)
                binding.btnAddFavorite.setImageResource(if (isFavorite) R.drawable.favorite_fill else R.drawable.favorite)
                editor.apply()
            }

            val sharedPreferenceBasket = binding.root.context.getSharedPreferences("basket", Context.MODE_PRIVATE)
            var isBasket = sharedPreferenceBasket.getBoolean(sneaker.id.toString(), false)
            binding.addToBasket.setImageResource(if (isBasket) R.drawable.frame_1000000821__1_ else R.drawable.frame_1000000821)

            binding.addToBasket.setOnClickListener {
                val editor = sharedPreferenceBasket.edit()
                val basketJson = sharedPreferenceBasket.getString("cart", "{}")
                val basketMap = Gson().fromJson(basketJson, Map::class.java) as MutableMap<String, Map<String, Any>>

                val sneakerId = sneaker.id.toString()

                if (basketMap.containsKey(sneakerId)) {
                    basketMap.remove(sneakerId)
                } else {
                    basketMap[sneakerId] = mapOf("quantity" to 1)
                }

                editor.putString("cart", Gson().toJson(basketMap))
                editor.putBoolean(sneakerId, basketMap.containsKey(sneakerId))
                editor.apply()

                binding.addToBasket.setImageResource(if (basketMap.containsKey(sneakerId)) R.drawable.frame_1000000821__1_ else R.drawable.frame_1000000821)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllSneakersAdapter.AllSneakersViewHolder {
        val binding = ItemSneakerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllSneakersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllSneakersAdapter.AllSneakersViewHolder, position: Int) {
        holder.bind(listSneakers[position])
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("SNEAKER_NAME", listSneakers[position].name)
                putExtra("SNEAKER_PRICE", listSneakers[position].price)
                putExtra("SNEAKER_TYPE", listSneakers[position].typeId)
                putExtra("SNEAKER_DESCRIPTION", listSneakers[position].description)
                putExtra("SNEAKER_IMAGE", listSneakers[position].imageUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listSneakers.size
    }

    fun updateList(newList: List<Sneakers>){
        listSneakers = newList
        notifyDataSetChanged()
    }
}