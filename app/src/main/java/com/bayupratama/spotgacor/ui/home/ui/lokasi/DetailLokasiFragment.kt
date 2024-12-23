package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.helper.LokasiViewModelFactory
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.FragmentDetailLokasiBinding
import com.bayupratama.spotgacor.databinding.FragmentLokasiBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.ui.adapter.ImageSliderAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailLokasiFragment : Fragment() {
    private var _binding: FragmentDetailLokasiBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedpreferencetoken: Sharedpreferencetoken

    private val viewModel: DetailLokasiViewModel by viewModels {
        LokasiViewModelFactory(
            ApiConfig.getApiService(requireContext()),
            Sharedpreferencetoken(requireContext()).getToken()!!
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailLokasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE

        val lokasiId = arguments?.getInt("lokasiId") ?: 0
        val lokasiNama = arguments?.getString("lokasiNama")
        Log.d("DetailLokasiFragment", "ID: $lokasiId, Nama: $lokasiNama")

        sharedpreferencetoken = Sharedpreferencetoken(requireContext())
        val token = "Bearer " + sharedpreferencetoken.getToken().toString()

        viewModel.getLokasiDetail(lokasiId, token)
        viewModel.lokasiDetail.observe(viewLifecycleOwner) { response ->
            val lokasi = response.data.lokasi
            val imageUrl = lokasi.photo.toString()
            val imageList = parseJsonToImageList(imageUrl)

            if (imageList.isNotEmpty()) {
                val adapter = ImageSliderAdapter(imageList)
                binding.viewPager.adapter = adapter
            } else {
                Log.d("DetailLokasiFragment", "No images available.")
            }
            binding.namaTempat.text = if (lokasi.nama_tempat.length > 17) {
                lokasi.nama_tempat.substring(0, 15) + ""
            } else {
                lokasi.nama_tempat
            }
            binding.createdBy.text = getString(R.string.shared_by, lokasi.created_by)
            binding.alamat.text = lokasi.alamat
            binding.rute.text = getString(R.string.route, lokasi.rute)
            binding.jenisIkan.text = getString(R.string.fish_type, lokasi.jenis_ikan)
            binding.saranPerlengkapan.text = getString(R.string.equipment_suggestion, lokasi.perlengkapan)
            binding.umpan.text = getString(R.string.bait, lokasi.umpan)
            binding.ratingBar.rating = response.data.average_rating.toFloat()

            val rating = response.data.average_rating.toFloat()
            binding.ratingCount.text = getString(R.string.rating, rating)

            val ulasan = response.data.ulasan.count()
            binding.commentCount.text = ulasan.toString()



        }
        viewModel.ulasanList.observe(viewLifecycleOwner) { ulasanList ->
            binding.comentIcon.setOnClickListener {
                // Membuat instance KomentarBottomSheetFragment dan mengirimkan ID lokasi
                val komentarBottomSheet = KomentarBottomSheetFragment()
                val bundle = Bundle()
                bundle.putInt("lokasiId", lokasiId)  // Mengirimkan ID lokasi
                komentarBottomSheet.arguments = bundle

                // Menampilkan BottomSheet
                komentarBottomSheet.show(parentFragmentManager, komentarBottomSheet.tag)
            }
        }
    }

    private fun parseJsonToImageList(jsonString: String): List<String> {
        // Menggunakan Gson untuk mengonversi JSON string menjadi List<String>
        val gson = com.google.gson.Gson()
        return gson.fromJson(jsonString, Array<String>::class.java).toList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
        _binding = null
    }
}
