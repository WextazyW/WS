package com.example.ws.ViewModel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ws.Http.AuthApiService
import com.example.ws.Model.Users
import com.example.ws.Singleton.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val authApi: AuthApiService, context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val _registrationStatus = MutableLiveData<String>()
    val registrationStatus: LiveData<String> get() = _registrationStatus

    private val _loginStatus = MutableLiveData<String>()
    val loginStatus: LiveData<String> get() = _loginStatus

    fun registerUser(user: Users) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authApi.signUp(user)
                withContext(Dispatchers.Main) {
                    _registrationStatus.value = "Успешная регистрация"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _registrationStatus.value = e.message ?: "Ошибка при регистрации"
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authApi.login(email, password)
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

    fun logoutUser(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                withContext(Dispatchers.Main){
                    _loginStatus.value = "Выход из аккаунта"
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", false)
                    editor.apply()
                }
            } catch (e:Exception){
                withContext(Dispatchers.Main){
                    _loginStatus.value = e.message ?: "Ошибка при выходе"
                }
            }
        }
    }
}