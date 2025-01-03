package com.bayupratama.spotgacor.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bayupratama.spotgacor.data.response.DataItem
import com.bayupratama.spotgacor.data.retrofit.ApiService

class StoryPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, DataItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItem> {
        return try {
            val currentPage = params.key ?: 1
            val response = apiService.getStoryPaged(currentPage)

            LoadResult.Page(
                data = response.data?.data?.filterNotNull() ?: emptyList(),
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (response.data?.nextPageUrl == null) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DataItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
