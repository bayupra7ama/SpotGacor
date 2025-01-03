package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bayupratama.spotgacor.databinding.FragmentBagikanLokasiBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

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
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel = ViewModelProvider(this).get(BagikanLokasiViewModel::class.java)

        binding.placeHolderImg.setOnClickListener {
            pickMultipleImages()
        }

        binding.switchUseLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fetchUserLocation()
            } else {
                viewModel.lat = null
                viewModel.long = null
            }
        }


        binding.btnUpload.setOnClickListener {
            uploadData()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.lat = it.latitude
                    viewModel.long = it.longitude
                } ?: Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun pickMultipleImages() {
        pickMultipleMedia.launch("image/*")
    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            selectedImages.clear()
            selectedImages.addAll(uris)
        }

    private fun uploadData() {
        val namaTempat = binding.namaTempatEditText.text.toString()
        val alamat = binding.alamatEditText.text.toString()
        val perlengkapan = binding.saranPerlengkapanEditText.text.toString()
        val rute = binding.ruteEditText.text.toString()
        val umpan = binding.jenisUmpanEditText.text.toString()
        val jenisIkan = binding.jenisIkanEditText.text.toString()

        if (namaTempat.isEmpty() || alamat.isEmpty() || viewModel.lat == null || viewModel.long == null) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.uploadData(
            namaTempat, alamat, perlengkapan, rute, umpan, jenisIkan, selectedImages, requireContext()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
