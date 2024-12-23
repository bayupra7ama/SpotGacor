package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayupratama.spotgacor.data.response.ApiResponseListLokasi
import com.bayupratama.spotgacor.data.retrofit.ApiConfig

import com.bayupratama.spotgacor.databinding.FragmentLokasiBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken


import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.helper.LokasiViewModelFactory
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
    ): View? {
        _binding = FragmentLokasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        lifecycleScope.launch {
            viewModel.lokasiPagingData.collectLatest { pagingData ->
                lokasiAdapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            lokasiAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.visibility = if (loadStates.refresh is LoadState.Loading) View.VISIBLE else View.GONE
                binding.swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading

                if (loadStates.refresh is LoadState.Error) {
                    val error = (loadStates.refresh as LoadState.Error).error
                    Log.e("LokasiFragment", "LoadState Error: ${error.localizedMessage}")
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




