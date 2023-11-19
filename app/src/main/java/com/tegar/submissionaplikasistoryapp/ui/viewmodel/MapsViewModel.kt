package com.tegar.submissionaplikasistoryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory

class MapsViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    init {
        getAllStoriesWithLocation()
    }
    fun getAllStoriesWithLocation() = repositoryStory.getAllStoriesWithLocation()

}