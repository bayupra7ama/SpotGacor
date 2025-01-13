package com.bayupratama.spotgacor.ui.home.ui.map

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.data.response.LokasiItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bayupratama.spotgacor.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.LatLngBounds

// Activity ini untuk menampilkan peta dan marker lokasi
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapViewModel: MapViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Peta Lokasi"

        // Menyiapkan ViewModel yang digunakan untuk mengambil data lokasi
        mapViewModel = MapViewModel(this)

        // Mengamati perubahan pada daftar lokasi, dan menambahkan marker ke peta jika data tersedia
        mapViewModel.lokasiList.observe(this) { lokasiList ->
            if (lokasiList.isNotEmpty()) {
                addMarkersToMap(lokasiList)
            } else {
                showErrorAndFinish("Gagal memuat data, harap coba lagi: koneksi gagal")
            }
        }

        // Mengamati error dari ViewModel dan menampilkan pesan kesalahan
        mapViewModel.error.observe(this) { errorMessage ->
            showErrorAndFinish(errorMessage)
        }

        // Meminta data lokasi melalui ViewModel
        mapViewModel.fetchLokasiList()

        // Menyiapkan peta dengan mendapatkan SupportMapFragment dan memanggil getMapAsync
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Fungsi yang dijalankan ketika peta siap
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Menyiapkan pengaturan UI untuk peta (misalnya zoom control, kompas, dll)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Meminta lokasi pengguna jika diizinkan
        getMyLocation()
    }

    // Fungsi untuk menambahkan marker ke peta berdasarkan data lokasi
    private fun addMarkersToMap(lokasiList: List<LokasiItem>) {
        lokasiList.forEach { lokasi ->
            val lat = lokasi.lat.toString().toDoubleOrNull()
            val lng = lokasi.jsonMemberLong.toString().toDoubleOrNull()
            if (lat != null && lng != null) {
                val latLng = LatLng(lat, lng)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(lokasi.namaTempat)
                )
                boundsBuilder.include(latLng)  // Menambahkan marker ke dalam batas peta
            }
        }

        // Membuat LatLngBounds untuk mengatur batas peta agar mencakup semua marker
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    // Fungsi untuk menampilkan pesan kesalahan dan kembali ke tampilan sebelumnya
    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()  // Menampilkan pesan kesalahan
        finish()  // Kembali ke activity sebelumnya
    }

    // Deklarasi untuk meminta izin lokasi dari pengguna
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()  // Jika izin diberikan, aktifkan lokasi pengguna di peta
            }
        }

    // Fungsi untuk mendapatkan lokasi pengguna
    private fun getMyLocation() {
        // Mengecek apakah aplikasi memiliki izin untuk mengakses lokasi pengguna
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true  // Menampilkan lokasi pengguna di peta
        } else {
            // Jika izin belum diberikan, minta izin kepada pengguna
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Fungsi untuk menampilkan menu pilihan jenis peta
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_menu, menu)
        return true
    }

    // Fungsi untuk menangani pilihan menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL  // Peta jenis normal
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE  // Peta jenis satelit
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN  // Peta jenis medan
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID  // Peta jenis hibrida
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
