package com.example.ws.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ws.Model.Users
import com.example.ws.R
import com.example.ws.Singleton.UserSession
import com.example.ws.client
import com.example.ws.databinding.FragmentProfileBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        try {
            loadInformation(UserSession.userId!!)
        } catch (e:Exception){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }

        binding.etName.isFocusable = false
        binding.etName.isEnabled = false

        binding.etEmail.isFocusable = false
        binding.etEmail.isEnabled = false

        binding.Save.setOnClickListener {
            createProfile()
        }

        return binding.root
    }

    private fun loadInformation(id : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users : List<Users> = client.postgrest["Users"]
                    .select {
                        filter {
                            eq("id", id)
                        }
                    }.decodeAs()
                withContext(Dispatchers.Main){
                    if (users.isNotEmpty()){
                        binding.etName.setText(users[0].name)
                        binding.etEmail.setText(users[0].email)
                    } else{
                        binding.etName.setText("Пользователь не найден")
                    }
                }
            } catch (e : Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createProfile(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userData = Users(
                    name = binding.etName.text.toString(),
                    email = binding.etEmail.text.toString(),
                )
                client.postgrest["Users"]
                    .insert(userData)
                    .decodeAs<List<Users>>()
            } catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}









