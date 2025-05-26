package com.example.ws.Activityes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ws.Http.RetrofitInstance
import com.example.ws.MainActivity
import com.example.ws.Model.Notification
import com.example.ws.Model.OrderItem
import com.example.ws.Model.Orders
import com.example.ws.Model.Users
import com.example.ws.R
import com.example.ws.Singleton.UserSession
import com.example.ws.ViewModel.NotificationViewModel
import com.example.ws.databinding.ActivityOrderBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizeIntent
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Currency
import java.util.Locale

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private var totalPrice: Double = 0.0
    private var deliveryCost: Double = 0.0
    private var finalTotal: Double = 0.0
    private var tokenAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = Color.parseColor("#F7F7F9")
        window.navigationBarColor = Color.parseColor("#FFFFFF")

        intent.extras?.let {
            totalPrice = it.getDouble("totalPrice")
            deliveryCost = it.getDouble("deliveryCost")
            finalTotal = it.getDouble("finalTotal")
        }

        updateUIWithPrices()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userId = UserSession.userId
                if (userId != null) {
                    val user = getUserData(userId)
                    updateUIWithUserData(user)
                } else {
                    Toast.makeText(this@OrderActivity, "User ID not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("OrderActivity", "Error fetching user data", e)
                Toast.makeText(this@OrderActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        }

        val mapImageView = findViewById<ImageView>(R.id.mapImageView)
        mapImageView.setOnClickListener {
            openGoogleMaps(binding.tvAddress.text.toString().trim())
        }

        binding.btnCreateOrder.setOnClickListener {
            startPayment()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }


    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateUIWithPrices() {
        binding.tvSumPrice.text = String.format("%.2f", totalPrice)
        binding.tvDostavka.text = String.format("%.2f", deliveryCost)
        binding.tvItogo.text = String.format("%.2f", finalTotal)
    }

    private suspend fun getUserData(userId: Int): Users {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.authApi.getUserById(userId)
        }
    }

    private fun updateUIWithUserData(user: Users) {
        binding.tvEmail.text = user.email
        binding.tvAddress.text = user.address
    }

    private fun openGoogleMaps(query: String) {
        val uri = Uri.parse("geo:0,0?q=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (intent.resolveActivity(packageManager) != null) {
            Log.d("map", "Opening Google Maps app for query: $query")
            startActivity(intent)
        } else {
            Log.d("map", "Google Maps app not found, opening in browser")

            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
        }
    }

    private fun startPayment() {
        tokenAmount = finalTotal
        val paymentParameters = PaymentParameters(
            amount = Amount(BigDecimal.valueOf("%.2f".format(Locale.US, tokenAmount).toDouble()), Currency.getInstance("RUB")),
            title = "Кроссовки Nike",
            subtitle = "Описание товара",
            clientApplicationKey = "test_MTA2MjU0MOUIhopGcGHlehf1n1nMu2TInO-K8Q0-JcM",
            shopId = "1062540",
            savePaymentMethod = SavePaymentMethod.ON,
            paymentMethodTypes = setOf(PaymentMethodType.BANK_CARD),
            userPhoneNumber = "+79872850257"
        )

        val intent = createTokenizeIntent(this, paymentParameters)
        tokenizeLauncher.launch(intent)
    }

    private val tokenizeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val paymentToken = data.getStringExtra("ru.yoomoney.sdk.kassa.payments.extra.PAYMENT_TOKEN")
                if (paymentToken != null) {
                    println("Payment token: $paymentToken")
                    sendPaymentTokenToServer(paymentToken)
                } else {
                    println("Payment token is null")
                }
            } else {
                println("Intent data is null")
            }
        } else if (result.resultCode == RESULT_CANCELED) {
            println("Payment canceled by user")
        } else {
            val error = result.data?.getSerializableExtra("error") as? Exception
            if (error != null) {
                println("Error details: ${error.message}")
            } else {
                println("Unknown error")
            }
        }
    }

    private fun sendPaymentTokenToServer(paymentToken: String) {
        SendPaymentTask().execute(paymentToken)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SendPaymentTask : AsyncTask<String, Void, String>() {
        private lateinit var context: Context

        init {
            this.context = this@OrderActivity
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String {
            val paymentToken = params[0]
            val client = OkHttpClient()
            val formattedAmount = "%.2f".format(Locale.US, tokenAmount)
            Log.d("PaymentRequest", "Formatted amount with dot: $formattedAmount")
            val jsonObject = JSONObject().apply {
                put("amount", JSONObject().apply {
                    put("value", formattedAmount.toDouble())
                    put("currency", "RUB")
                })
                put("payment_token", paymentToken)
                put("confirmation", JSONObject().apply {
                    put("type", "redirect")
                    put("return_url", "https://dzen.ru/?yredirect=true")
                })
                put("capture", true)
                put("description", "Описание заказа")
            }
            Log.d("PaymentRequest", "Request body: ${jsonObject.toString()}")
            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                jsonObject.toString()
            )
            val shopId = "1062540"
            val secretKey = "test__pqwCfXbihuBzDCPxkTIsn84_3dokI3V7lOhkJm32BU"
            val credentials = "$shopId:$secretKey"
            val base64Credentials = android.util.Base64.encodeToString(credentials.toByteArray(), android.util.Base64.NO_WRAP)
            val request = Request.Builder()
                .url("https://api.yookassa.ru/v3/payments")
                .addHeader("Authorization", "Basic $base64Credentials")
                .addHeader("Idempotence-Key", java.util.UUID.randomUUID().toString())
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
            try {
                val response = client.newCall(request).execute()
                return response.body?.string() ?: "No response"
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error: ${e.message}"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("response", "Server response: $result")
            try {
                val jsonResponse = JSONObject(result ?: "{}")
                val status = jsonResponse.optString("status", "")
                if (status == "succeeded") {
                    Log.d("PaymentSuccess", "Payment succeeded, creating order...")
                    val userId = UserSession.userId
                    if (userId == null) {
                        Log.e("OrderError", "User ID is null")
                        Toast.makeText(this@OrderActivity, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val order = Orders(
                        id = 0,
                        userId = userId,
                        orderDate = "27 мая",
                        status = "Оплачен",
                        deliveryAddress = binding.tvAddress.text.toString().trim(),
                        totalAmount = finalTotal.toFloat()
                    )

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val createdOrder = RetrofitInstance.orderApi.createOrder(order)
                            val orderId = createdOrder.id

                            val basketItems = getBasketItems(this@OrderActivity)

                            for ((sneakerId, quantity) in basketItems) {
                                val orderItem = OrderItem(
                                    id = 0,
                                    orderId = orderId,
                                    sneakerId = sneakerId,
                                    quantity = quantity
                                )
                                try {
                                    RetrofitInstance.orderApi.createOrderItem(orderItem)
                                    val notification = Notification(
                                        id = 0,
                                        header = "Успешный заказ",
                                        body = "Товар #$sneakerId успешно добавлен в заказ #$orderId",
                                        date = LocalDateTime.now().toString(),
                                        userId = userId
                                    )
                                    createNotification(notification)
                                } catch (e: Exception) {
                                    Log.e("OrderError", "Failed to create order item for sneakerId=$sneakerId", e)
                                }
                            }

                            val sharedPreferences = getSharedPreferences("basket", Context.MODE_PRIVATE)
                            sharedPreferences.edit().clear().apply()

                            val inflater = layoutInflater
                            val customToastLayout = inflater.inflate(R.layout.layout_toast, findViewById(R.id.layoutToastContainer))

                            val customToast = Toast(applicationContext)
                            customToast.duration = Toast.LENGTH_SHORT
                            customToast.view = customToastLayout
                            customToast.show()
                            val intent = Intent(this@OrderActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Log.e("OrderError", "Failed to create order", e)
                            Toast.makeText(this@OrderActivity, "Не удалось создать заказ", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("PaymentError", "Payment failed: $result")
                    Toast.makeText(this@OrderActivity, "Ошибка при обработке платежа", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PaymentError", "Failed to parse server response", e)
                Toast.makeText(this@OrderActivity, "Не удалось обработать ответ сервера", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatDateForDisplay(date: LocalDate): String {
        val monthNames = listOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        )
        val day = date.dayOfMonth
        val month = monthNames[date.monthValue - 1]
        return "$day $month"
    }

    private fun createNotification(notification: Notification) {
        val notificationViewModel = NotificationViewModel()
        notificationViewModel.createNotification(notification)
    }

    fun getBasketItems(context: Context): List<Pair<Int, Int>> {
        val sharedPreferenceBasket = context.getSharedPreferences("basket", Context.MODE_PRIVATE)
        val basketJson = sharedPreferenceBasket.getString("cart", "{}")
        val basketMap = Gson().fromJson(basketJson, Map::class.java) as Map<String, Map<String, Any>>

        return basketMap.map { (sneakerId, data) ->
            val quantity = when (val q = data["quantity"]) {
                is Double -> q.toInt()
                is Int -> q
                else -> 1
            }
            Pair(sneakerId.toInt(), quantity)
        }
    }
}