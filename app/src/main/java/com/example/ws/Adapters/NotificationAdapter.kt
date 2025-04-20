package com.example.ws.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ws.Model.Notification
import com.example.ws.Model.Sneakers
import com.example.ws.databinding.ItemNotificationBinding

class NotificationAdapter(
    private var listNotification : MutableList<Notification>,
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding : ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (notifications: Notification){
            binding.tvHeader.text = notifications.header
            binding.tvBody.text = notifications.body
            binding.tvDate.text = notifications.date.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationAdapter.NotificationViewHolder,
        position: Int
    ) {
        holder.bind(listNotification[position])
    }

    override fun getItemCount(): Int {
        return listNotification.size
    }

    fun updateList(newList: List<Notification>){
        listNotification = newList.toMutableList()
        notifyDataSetChanged()
    }
}