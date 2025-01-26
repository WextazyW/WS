package com.example.ws.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import com.example.ws.R
import com.example.ws.databinding.FragmentProfileAllBinding

class ProfileAllFragment : Fragment() {

    private lateinit var binding: FragmentProfileAllBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileAllBinding.inflate(inflater, container, false)
        replaceFragment(ProfileFragment())
        binding.btnOpenSide.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()
            when(it.itemId){
                R.id.nav_profile -> replaceFragment(ProfileFragment())
                R.id.nav_favorite -> replaceFragment(FavoriteSneakersFragment())
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout2, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}