package com.example.ws.Singleton

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_PASSWORD = "userPassword"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var userPassword: String
        get() = sharedPreferences.getString(KEY_USER_PASSWORD, "") ?: ""
        set(value) {
            sharedPreferences.edit().putString(KEY_USER_PASSWORD, value).apply()
        }

    var userId: Int
        get() = sharedPreferences.getInt(KEY_USER_ID, 0)
        set(value) {
            sharedPreferences.edit().putInt(KEY_USER_ID, value).apply()
        }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}