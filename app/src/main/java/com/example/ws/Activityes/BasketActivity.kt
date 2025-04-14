package com.example.ws.Activityes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ws.Adapters.BasketAdapter
import com.example.ws.Model.Sneakers
import com.example.ws.R
import com.example.ws.ViewModel.SneakerViewModel
import com.example.ws.client
import com.example.ws.databinding.ActivityBasketBinding
import com.example.ws.databinding.DialogCustomBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizeIntent
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import java.io.IOException
import java.math.BigDecimal
import java.util.Currency

class BasketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasketBinding
    private var listSneakers = mutableListOf<Sneakers>()
    private lateinit var adapter: BasketAdapter
    private val viewModel: SneakerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = BasketAdapter(listSneakers, ::updateTotalPrice, this)
        binding.rvBasket.layoutManager = LinearLayoutManager(this)
        binding.rvBasket.adapter = adapter

        adapter.setupSwipeToDelete(binding.rvBasket)

        binding.btnCreateOrder.setOnClickListener {
            startActivity(Intent(this@BasketActivity, OrderActivity::class.java))
//            startPayment()
        }

        viewModel.sneakers.observe(this) { allSneakers ->
            val basketSneakers = viewModel.getSneakersInBasket(this)
            updateBasket(basketSneakers)
        }

        viewModel.loadSneakers()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateTotalPrice(totalPrice: Double) {
        binding.tvSumPrice.text = String.format("%.2f", totalPrice)
        val deliveryCost = 34.32
        binding.tvDostavka.text = deliveryCost.toString()
        val finalTotal = totalPrice + deliveryCost
        binding.tvItogo.text = String.format("%.2f", finalTotal)
    }

    private fun updateBasket(newList: List<Sneakers>) {
        listSneakers.clear()
        listSneakers.addAll(newList)
        adapter.notifyDataSetChanged()
        updateTotalPrice(adapter.getAllPrice())
        binding.tvCount.text = "${listSneakers.size} товара"
    }

    private fun startPayment() {
        val paymentParameters = PaymentParameters(
            amount = Amount(BigDecimal.valueOf(binding.tvItogo.text.toString().toDouble()), Currency.getInstance("RUB")),
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

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String {
            val paymentToken = params[0]
            val client = OkHttpClient()

            val jsonBody = """
            {
                "amount": {
                    "value": "${binding.tvItogo.text.toString().toDouble()}",
                    "currency": "RUB"
                },
                "payment_token": "$paymentToken",
                "confirmation": {
                    "type": "redirect",
                    "return_url": "https://dzen.ru/?yredirect=true"
                },
                "capture": true,
                "description": "Описание заказа"
            }
        """.trimIndent()

            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                jsonBody
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
            Log.d("response","Server response: $result")
        }
    }
}