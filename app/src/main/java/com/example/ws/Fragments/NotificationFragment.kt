package com.example.ws.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ws.Adapters.NotificationAdapter
import com.example.ws.Adapters.SneakerAdapter
import com.example.ws.Model.Notification
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.Singleton.UserSession
import com.example.ws.ViewModel.NotificationViewModel
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {


    private lateinit var binding: FragmentNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private var listNotifications = mutableListOf<Notification>()
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        Toast.makeText(context, "${UserSession.userId}", Toast.LENGTH_SHORT).show()
        adapter = NotificationAdapter(listNotifications)
        binding.rvNotification.layoutManager = LinearLayoutManager(context)
        binding.rvNotification.adapter = adapter

        viewModel.notification.observe(viewLifecycleOwner) { notifications ->
            if (notifications.isEmpty()) {
                binding.tvNoNotifications.visibility = View.VISIBLE
                binding.rvNotification.visibility = View.GONE
            } else {
                binding.tvNoNotifications.visibility = View.GONE
                binding.rvNotification.visibility = View.VISIBLE
            }
            adapter.updateList(notifications)
        }

        viewModel.loadNotification()

        return binding.root
    }

}