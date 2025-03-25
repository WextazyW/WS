package com.example.ws.Factoryes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ws.Http.AuthApiService
import com.example.ws.ViewModel.AuthViewModel

class SignUpViewModelFactory(private val authApi: AuthApiService, val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authApi, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}