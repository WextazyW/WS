package com.example.ws.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.ws.Http.RetrofitInstance
import com.example.ws.Model.Users
import com.example.ws.Singleton.UserSession
import com.example.ws.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadUserData()

        binding.Save.setOnClickListener {
            if (validateInput()) {
                saveUserData()
            }
        }

        return binding.root
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val userId = UserSession.userId
                if (userId != 0) {
                    val user = RetrofitInstance.authApi.getUserById(userId)
                    binding.etName.setText(user.name)
                    binding.etEmail.setText(user.email)
                    binding.etAddress.setText(user.address)
                } else {
                    Log.e("ProfileFragment", "User ID is not set")
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Failed to load user data", e)
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = "Введите имя"
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Введите email"
            return false
        }
        if (!isValidEmail(email)) {
            binding.etEmail.error = "Некорректный email"
            return false
        }

        if (address.isEmpty()) {
            binding.etAddress.error = "Введите адрес"
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return emailPattern.matcher(email).matches()
    }

    private fun saveUserData() {
        lifecycleScope.launch {
            try {
                val userId = UserSession.userId
                val userPassword = UserSession.userPassword
                if (userId != 0) {
                    val updatedUser = Users(
                        id = userId,
                        name = binding.etName.text.toString().trim(),
                        email = binding.etEmail.text.toString().trim(),
                        password = userPassword,
                        address = binding.etAddress.text.toString().trim()
                    )

                    Log.d("ProfileFragment", "Sending user data: $updatedUser")

                    RetrofitInstance.authApi.updateUser(userId, updatedUser)

                    Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfileFragment", "User ID is not set")
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Failed to save user data", e)

                if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("ProfileFragment", "Server response: $errorBody")
                    Toast.makeText(requireContext(), "Ошибка: $errorBody", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}