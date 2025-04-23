package com.example.ws.Fragments

import OrderAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Model.OrderItem
import com.example.ws.Model.Sneakers
import com.example.ws.R

class OrderHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter
    private lateinit var emptyTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        emptyTextView = view.findViewById(R.id.emptyTextView)

        adapter = OrderAdapter(emptyList(), emptyMap(), emptyMap())
        recyclerView.adapter = adapter

        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        lifecycleScope.launchWhenCreated {
            try {
                val orders = RetrofitInstance.orderApi.getOrders()
                val orderItemsMap = mutableMapOf<Int, List<OrderItem>>()
                val sneakersMap = mutableMapOf<Int, Sneakers>()

                for (order in orders) {
                    val orderItems = RetrofitInstance.orderApi.getOrderItemsByOrderId(order.id)
                    orderItemsMap[order.id] = orderItems

                    for (item in orderItems) {
                        if (!sneakersMap.containsKey(item.sneakerId)) {
                            val sneaker = RetrofitInstance.orderApi.getSneakerById(item.sneakerId)
                            sneakersMap[item.sneakerId] = sneaker
                        }
                    }
                }

                if (orders.isEmpty()) {
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                adapter = OrderAdapter(orders, orderItemsMap, sneakersMap)
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }
}