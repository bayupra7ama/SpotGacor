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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class LokasiViewModel(private val apiService: ApiService, private val token: String) : ViewModel() {

    private val searchQuery = MutableStateFlow<String?>(null)
    private val selectedJenisIkan = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val lokasiPagingData: Flow<PagingData<LokasiItem>> =
        combine(searchQuery, selectedJenisIkan) { query, jenisIkan ->
            Pair(query, jenisIkan)
        }.flatMapLatest { (query, jenisIkan) ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    LokasiPagingSource(apiService, token, query, jenisIkan)
                }
            ).flow.cachedIn(viewModelScope)
        }

    fun updateSearchQuery(query: String?) {
        searchQuery.value = query
    }

    fun updateJenisIkan(jenis: String?) {
        selectedJenisIkan.value = jenis
    }
}

