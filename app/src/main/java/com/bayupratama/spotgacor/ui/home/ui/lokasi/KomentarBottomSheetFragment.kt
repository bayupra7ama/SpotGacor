package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.helper.LokasiViewModelFactory
import com.bayupratama.spotgacor.data.response.Ulasan
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.FragmentKomentarBottomSheetBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.ui.adapter.KomentarAdapter

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KomentarBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentKomentarBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var komentarAdapter: KomentarAdapter
    private val viewModel: DetailLokasiViewModel by viewModels {
        LokasiViewModelFactory(
            ApiConfig.getApiService(requireContext()),
            Sharedpreferencetoken(requireContext()).getToken()!!
        )
    }

    private var lokasiId: Int = 0 // Menyimpan ID Lokasi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKomentarBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mendapatkan ID Lokasi dari arguments
        lokasiId = arguments?.getInt("lokasiId") ?: 0
        viewModel.getLokasiDetail(lokasiId, "Bearer ${Sharedpreferencetoken(requireContext()).getToken()!!}")
        // Mengamati LiveData ulasanList dari ViewModel
        viewModel.ulasanList.observe(viewLifecycleOwner) { ulasanList ->
            // Setup RecyclerView dan Adapter
            komentarAdapter = KomentarAdapter(ulasanList)
            binding.recyclerViewKomentar.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewKomentar.adapter = komentarAdapter
            val ulasanCount = ulasanList?.count()?.toString() ?: "0" // Default ke "0" jika ulasanList null
            binding.ulsanCount.text = getString(R.string.ulasan,ulasanCount)
        }

        // Memuat data komentar berdasarkan ID lokasi
        viewModel.getLokasiDetail(lokasiId, "Bearer ${Sharedpreferencetoken(requireContext()).getToken()!!}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

