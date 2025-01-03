package com.bayupratama.spotgacor.ui.home.ui.story

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bayupratama.spotgacor.R
import com.bayupratama.spotgacor.databinding.ActivityAddStoryBinding
import com.bayupratama.spotgacor.helper.getImageUri
import com.bayupratama.spotgacor.helper.uriToFile
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private var currentImageUri: Uri? = null
    private lateinit var binding : ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_story)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadStory() }

        observeViewModel()

    }
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.responseMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) finish() // Tutup aktivitas jika berhasil
        }
    }

    private fun uploadStory() {
        val caption = binding.etCaption.text.toString()
        if (caption.isBlank()) {
            Toast.makeText(this, "Caption tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val isiStory = caption.toRequestBody("text/plain".toMediaTypeOrNull())
        var photoPart: MultipartBody.Part? = null

        // Jika ada foto yang dipilih, buatkan MultipartBody.Part, jika tidak biarkan null
        currentImageUri?.let { uri ->
            val file = uriToFile(uri, application)
            file?.let {
                lifecycleScope.launch {
                    try {
                        val compressedFile = compressImage(this@AddStoryActivity, it)
                        val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        photoPart = MultipartBody.Part.createFormData("photo", compressedFile.name, requestBody)

                        // Kirim data ke ViewModel
                        viewModel.uploadStory(isiStory, photoPart)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@AddStoryActivity, "Gagal mengompres gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } ?: run {
            // Jika tidak ada gambar yang dipilih, tetap kirim story dengan null photoPart
            viewModel.uploadStory(isiStory, photoPart)
        }
    }
    private suspend fun compressImage(context: Context, file: File): File {
        return Compressor.compress(context, file) {
            quality(80) // Atur kualitas sesuai kebutuhan (0-100)
            format(Bitmap.CompressFormat.JPEG) // Format gambar
            size(1_000_000) // Ukuran maksimum dalam bytes (1MB)
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

}