package com.bayupratama.spotgacor.ui.home.ui.lokasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bayupratama.spotgacor.data.paging.LokasiPagingSource
import com.bayupratama.spotgacor.data.response.LokasiItem
import com.bayupratama.spotgacor.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class LokasiViewModel(private val apiService: ApiService, private val token: String) : ViewModel() {

    val lokasiPagingData: Flow<PagingData<LokasiItem>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { LokasiPagingSource(apiService, token) }
    ).flow.cachedIn(viewModelScope)
}
