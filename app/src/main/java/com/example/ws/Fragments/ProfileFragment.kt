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

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadUserData()

        binding.Save.setOnClickListener {
            saveUserData()
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

    private fun saveUserData() {
        lifecycleScope.launch {
            try {
                val userId = UserSession.userId
                if (userId != 0) {
                    val updatedUser = Users(
                        id = userId,
                        name = binding.etName.text.toString(),
                        email = binding.etEmail.text.toString(),
                        address = binding.etAddress.text.toString(),
                    )

                    RetrofitInstance.authApi.updateUser(userId, updatedUser)

                    Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfileFragment", "User ID is not set")
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Failed to save user data", e)
                Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

