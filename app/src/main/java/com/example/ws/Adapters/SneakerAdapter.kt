package com.example.ws.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ws.Activityes.DetailsActivity
import com.example.ws.MainActivity
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.databinding.ItemSneakerBinding

class SneakerAdapter(
    listSneaker : List<Sneakers>
) : RecyclerView.Adapter<SneakerAdapter.SneakerViewHolder>() {

    private var limitedList = if (listSneaker.size > 4) listSneaker.subList(0, 4) else listSneaker

    inner class SneakerViewHolder(private var binding : ItemSneakerBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(sneaker: Sneakers){
            binding.tvName.text = sneaker.name
            binding.tvPrice.text = sneaker.price.toString()
            Glide.with(binding.root.context)
                .load(sneaker.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                isBasket = !isBasket
                editor.putBoolean(sneaker.id.toString(), isBasket)
                binding.addToBasket.setImageResource(if (isBasket) R.drawable.frame_1000000821__1_ else R.drawable.frame_1000000821)

                if (isBasket) {
                    updateBasketCounter(binding.root.context, 1)
                } else {
                    updateBasketCounter(binding.root.context, -1)
                }

                editor.apply()
            }
        }
    }

    private fun updateBasketCounter(context: Context, delta: Int) {
        val sharedPreferenceBasket = context.getSharedPreferences("basket", Context.MODE_PRIVATE)
        val currentCount = sharedPreferenceBasket.getInt("basket_count", 0)
        val newCount = currentCount + delta
        sharedPreferenceBasket.edit().putInt("basket_count", newCount).apply()

        val activity = context as? MainActivity
        activity?.updateBasketCounter(newCount)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SneakerAdapter.SneakerViewHolder {
        val binding = ItemSneakerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SneakerViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SneakerAdapter.SneakerViewHolder, position: Int) {
        holder.bind(limitedList[position])
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("SNEAKER_ID", limitedList[position].id)
                putExtra("SNEAKER_NAME", limitedList[position].name)
                putExtra("SNEAKER_PRICE", limitedList[position].price)
                putExtra("SNEAKER_DESCRIPTION", limitedList[position].description)
                putExtra("SNEAKER_IMAGE", limitedList[position].imageUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return limitedList.size
    }

    fun updateList(newList: List<Sneakers>){
        limitedList = if (newList.size > 4) newList.subList(0, 4) else newList
        notifyDataSetChanged()
    }
}