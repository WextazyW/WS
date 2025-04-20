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

class BasketAdapter(
    private val listSneakers: MutableList<Sneakers>,
    private val onPriceChange: (Double) -> Unit,
    context : Context
) : RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

    private val quantities = mutableMapOf<Long, Int>()
    private val sharedPreferencesBasket = context.getSharedPreferences("basket", Context.MODE_PRIVATE)

    inner class BasketViewHolder(private var binding : ItemSneakerBasketBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(sneaker : Sneakers){
            binding.productName.text = sneaker.name
            binding.productPrice.text = sneaker.price.toString()
            Glide.with(binding.root.context)
                .load(sneaker.imageUrl)
                .into(binding.productImage)

            binding.textViewCount.text = getQuantity(sneaker.id.toLong()).toString()

            binding.buttonPlus.setOnClickListener {
                updateQuantity(sneaker.id.toLong(), 1)
            }
            binding.buttonMinus.setOnClickListener {
                updateQuantity(sneaker.id.toLong(), -1)
            }

        }

        private fun getQuantity(sneakerId: Long) : Int{
            return quantities[sneakerId] ?: 1
        }

        @SuppressLint("SetTextI18n")
        private fun updateQuantity(sneakerId: Long, change: Int) {
            val currentQuantity = getQuantity(sneakerId)
            val newQuantity = (currentQuantity + change).coerceAtLeast(1)

            quantities[sneakerId] = newQuantity
            binding.textViewCount.text = newQuantity.toString()
            binding.productPrice.text = (newQuantity * listSneakers.find { it.id.toLong() == sneakerId }?.price!!).toString()

            onPriceChange(getAllPrice())
        }
    }

    fun getAllPrice() : Double{
        return listSneakers.sumOf { (it.price * (quantities[it.id.toLong()] ?: 1)).toDouble() }
    }

    fun setupSwipeToDelete(recyclerView : RecyclerView){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
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

    private fun removeItem(position: Int){
        val sneakerId = listSneakers[position].id.toString()

        val editor = sharedPreferencesBasket.edit()
        editor.putBoolean(sneakerId, false)
        editor.apply()

        quantities.remove(sneakerId.toLong())
        listSneakers.removeAt(position)
        notifyItemRemoved(position)
        onPriceChange(getAllPrice())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BasketAdapter.BasketViewHolder {
        val binding = ItemSneakerBasketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BasketAdapter.BasketViewHolder, position: Int) {
        holder.bind(listSneakers[position])
    }

    override fun getItemCount(): Int {
        return listSneakers.size
    }
}