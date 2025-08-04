package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.databinding.FragmentBagikanLokasiBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class BagikanLokasiFragment : Fragment() {
    private var _binding: FragmentBagikanLokasiBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BagikanLokasiViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedImages: MutableList<Uri> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBagikanLokasiBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel = ViewModelProvider(this)[BagikanLokasiViewModel::class.java]
        binding.placeHolderImg.setOnClickListener {
            showImageSourceDialog() // buka pilihan galeri/kamera
        }


        binding.backImg.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke halaman sebelumnya
        }

        binding.switchUseLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fetchUserLocation { success ->
                    if (!success) {
                        Toast.makeText(requireContext(), "Gagal Mendapatkan Lokasi. Silahkan Coba Lagi.", Toast.LENGTH_SHORT).show()
                        binding.switchUseLocation.isChecked = false
                    }
                }
            } else {
                viewModel.lat = null
                viewModel.long = null
            }
        }

        if (viewModel.lat == null && viewModel.long == null ) {
            binding.switchUseLocation.isChecked = false
        }
        binding.btnUpload.setOnClickListener {
            uploadData()
        }


        observeViewModel()
    }
    private fun observeViewModel() {
        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.btnUpload.isEnabled = true
            Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
            if (result.contains("Upload Successful")) {
                // Tutup halaman setelah berhasil
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun fetchUserLocation(callback: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.lat = location.latitude
                    viewModel.long = location.longitude
                    callback(true)
                } else {
                    callback(false)
                }
            }.addOnFailureListener {
                callback(false)
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            callback(false)
        }
    }


    private fun pickMultipleImages() {
        pickMultipleMedia.launch("image/*")
    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            selectedImages.clear()
            selectedImages.addAll(uris)
            updateSelectedImagesView()
        }

    private fun updateSelectedImagesView() {
        // Menghapus gambar lama
        binding.selectedImagesLayout.removeAllViews()

        if (selectedImages.isEmpty()) {
            binding.placeHolderImg.visibility = View.VISIBLE
        } else {
            binding.placeHolderImg.visibility = View.GONE
        }
        // Menambahkan gambar baru
        selectedImages.forEach { uri ->
            val imageView = ImageView(requireContext())
            imageView.setImageURI(uri)
            imageView.layoutParams = ViewGroup.LayoutParams(600, 400) // Ganti ukuran sesuai kebutuhan
            imageView.setOnClickListener {
                // Mengganti gambar jika di klik
                pickMultipleImages()
            }

            // Menambahkan tombol hapus untuk setiap gambar
            val deleteButton = ImageView(requireContext())
            deleteButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            deleteButton.layoutParams = ViewGroup.LayoutParams(100, 50)
            deleteButton.setOnClickListener {
                // Menghapus gambar yang dipilih
                selectedImages.remove(uri)
                updateSelectedImagesView()
            }

            // Menambahkan gambar dan tombol hapus ke dalam layout
            binding.selectedImagesLayout.addView(imageView)
            binding.selectedImagesLayout.addView(deleteButton)
        }
    }


    private fun uploadData() {
        val namaTempat = binding.editTextNamaTempat.text.toString()
        val alamat = binding.alamatEditText.text.toString()
        val perlengkapan = binding.saranPerlengkapanEditText.text.toString()
        val rute = binding.ruteEditText.text.toString()
        val umpan = binding.jenisUmpanEditText.text.toString()
        val jenisIkan = binding.jenisIkanEditText.text.toString()
        val medan = binding.medanEditText.text.toString()

        if (namaTempat.isEmpty() || alamat.isEmpty() || viewModel.lat == null || viewModel.long == null) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpload.isEnabled = false
        viewModel.uploadData(
            namaTempat, alamat, perlengkapan, rute, umpan, jenisIkan, medan,selectedImages, requireContext()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE

        _binding = null
    }

    private var tempImageUri: Uri? = null

    private val launcherCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempImageUri != null) {
            selectedImages.add(tempImageUri!!)
            updateSelectedImagesView()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Ambil dari Kamera", "Pilih dari Galeri")

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> pickMultipleImages()
                }
            }
            .show()
    }

    private fun openCamera() {
        val imageFile = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        tempImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )
        launcherCamera.launch(tempImageUri)
    }




    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
