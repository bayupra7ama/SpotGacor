package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class BagikanLokasiViewModel : ViewModel() {
    val uploadResult = MutableLiveData<String>()
    var lat: Double? = null
    var long: Double? = null

    // Fungsi upload data beserta kompresi gambar
    fun uploadData(
        namaTempat: String,
        alamat: String,
        perlengkapan: String,
        rute: String,
        umpan: String,
        jenisIkan: String?,
        medan:String,
        selectedImages: List<Uri>,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(context)

                // Proses data teks menjadi RequestBody dengan Content-Type: text/plain
                val namaTempatBody = namaTempat.toRequestBody("text/plain".toMediaTypeOrNull())
                val alamatBody = alamat.toRequestBody("text/plain".toMediaTypeOrNull())
                val perlengkapanBody = perlengkapan.toRequestBody("text/plain".toMediaTypeOrNull())
                val ruteBody = rute.toRequestBody("text/plain".toMediaTypeOrNull())
                val umpanBody = umpan.toRequestBody("text/plain".toMediaTypeOrNull())
                val medanBody = medan.toRequestBody("text/plain".toMediaTypeOrNull())

                val jenisIkanBody = jenisIkan?.toRequestBody("text/plain".toMediaTypeOrNull())
                val latBody = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val longBody = long?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                // Proses gambar menjadi MultipartBody.Part
                val imageParts = selectedImages.mapNotNull { uri ->
                    val file = uriToFile(uri, context)
                    file?.let {
                        val compressedFile = compressImage(context, it, 1024, 1024, 80)
                        val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images[]", compressedFile.name, requestFile)
                    }
                }

                // Kirim request ke API
                val response = apiService.addLokasi(
                    namaTempat = namaTempatBody, alamat = alamatBody, perlengkapan =  perlengkapanBody,
                    rute = ruteBody, umpan =  umpanBody, jenisIkan =  jenisIkanBody , medan = medanBody,
                   lat =  latBody, long =  longBody, images =  imageParts
                )

                if (response.isSuccessful) {
                    uploadResult.postValue("Upload Successful: ${response.body()?.message}")
                } else {
                    uploadResult.postValue("Upload Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UploadError", e.message.toString())
                uploadResult.postValue("Error: ${e.message}")
            }
        }
    }


    // Fungsi untuk mengonversi URI ke file
    private fun uriToFile(uri: Uri, context: Context): File? {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    // Fungsi kompresi gambar menggunakan Bitmap
    private fun compressImage(context: Context, file: File, maxWidth: Int, maxHeight: Int, quality: Int): File {
        val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)

        // Menghitung dimensi baru
        val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        var newWidth = maxWidth
        var newHeight = (maxWidth / ratio).toInt()

        if (newHeight > maxHeight) {
            newHeight = maxHeight
            newWidth = (maxHeight * ratio).toInt()
        }

        // Resize gambar
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false)

        // Simpan ke file sementara dengan kompresi
        val resizedFile = File(file.parent, "resized_${file.name}")
        val outputStream = FileOutputStream(resizedFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        return resizedFile
    }
}
