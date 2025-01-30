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

        loadInformation(UserSession?.userId!!)

        binding.Save.setOnClickListener {
            editProfile()
        }

        return binding.root
    }

    private fun loadInformation(id : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val users : List<Users> = client.postgrest["Users"]
                .select {
                    filter {
                        eq("id", id)
                    }
                }.decodeAs()
            withContext(Dispatchers.Main){
                if (users.isNotEmpty()){
                    binding.etName.setText(users[0].Name)
                    binding.etEmail.setText(users[0].Email)
                } else{
                    binding.etName.setText("Пользователь не найден")
                }
            }
        }
    }

    private fun editProfile(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userData = Users(
                    Name = binding.etName.text.toString(),
                    Email = binding.etEmail.text.toString(),
                    id = UserSession.userId,
                    id_uuid = "f2cd6343-e0d7-40b9-892c-929108145adc"
                )
                client.postgrest["Users"]
                    .update(userData){
                        filter {
                            eq("id", userData.id.toString())
                        }
                    }
            } catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}









