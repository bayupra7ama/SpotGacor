package com.bayupratama.spotgacor.ui.home.ui.lokasi

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayupratama.spotgacor.data.response.ApiResponseLokasiDetail
import com.bayupratama.spotgacor.data.response.Ulasan
import com.bayupratama.spotgacor.data.retrofit.ApiService
import kotlinx.coroutines.launch

class DetailLokasiViewModel(private val apiService: ApiService, private  val token: String) : ViewModel() {

    private val _lokasiDetail = MutableLiveData<ApiResponseLokasiDetail>()
    val lokasiDetail: LiveData<ApiResponseLokasiDetail> get() = _lokasiDetail

    private val _ulasanList = MutableLiveData<List<Ulasan>>()
    val ulasanList: LiveData<List<Ulasan>> get() = _ulasanList

    fun getLokasiDetail(lokasiId: Int,token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getLokasiDetail(lokasiId,token)
                _lokasiDetail.postValue(response.body())
                _ulasanList.postValue(response.body()?.data?.ulasan)
            } catch (e: Exception) {
                Log.d("gagal meload detail", "getLokasiDetail: ${e.message} ")
            }
        }
    }
}
