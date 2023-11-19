package com.tegar.submissionaplikasistoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory
import com.tegar.submissionaplikasistoryapp.data.remote.response.ListStoryItem

class MainViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    var isStoryAdded = false
    fun getStories(): LiveData<PagingData<ListStoryItem>>{
        return repositoryStory.getAllStoriesWithPaging().cachedIn(viewModelScope)
    }

    fun getStoriesOffline(): LiveData<PagingData<ListStoryItem>>{
        return repositoryStory.getAllStoriesWithPagingOffline().cachedIn(viewModelScope)
    }

}