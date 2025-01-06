package com.bayupratama.spotgacor.ui.home.ui.story

import StoryPagingAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayupratama.spotgacor.databinding.FragmentStoryBinding
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.helper.StoryViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(ApiConfig.getApiService(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addStory.setOnClickListener {
            val intent = Intent(requireContext(), AddStoryActivity::class.java)
            startActivity(intent)
        }

        val adapter = StoryPagingAdapter()
        binding.recyclerViewStories.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshStories(adapter)
        }





        // Load initial data
        observeStories(adapter)
    }

    private fun refreshStories(adapter: StoryPagingAdapter) {
        binding.swipeRefreshLayout.isRefreshing = true // Show refresh indicator
        adapter.refresh() // Request fresh data

        // Tambahkan timeout untuk menghentikan indikator jika tidak ada respons
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(3000) // Timeout 3 detik
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false // Force hide
            }
        }
    }

    private fun observeStories(adapter: StoryPagingAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            storyViewModel.stories.collectLatest { pagingData ->
                adapter.submitData(pagingData)
                binding.swipeRefreshLayout.isRefreshing = false // Hide refresh indicator
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
