package com.tegar.submissionaplikasistoryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory

class LoginViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    fun login(email: String, password: String) = repositoryStory.login(email, password)
}