package com.bayupratama.spotgacor.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bayupratama.spotgacor.data.response.LokasiItem
import com.bayupratama.spotgacor.data.retrofit.ApiService

class LokasiPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, LokasiItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LokasiItem> {
        val page = params.key ?: 1 // Default ke halaman pertama
        return try {
            val response = apiService.getLokasiPaged("Bearer $token", page)
            val lokasiList = response.responseData?.lokasiList?.filterNotNull() ?: emptyList()

            LoadResult.Page(
                data = lokasiList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.responseData?.nextPageUrl == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LokasiItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
