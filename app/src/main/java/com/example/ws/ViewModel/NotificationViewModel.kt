package com.example.ws.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Model.Notification
import com.example.ws.Singleton.UserSession
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val _notification = MutableLiveData<List<Notification>>()
    val notification: LiveData<List<Notification>> get() = _notification

    private val _createNotificationStatus = MutableLiveData<String>()
    val createNotificationStatus: LiveData<String> get() = _createNotificationStatus

    fun loadNotification() {
        viewModelScope.launch {
            try {
                val userId = UserSession.userId
                if (userId != null) {
                    val response = RetrofitInstance.notificationApi.getNotificationsByUserId(userId)
                    _notification.value = response
                } else {
                    _notification.value = emptyList()
                    Log.d("NotificationViewModel", "User ID is null. User is not logged in.")
                }
            } catch (e: Exception) {
                _notification.value = emptyList()
                Log.e("NotificationViewModel", "Error loading notifications: ${e.message}")
            }
        }
    }

    fun createNotification(notification: Notification) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.notificationApi.createNotification(notification)
                _createNotificationStatus.value = "Уведомление успешно создано"
                Log.d("NotificationViewModel", "Notification creation response: $response")
            } catch (e: Exception) {
                _createNotificationStatus.value = e.message ?: "Ошибка при создании уведомления"
                Log.e("NotificationViewModel", "Error creating notification: ${e.message}")
            }
        }
    }
}