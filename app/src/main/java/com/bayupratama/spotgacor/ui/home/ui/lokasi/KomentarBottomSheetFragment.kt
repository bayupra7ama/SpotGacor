package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.helper.LokasiViewModelFactory
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

        binding.btnAddUlasan.setOnClickListener{
            val intent = Intent(requireContext(), AddUlasanActivity::class.java)
            intent.putExtra("lokasiId", lokasiId) // Kirim lokasi ID
            startActivity(intent)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar2.visibility = View.VISIBLE
                binding.recyclerViewKomentar.visibility = View.GONE
            } else {
                binding.progressBar2.visibility = View.GONE
                binding.recyclerViewKomentar.visibility = View.VISIBLE
            }
        }

        // Mendapatkan ID Lokasi dari arguments
        lokasiId = arguments?.getInt("lokasiId") ?: 0
        viewModel.getLokasiDetail(lokasiId, "Bearer ${Sharedpreferencetoken(requireContext()).getToken()!!}")
        // Mengamati LiveData ulasanList dari ViewModel
        viewModel.ulasanList.observe(viewLifecycleOwner) { ulasanList ->
            if (::komentarAdapter.isInitialized) {
                // Update data pada adapter
                komentarAdapter.updateData(ulasanList)
            } else {
                val currentUserId = Sharedpreferencetoken(requireContext()).getId()

                // Inisialisasi adapter dan RecyclerView
                komentarAdapter = KomentarAdapter(ulasanList,currentUserId)
                binding.recyclerViewKomentar.layoutManager = LinearLayoutManager(context)
                binding.recyclerViewKomentar.adapter = komentarAdapter
            }
            val ulasanCount = ulasanList?.count()?.toString() ?: "0"
            binding.ulsanCount.text = getString(R.string.ulasan, ulasanCount)
        }


        // Memuat data komentar berdasarkan ID lokasi
        viewModel.getLokasiDetail(lokasiId, "Bearer ${Sharedpreferencetoken(requireContext()).getToken()!!}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        viewModel.getLokasiDetail(lokasiId, "Bearer ${Sharedpreferencetoken(requireContext()).getToken()!!}")
    }

}

