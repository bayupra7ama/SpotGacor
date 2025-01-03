package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bayupratama.spotgacor.data.response.ApiAddUlsanResponse
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.databinding.ActivityAddUlasanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class AddUlasanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUlasanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUlasanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lokasiId = intent.getIntExtra("lokasiId", 0) // Mendapatkan lokasi ID dari intent

        binding.btnSubmitUlasan.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt() // Mengambil nilai rating dari RatingBar
            val komentar =binding.etKomentar.text.toString()

            if (komentar.isBlank()) {
                Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rating < 1 || rating > 5) {
                Toast.makeText(this, "Rating harus antara 1 hingga 5", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitUlasan(lokasiId, rating, komentar)
        }
    }

    private fun submitUlasan(lokasiId: Int, rating: Int, komentar: String) {
        val apiService = ApiConfig.getApiService(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<ApiAddUlsanResponse> =
                    apiService.storeUlasan(lokasiId, rating, komentar)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    runOnUiThread {
                        Toast.makeText(
                            this@AddUlasanActivity,
                            responseBody?.meta?.message ?: "Ulasan berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AddUlasanActivity,
                            response.body()?.meta?.message ?: "anda sudah memberikan ulasan",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddUlasanActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
