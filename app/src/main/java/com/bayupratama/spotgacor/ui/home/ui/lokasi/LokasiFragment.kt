package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
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
import com.bayupratama.spotgacor.ui.home.ui.map.MapsActivity
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

        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.isIconified = false
        binding.searchView.clearFocus() // Opsional, agar tidak langsung fokus saat membuka halaman

// Tambahan visual
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.hint = "Cari berdasarkan lokasi..."
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextSecondary))
        searchEditText.setBackgroundColor(Color.TRANSPARENT)

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
//        binding.iconAdd.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_lokasi_to_bagikanLokasiFragment)
//        }
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
//        binding.iconLocation.setOnClickListener{
//            val intent = Intent(requireContext(), MapsActivity::class.java)
//            startActivity(intent)
//        }

        val popupMenu = PopupMenu(requireContext(), binding.iconMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_lokasi, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_add_location -> {
                    findNavController().navigate(R.id.action_navigation_lokasi_to_bagikanLokasiFragment)
                    true
                }
                R.id.menu_open_map -> {
                    val intent = Intent(requireContext(), MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_filter_fish -> {
                    showJenisIkanDialog() // fungsi untuk memilih jenis ikan, bisa dialog atau bottom sheet
                    true
                }
                else -> false
            }
        }



        binding.iconMenu.setOnClickListener {
            popupMenu.show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lokasiAdapter
        }
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

    private fun showJenisIkanDialog() {
        val jenisIkanList = arrayOf("Semua", "lele", "Gurame", "Nila", "Puyu","Gabus", "Toman")

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Jenis Ikan")
            .setItems(jenisIkanList) { _, which ->
                val selectedJenis = if (which == 0) null else jenisIkanList[which]
                viewModel.updateJenisIkan(selectedJenis)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Hindari akses binding setelah fragment dihancurkan
    }
}




