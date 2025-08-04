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

    // Fungsi ini dipanggil saat tampilan fragment dibuat
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Fungsi ini dipanggil setelah tampilan fragment selesai dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menambahkan aksi saat tombol 'addStory' diklik untuk membuka AddStoryActivity
        binding.addStoryButton.setOnClickListener {
            val intent = Intent(requireContext(), AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Menyiapkan adapter untuk menampilkan data dengan Paging
        val adapter = StoryPagingAdapter()
        binding.recyclerViewStories.apply {
            layoutManager = LinearLayoutManager(context) // Menentukan layout untuk RecyclerView
            this.adapter = adapter // Menetapkan adapter ke RecyclerView
        }

        // Menyiapkan SwipeRefreshLayout untuk refresh data
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshStories(adapter) // Memanggil fungsi untuk refresh data
        }

        // Memulai pengamatan data cerita
        observeStories(adapter)
    }

    // Fungsi untuk merefresh data ketika swipe refresh dilakukan
    private fun refreshStories(adapter: StoryPagingAdapter) {
        binding.swipeRefreshLayout.isRefreshing = true // Menampilkan indikator refresh
        adapter.refresh() // Meminta data baru

        // Menambahkan delay untuk menghentikan indikator refresh jika tidak ada respons dalam 3 detik
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(3000) // Timeout 3 detik
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false // Menyembunyikan indikator refresh
            }
        }
    }

    // Fungsi untuk mengamati data cerita dari ViewModel
    private fun observeStories(adapter: StoryPagingAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            storyViewModel.stories.collectLatest { pagingData ->
                adapter.submitData(pagingData) // Mengirim data ke adapter
                binding.swipeRefreshLayout.isRefreshing = false // Menyembunyikan indikator refresh setelah data diterima
            }
        }
    }

    // Fungsi untuk membersihkan binding saat tampilan fragment dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
