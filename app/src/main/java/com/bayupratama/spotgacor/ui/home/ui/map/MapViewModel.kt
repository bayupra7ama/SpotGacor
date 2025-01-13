package com.bayupratama.spotgacor.ui.home.ui.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayupratama.spotgacor.data.response.LokasiItem
import com.bayupratama.spotgacor.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class MapViewModel(private val context: Context) : ViewModel() {

    private val _lokasiList = MutableLiveData<List<LokasiItem>>()
    val lokasiList: LiveData<List<LokasiItem>> get() = _lokasiList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchLokasiList() {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(context)
                val response = apiService.getListLokasi() // Pastikan API Anda memiliki endpoint ini
                if (response.responseMeta?.code == 200) {
                    _lokasiList.postValue(response.responseData?.lokasiList?.filterNotNull())
                } else {
                    _lokasiList.postValue(emptyList())
                    _error.postValue("Gagal memuat data: Response tidak valid.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _lokasiList.postValue(emptyList())
                _error.postValue("Gagal memuat data: ${e.localizedMessage}")
            }
        }
    }
}
