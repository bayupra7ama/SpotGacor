package com.bayupratama.spotgacor.ui.home.ui.story

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bayupratama.spotgacor.data.paging.StoryPagingSource
import kotlinx.coroutines.flow.Flow
import com.bayupratama.spotgacor.data.response.DataItem
import com.bayupratama.spotgacor.data.retrofit.ApiService

class StoryRepository(private val apiService: ApiService) {

    fun getStories(): Flow<PagingData<DataItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).flow
    }
}
