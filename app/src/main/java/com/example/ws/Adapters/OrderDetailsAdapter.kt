package com.example.ws.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ws.Model.OrderItemDisplay
import com.example.ws.R

class OrderDetailsAdapter(private val items: List<OrderItemDisplay>) :
    RecyclerView.Adapter<OrderDetailsAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_details, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgSneaker: ImageView = itemView.findViewById(R.id.imgSneaker)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        fun bind(item: OrderItemDisplay) {
            tvProductName.text = item.name
            tvProductPrice.text = "₽${item.price}"
            tvQuantity.text = "Количество: ${item.quantity}"

            Glide.with(imgSneaker.context)
                .load(item.imageUrl)
                .into(imgSneaker)
        }
    }
}