package com.example.ws.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ws.Model.Sneakers
import com.example.ws.databinding.ItemSneakerBasketBinding

class BasketTestAdapter(
    private val listSneakers : MutableList<Sneakers>,
    private val onPriceChanged : (Double) -> Unit,
    private val context : Context
) : RecyclerView.Adapter<BasketTestAdapter.BasketTestViewHolder>() {

    private val sharedPreferencesBasket = context.getSharedPreferences("basket", Context.MODE_PRIVATE)
    private val quantities = mutableMapOf<Long, Int>()

    inner class BasketTestViewHolder(private var binding : ItemSneakerBasketBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(sneaker: Sneakers){
            binding.productName.text = sneaker.Name
            binding.productPrice.text = sneaker.Price.toString()
            Glide.with(binding.root.context)
                .load("https://fnuichoiatdyljxuvfkq.supabase.co/storage/v1/object/sign/SneakersPhoto/nike-zoom-winflo-3-831561-001-mens-running-shoes-11550187236tiyyje6l87_prev_ui%203.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cmwiOiJTbmVha2Vyc1Bob3RvL25pa2Utem9vbS13aW5mbG8tMy04MzE1NjEtMDAxLW1lbnMtcnVubmluZy1zaG9lcy0xMTU1MDE4NzIzNnRpeXlqZTZsODdfcHJldl91aSAzLnBuZyIsImlhdCI6MTczNzgwNDM2NiwiZXhwIjoxNzY5MzQwMzY2fQ.OkSySMFkhhPmjWZwuvTSMSY3jJb8FemVCvGC_2bGSPg&t=2025-01-25T11%3A26%3A11.938Z")
                .into(binding.productImage)

            binding.textViewCount.text = getQuantities(sneaker.id.toLong()).toString()

            binding.buttonPlus.setOnClickListener {
                updateQuantity(sneaker.id.toLong(), 1)
            }

            binding.buttonMinus.setOnClickListener {
                updateQuantity(sneaker.id.toLong(), -1)
            }
        }

        @SuppressLint("SetTextI18n")
        fun updateQuantity(sneakerId: Long, change : Int){
            val currentQuantity = getQuantities(sneakerId)
            val newQuantity = (currentQuantity + change).coerceAtLeast(1)

            quantities[sneakerId] = newQuantity
            binding.textViewCount.text = newQuantity.toString()
            binding.productPrice.text = (newQuantity * listSneakers.find { it.id.toLong() == sneakerId }?.Price!!).toString()

            onPriceChanged(getAllPrice())
        }
    }

    fun getAllPrice() : Double {
        return listSneakers.sumOf { it.Price * (quantities[it.id.toLong()] ?: 1).toDouble() }
    }

    fun swipeToDelete(recyclerView: RecyclerView){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                removeItem(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun getQuantities(sneakerId : Long) : Int{
        return quantities[sneakerId] ?: 1
    }

    fun removeItem(position: Int){
        val sneakerId = listSneakers[position].id.toString()

        val editor = sharedPreferencesBasket.edit()
        editor.putBoolean(sneakerId, false)
        editor.apply()

        quantities.remove(sneakerId.toLong())
        listSneakers.removeAt(position)
        notifyItemRemoved(position)
        onPriceChanged(getAllPrice())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BasketTestAdapter.BasketTestViewHolder {
        val binding = ItemSneakerBasketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BasketTestAdapter.BasketTestViewHolder, position: Int) {
        holder.bind(listSneakers[position])
    }

    override fun getItemCount(): Int {
        return listSneakers.size
    }
}