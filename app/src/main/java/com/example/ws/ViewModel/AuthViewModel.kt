package com.example.ws.ViewModel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Http.Service.AuthApiService
import com.example.ws.Http.Service.NotificationApiService
import com.example.ws.Model.Notification
import com.example.ws.Model.Users
import com.example.ws.Singleton.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val authApi: AuthApiService,
    private val notificationViewModel: NotificationApiService,
    context: Context
) : ViewModel() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val _registrationStatus = MutableLiveData<String>()
    val registrationStatus: LiveData<String> get() = _registrationStatus
    var userId : Int = UserSession.userId

    private val _loginStatus = MutableLiveData<String>()
    val loginStatus: LiveData<String> get() = _loginStatus

    fun registerUser(user: Users) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authApi.signUp(user)
                UserSession.userId = response.id
                withContext(Dispatchers.Main) {
                    _registrationStatus.value = "Успешная регистрация"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _registrationStatus.value = e.message ?: "Ошибка при регистрации"
                    Log.e("AuthViewModel", "Error during registration: ${e.message}")
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authApi.login(email, password)
                userId = response.id
                UserSession.userPassword = response.password
                withContext(Dispatchers.Main) {
                    _loginStatus.value = "Успешный вход"
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _loginStatus.value = e.message ?: "Ошибка при авторизации"
                }
            }
        }
    }
}