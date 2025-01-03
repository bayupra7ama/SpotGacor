package com.bayupratama.spotgacor.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bayupratama.spotgacor.data.retrofit.ApiService
import com.bayupratama.spotgacor.ui.home.ui.story.StoryViewModel

class StoryViewModelFactory(private val apiService: ApiService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

