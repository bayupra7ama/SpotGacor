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
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class AddUlasanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUlasanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAddUlasanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lokasiId = intent.getIntExtra("lokasiId", 0) // Mendapatkan lokasi ID dari intent
        val isEdit = intent.getBooleanExtra("isEdit", false)
        val ulasanId = intent.getIntExtra("ulasanId", -1)
        if (isEdit) {
            val existingRating = intent.getIntExtra("rating", 0)
            val existingKomentar = intent.getStringExtra("komentar")

            binding.ratingBar.rating = existingRating.toFloat()
            binding.etKomentar.setText(existingKomentar)
            binding.btnSubmitUlasan.text = "Update Ulasan"
        }

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

            if (isEdit && ulasanId != -1) {
                updateUlasan(ulasanId, rating, komentar)
            } else {
                submitUlasan(lokasiId, rating, komentar)
            }
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

    private fun updateUlasan(ulasanId: Int, rating: Int, komentar: String) {
        val apiService = ApiConfig.getApiService(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val komentarBody = komentar.toRequestBody("text/plain".toMediaTypeOrNull())

                val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "PUT")
                val response = apiService.updateUlasan(
                    methodBody,
                    ulasanId,
                    ratingBody,
                    komentarBody,
                    null // Belum dukung edit foto, bisa kamu tambahkan nanti
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddUlasanActivity, "Ulasan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddUlasanActivity, "Gagal memperbarui ulasan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddUlasanActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
