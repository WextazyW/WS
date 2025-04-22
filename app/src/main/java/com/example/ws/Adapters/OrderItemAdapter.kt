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

class OrderItemAdapter(private val items: List<OrderItemDisplay>) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_display, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgSneaker: ImageView = itemView.findViewById(R.id.imgSneaker)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        fun bind(item: OrderItemDisplay) {
            tvQuantity.text = item.quantity.toString()
            Glide.with(imgSneaker.context)
                .load(item.imageUrl)
                .into(imgSneaker)
        }
    }
}