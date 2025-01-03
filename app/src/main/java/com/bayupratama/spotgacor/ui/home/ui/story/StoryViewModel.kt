package com.bayupratama.spotgacor.ui.home.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import com.bayupratama.spotgacor.data.response.DataItem
import com.bayupratama.spotgacor.data.retrofit.ApiService

class StoryViewModel(apiService: ApiService) : ViewModel() {

    private val repository = StoryRepository(apiService)

    val stories: Flow<PagingData<DataItem>> = repository.getStories().cachedIn(viewModelScope)
}
