package com.example.ws.Activityes

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ws.Adapters.OrderItemAdapter
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Model.OrderItemDisplay
import com.example.ws.Model.Orders
import com.example.ws.R
import com.example.ws.databinding.ActivityOrderDetailsBinding
import com.example.ws.Singleton.UserSession
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.util.Locale

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var orderItemAdapter: OrderItemAdapter

    companion object {
        const val EXTRA_ORDER_ITEMS = "extra_order_items"
        const val EXTRA_ORDER = "extra_order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val order = intent.getSerializableExtra(EXTRA_ORDER) as? Orders
        val items = intent.getSerializableExtra(EXTRA_ORDER_ITEMS) as? List<OrderItemDisplay>

        if (order != null && items != null) {
            setupUI(order, items)
            loadUserData(order.userId)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(order: Orders, items: List<OrderItemDisplay>) {
        binding.tvOrderId.text = "№${order.id}"
        binding.tvOrderTime.text = "Дата: ${formatOrderDate(order.orderDate)}"
        binding.tvDeliveryAddress.text = "Адрес: ${order.deliveryAddress}"
        binding.tvTotalAmount.text = "Общая сумма: ${order.totalAmount} ₽"

        orderItemAdapter = OrderItemAdapter(items)
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvOrderItems.adapter = orderItemAdapter
    }

    private fun formatOrderDate(dateString: String): String {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData(userId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = RetrofitInstance.authApi.getUserById(userId)
                withContext(Dispatchers.Main) {
                    binding.tvUserName.text = "Имя: ${user.name}"
                    binding.tvUserEmail.text = "Email: ${user.email}"
                }
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 404) {
                    Log.e("userInfo","Пользователь не найден")
                } else {
                    Log.e("userInfo","Пользователь не найден")
                }
            }
        }
    }
}