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
import com.example.ws.Singleton.UserSession

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
        emptyTextView = view.findViewById(R.id.emptyTextView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrderAdapter(emptyList(), emptyMap(), emptyMap())
        recyclerView.adapter = adapter

        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        lifecycleScope.launchWhenCreated {
            try {
                val userId = UserSession.userId

                val orders = RetrofitInstance.orderApi.getOrdersByUserId(userId)

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
                    emptyTextView.text = "У вас пока нет заказов"
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                val sortedOrders = orders.sortedByDescending { it.orderDate }

                adapter = OrderAdapter(sortedOrders, orderItemsMap, sneakersMap)
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }
}