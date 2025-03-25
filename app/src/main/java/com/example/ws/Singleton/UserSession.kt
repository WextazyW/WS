package com.example.ws.Singleton

object UserSession {
    var userId: Int? = null

    private var isLoggedIn: Boolean = false

    fun isLoggedIn(): Boolean {
        return isLoggedIn
    }

    fun setLoggedIn(loggedIn: Boolean) {
        isLoggedIn = loggedIn
    }
}