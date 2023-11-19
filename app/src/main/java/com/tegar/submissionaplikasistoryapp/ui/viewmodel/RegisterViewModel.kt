package com.tegar.submissionaplikasistoryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory

class RegisterViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {
    fun register(name: String, email: String, password: String) = repositoryStory.register(name, email, password)
}