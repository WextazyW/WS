package com.example.ws.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ws.Activityes.DetailsActivity
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.databinding.ItemSneakerBinding

class SneakerAdapter(
    listSneaker : List<Sneakers>
) : RecyclerView.Adapter<SneakerAdapter.SneakerViewHolder>() {

    private var limitedList = if (listSneaker.size > 2) listSneaker.subList(0, 2) else listSneaker
    private var originalListSneaker: List<Sneakers> = limitedList

    inner class SneakerViewHolder(private var binding : ItemSneakerBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(sneaker: Sneakers){
            binding.tvName.text = sneaker.Name
            binding.tvPrice.text = sneaker.Price.toString()
            Glide.with(binding.root.context)
                .load("https://fnuichoiatdyljxuvfkq.supabase.co/storage/v1/object/sign/SneakersPhoto/nike-zoom-winflo-3-831561-001-mens-running-shoes-11550187236tiyyje6l87_prev_ui%203.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cmwiOiJTbmVha2Vyc1Bob3RvL25pa2Utem9vbS13aW5mbG8tMy04MzE1NjEtMDAxLW1lbnMtcnVubmluZy1zaG9lcy0xMTU1MDE4NzIzNnRpeXlqZTZsODdfcHJldl91aSAzLnBuZyIsImlhdCI6MTczNzcxNTk2NSwiZXhwIjoxNzY5MjUxOTY1fQ.fRY08IoU9h-hgc8L64UqrhZSD_c6TnDabI5JEFnN8-s&t=2025-01-24T10%3A52%3A48.518Z")
                .into(binding.sneakerImage)

            val sharedPreferenceFavorite = binding.root.context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
            var isFavorite = sharedPreferenceFavorite.getBoolean(sneaker.id.toString(), false)
            binding.btnAddFavorite.setImageResource(if (isFavorite) R.drawable.favorite_fill else R.drawable.favorite)

            binding.btnAddFavorite.setOnClickListener {
                val editor = sharedPreferenceFavorite.edit()
                if (isFavorite){
                    isFavorite = !isFavorite
                    editor.putBoolean(sneaker.id.toString(), false)
                    binding.btnAddFavorite.setImageResource(R.drawable.favorite)
                } else {
                    editor.putBoolean(sneaker.id.toString(), true)
                    binding.btnAddFavorite.setImageResource(R.drawable.favorite_fill)
                }
                editor.apply()
            }

            val sharedPreferenceBasket = binding.root.context.getSharedPreferences("basket", Context.MODE_PRIVATE)
            var isBasket = sharedPreferenceBasket.getBoolean(sneaker.id.toString(), false)
            binding.addToBasket.setImageResource(if (isBasket) R.drawable.frame_1000000821__1_ else R.drawable.frame_1000000821)

            binding.addToBasket.setOnClickListener {
                val editor = sharedPreferenceBasket.edit()
                if (isBasket){
                    isBasket = !isBasket
                    editor.putBoolean(sneaker.id.toString(), false)
                    binding.addToBasket.setImageResource(R.drawable.frame_1000000821)
                } else {
                    editor.putBoolean(sneaker.id.toString(), true)
                    binding.addToBasket.setImageResource(R.drawable.frame_1000000821__1_)
                }
                editor.apply()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SneakerAdapter.SneakerViewHolder {
        val binding = ItemSneakerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SneakerViewHolder(binding)
    }

    fun filterList(category : String){
        limitedList = if (category == "Все"){
            originalListSneaker.take(2)
        } else{
            originalListSneaker.filter { it.Name == category }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SneakerAdapter.SneakerViewHolder, position: Int) {
        holder.bind(limitedList[position])
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("SNEAKER_ID", limitedList[position].id)
                putExtra("SNEAKER_NAME", limitedList[position].Name)
                putExtra("SNEAKER_PRICE", limitedList[position].Price)
                putExtra("SNEAKER_DESCRIPTION", limitedList[position].Description)
                putExtra("SNEAKER_IMAGE", "https://fnuichoiatdyljxuvfkq.supabase.co/storage/v1/object/sign/SneakersPhoto/nike-zoom-winflo-3-831561-001-mens-running-shoes-11550187236tiyyje6l87_prev_ui%203.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cmwiOiJTbmVha2Vyc1Bob3RvL25pa2Utem9vbS13aW5mbG8tMy04MzE1NjEtMDAxLW1lbnMtcnVubmluZy1zaG9lcy0xMTU1MDE4NzIzNnRpeXlqZTZsODdfcHJldl91aSAzLnBuZyIsImlhdCI6MTczNzcxNTk2NSwiZXhwIjoxNzY5MjUxOTY1fQ.fRY08IoU9h-hgc8L64UqrhZSD_c6TnDabI5JEFnN8-s&t=2025-01-24T10%3A52%3A48.518Z")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return limitedList.size
    }

    fun updateList(newList: List<Sneakers>){
        limitedList = if (newList.size > 2) newList.subList(0, 2) else newList
        notifyDataSetChanged()

    }
}