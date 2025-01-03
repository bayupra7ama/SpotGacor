package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayupratama.spotgacor.data.retrofit.ApiConfig

import com.bayupratama.spotgacor.databinding.FragmentLokasiBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken


import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.helper.LokasiViewModelFactory
import com.bayupratama.spotgacor.ui.adapter.LokasiPagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LokasiFragment : Fragment() {

    private var _binding: FragmentLokasiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LokasiViewModel by viewModels {
        LokasiViewModelFactory(ApiConfig.getApiService(requireContext()), Sharedpreferencetoken(requireContext()).getToken()!!)
    }
    private lateinit var lokasiAdapter: LokasiPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentLokasiBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.updateSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.updateSearchQuery(newText)
                return true
            }
        })

        binding.iconAdd.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_lokasi_to_bagikanLokasiFragment)
        }

        lokasiAdapter = LokasiPagingAdapter { lokasiItem ->
            val bundle = Bundle().apply {
                putInt("lokasiId", lokasiItem.id ?: 0)
                putString("lokasiNama", lokasiItem.namaTempat)
            }
            // Gunakan ID navigasi yang sesuai untuk menuju ke DetailLokasiFragment
            findNavController().navigate(
                R.id.action_navigation_lokasi_to_detailLokasiFragment,
                bundle
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lokasiAdapter
        }

        // Handle swipe-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            lokasiAdapter.refresh()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lokasiPagingData.collectLatest { pagingData ->
                    lokasiAdapter.submitData(pagingData)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                lokasiAdapter.loadStateFlow.collectLatest { loadStates ->
                    binding.progressBar.visibility =
                        if (loadStates.refresh is LoadState.Loading) View.VISIBLE else View.GONE
                    binding.swipeRefreshLayout.isRefreshing =
                        loadStates.refresh is LoadState.Loading

                }
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Hindari akses binding setelah fragment dihancurkan
    }



}




