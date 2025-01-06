package com.bayupratama.spotgacor.ui.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.databinding.FragmentHomeBinding
import com.bayupratama.spotgacor.ui.home.ui.map.MapsActivity


import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLokasi.setOnClickListener {
            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNavView.selectedItemId = R.id.navigation_lokasi
        }
        binding.storyButton.setOnClickListener {
            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNavView.selectedItemId = R.id.navigation_story
        }
        binding.mapBtn.setOnClickListener {
            startActivity(Intent(requireContext(), MapsActivity::class.java))
        }
        binding.btnProfile.setOnClickListener {

            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNavView.selectedItemId = R.id.navigation_profile
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}