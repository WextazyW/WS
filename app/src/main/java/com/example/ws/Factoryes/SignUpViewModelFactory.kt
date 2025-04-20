package com.example.ws.Factoryes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ws.Http.Service.AuthApiService
import com.example.ws.Http.Service.NotificationApiService
import com.example.ws.ViewModel.AuthViewModel

class SignUpViewModelFactory(private val authApi: AuthApiService, private val notificationApi: NotificationApiService,
                             val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authApi, notificationApi, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}