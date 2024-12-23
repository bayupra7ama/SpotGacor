package com.bayupratama.spotgacor.data.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bayupratama.spotgacor.data.retrofit.ApiService
import com.bayupratama.spotgacor.ui.home.ui.lokasi.DetailLokasiViewModel
import com.bayupratama.spotgacor.ui.home.ui.lokasi.LokasiViewModel

class LokasiViewModelFactory(
    private val apiService: ApiService,
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LokasiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LokasiViewModel(apiService, token) as T
        }
        if (modelClass.isAssignableFrom(DetailLokasiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailLokasiViewModel(apiService, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
