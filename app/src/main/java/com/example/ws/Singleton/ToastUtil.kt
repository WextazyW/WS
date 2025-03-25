package com.example.ws.Singleton

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.ws.R

object ToastUtil {
    fun showSuccessToast(context: Context) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.layout_toast, null)
        val toast = Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            view = toastView
        }
        toast.show()
    }

    fun showFailedToast(context: Context) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.layout_failed_toast, null)
        val toast = Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            view = toastView
        }
        toast.show()
    }

    fun showIsValidEmailToast(context: Context) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.layout_toast_isvalid, null)
        val toast = Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            view = toastView
        }
        toast.show()
    }

    fun showIsNotEmptyToast(context: Context) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.layout_toast_isnotempty, null)
        val toast = Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            view = toastView
        }
        toast.show()
    }
}