package com.example.ws.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Model.Sneakers
import kotlinx.coroutines.launch

class SneakerViewModel : ViewModel() {
    private val _sneakers = MutableLiveData<List<Sneakers>>()
    val sneakers: LiveData<List<Sneakers>> get() = _sneakers

    fun loadSneakers() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getSneakers()
                _sneakers.value = response
            } catch (e: Exception) {
                Log.d("error", e.message.toString())
            }
        }
    }

    fun getSneakersInBasket(context: Context): List<Sneakers> {
        val sharedPreferencesBasket = context.getSharedPreferences("basket", Context.MODE_PRIVATE)
        return _sneakers.value?.filter { sneaker ->
            sharedPreferencesBasket.getBoolean(sneaker.id.toString(), false)
        } ?: emptyList()
    }
}