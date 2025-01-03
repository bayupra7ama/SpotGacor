package com.bayupratama.spotgacor.ui.home.ui.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun uploadStory(isiStory: RequestBody, photo: MultipartBody.Part?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(getApplication())
                val response = apiService.addStory(isiStory, photo)  // API yang sudah menerima photo nullable
                if (response.isSuccessful && response.body()?.meta?.status == "success") {
                    _isSuccess.value = true
                    _responseMessage.value = "Story berhasil diunggah"
                } else {
                    _isSuccess.value = false
                    _responseMessage.value = response.body()?.meta?.message ?: "Gagal mengunggah story"
                }
            } catch (e: Exception) {
                _isSuccess.value = false
                _responseMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
