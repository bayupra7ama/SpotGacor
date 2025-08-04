package com.bayupratama.spotgacor.ui.home.ui.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bayupratama.spotgacor.data.response.GenericResponse
import com.bayupratama.spotgacor.data.response.PhotoResponse
import com.bayupratama.spotgacor.data.response.UserData
import com.bayupratama.spotgacor.data.response.UserResponse
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import com.bayupratama.spotgacor.helper.compressImage
import com.bayupratama.spotgacor.helper.uriToFile
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ApiConfig.getApiService(application)

    val userData = MutableLiveData<UserData>()
    val isLoading = MutableLiveData<Boolean>()

    private val _newProfilePhotoUrl = MutableLiveData<String?>()
    val newProfilePhotoUrl: LiveData<String?> = _newProfilePhotoUrl

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    fun getUserById(id: Int) {
        isLoading.value = true
        apiService.getUserById(id).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    userData.value = response.body()?.data
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                isLoading.value = false
                Log.e("ProfileViewModel", "Error: ${t.message}")
            }
        })
    }

    fun uploadProfilePhoto(uri: Uri) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Ubah Uri jadi MultipartBody.Part
                val file = File(getApplication<Application>().cacheDir, "temp_photo.jpg")
                val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
                inputStream?.use {
                    file.outputStream().use { output -> it.copyTo(output) }
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("profile_photo", file.name, requestFile)

                val response = apiService.updateProfilePhoto(body)

                if (response.isSuccessful && response.body() != null) {
                    _newProfilePhotoUrl.value = response.body()!!.data?.profilePhotoUrl
                    _responseMessage.value = "Foto profil berhasil diperbarui"
                } else {
                    _responseMessage.value = "Gagal memperbarui foto profil"
                }
            } catch (e: Exception) {
                _responseMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        isLoading.value = true

        apiService.updatePassword(currentPassword, newPassword, confirmPassword)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful && response.body()?.meta?.code == 200) {
                        onResult(true, response.body()?.meta?.message.toString() ?: "Password berhasil diubah")
                    } else {
                        val errorMessage = response.body()?.meta?.message.toString() ?: "Gagal memperbarui password"
                        onResult(false, errorMessage)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    isLoading.value = false
                    onResult(false, "Terjadi kesalahan: ${t.message}")
                }
            })
    }

}
