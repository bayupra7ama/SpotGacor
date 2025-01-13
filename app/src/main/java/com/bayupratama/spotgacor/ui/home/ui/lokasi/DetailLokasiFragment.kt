package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.helper.LokasiViewModelFactory
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.FragmentDetailLokasiBinding
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import com.bayupratama.spotgacor.ui.adapter.ImageSliderAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailLokasiFragment : Fragment() {
    private var _binding: FragmentDetailLokasiBinding? = null
    private val binding get() = _binding!! // Mengakses binding, yang menghubungkan tampilan XML dengan kode

    private lateinit var sharedpreferencetoken: Sharedpreferencetoken // Menyimpan token untuk otentikasi
    private val viewModel: DetailLokasiViewModel by viewModels { // Menggunakan ViewModel untuk mengelola data
        LokasiViewModelFactory(
            ApiConfig.getApiService(requireContext()), // API untuk mendapatkan data lokasi
            Sharedpreferencetoken(requireContext()).getToken()!! // Mengambil token dari SharedPreferences
        )
    }

    // Fungsi ini dipanggil saat tampilan fragment dibuat
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailLokasiBinding.inflate(inflater, container, false) // Menghubungkan layout XML
        return binding.root // Mengembalikan tampilan fragment
    }

    // Fungsi ini dipanggil setelah tampilan fragment selesai dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menyembunyikan BottomNavigationView saat berada di halaman detail lokasi
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE

        // Mendapatkan ID dan nama lokasi dari argumen fragment
        val lokasiId = arguments?.getInt("lokasiId") ?: 0
        val lokasiNama = arguments?.getString("lokasiNama")
        Log.d("DetailLokasiFragment", "ID: $lokasiId, Nama: $lokasiNama")

        // Mengambil token dari SharedPreferences untuk otentikasi API
        sharedpreferencetoken = Sharedpreferencetoken(requireContext())
        val token = "Bearer " + sharedpreferencetoken.getToken().toString()

        // Mengatur aksi tombol kembali untuk kembali ke halaman sebelumnya
        binding.backImg.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke halaman sebelumnya
        }

        // Memanggil ViewModel untuk mendapatkan detail lokasi berdasarkan ID dan token
        viewModel.getLokasiDetail(lokasiId, token)

        // Mengamati perubahan data lokasi dari ViewModel
        viewModel.lokasiDetail.observe(viewLifecycleOwner) { response ->
            val lokasi = response.data.lokasi
            val imageUrl = lokasi.photo.toString() // Mengambil URL foto dari lokasi
            val imageList = parseJsonToImageList(imageUrl) // Mengonversi URL foto ke dalam daftar gambar

            // Jika ada gambar, tampilkan gambar menggunakan ImageSliderAdapter
            if (imageList.isNotEmpty()) {
                val adapter = ImageSliderAdapter(imageList)
                binding.viewPager.adapter = adapter
            } else {
                Log.d("DetailLokasiFragment", "No images available.")
            }

            // Menampilkan data lokasi pada tampilan
            binding.namaTempat.text = if (lokasi.nama_tempat.length > 17) {
                lokasi.nama_tempat.substring(0, 15) + "..." // Membatasi panjang nama lokasi
            } else {
                lokasi.nama_tempat
            }
            binding.createdBy.text = getString(R.string.shared_by, lokasi.created_by)
            binding.alamat.text = lokasi.alamat
            binding.rute.text = getString(R.string.route, lokasi.rute)
            binding.jenisIkan.text = getString(R.string.fish_type, lokasi.jenis_ikan)
            binding.saranPerlengkapan.text = getString(R.string.equipment_suggestion, lokasi.perlengkapan)
            binding.umpan.text = getString(R.string.bait, lokasi.umpan)

            // Menampilkan rating dan jumlah ulasan
            binding.ratingBar.rating = response.data.average_rating.toFloat()
            val rating = response.data.average_rating.toFloat()
            binding.ratingCount.text = getString(R.string.rating, rating)
            val ulasan = response.data.ulasan.count()
            binding.commentCount.text = ulasan.toString()
        }

        // Menangani aksi tombol untuk membuka lokasi di Google Maps
        binding.btnGotoGoolemap.setOnClickListener {
            val lokasi = viewModel.lokasiDetail.value?.data?.lokasi
            if (lokasi != null) {
                val latitude = lokasi.lat
                val longitude = lokasi.long
                val uri = "google.navigation:q=$latitude,$longitude" // Menyusun URI untuk membuka Google Maps
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent) // Membuka Google Maps
                } else {
                    // Menampilkan pesan jika Google Maps tidak tersedia
                    Toast.makeText(
                        requireContext(),
                        "Google Maps tidak tersedia di perangkat ini",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        // Mengamati perubahan daftar ulasan dari ViewModel
        viewModel.ulasanList.observe(viewLifecycleOwner) { _ ->
            binding.comentIcon.setOnClickListener {
                // Menampilkan bottom sheet untuk melihat atau menambah ulasan
                val komentarBottomSheet = KomentarBottomSheetFragment()
                val bundle = Bundle()
                bundle.putInt("lokasiId", lokasiId)
                komentarBottomSheet.arguments = bundle
                komentarBottomSheet.show(parentFragmentManager, komentarBottomSheet.tag)
            }
        }
    }

    // Fungsi untuk mengonversi string JSON ke daftar gambar
    private fun parseJsonToImageList(jsonString: String): List<String> {
        val gson = com.google.gson.Gson()
        return gson.fromJson(jsonString, Array<String>::class.java).toList() // Mengonversi JSON menjadi list gambar
    }

    // Fungsi untuk membersihkan binding saat tampilan fragment dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE // Menampilkan kembali BottomNavigationView
        _binding = null // Menghapus binding agar tidak menyebabkan memory leak
    }

    // Memanggil ulang detail lokasi ketika fragment kembali terlihat
    override fun onResume() {
        super.onResume()
        val lokasiId = arguments?.getInt("lokasiId") ?: 0
        viewModel.getLokasiDetail(lokasiId = lokasiId, "Bearer ${Sharedpreferencetoken(requireContext()).getToken()!!}")
    }
}
