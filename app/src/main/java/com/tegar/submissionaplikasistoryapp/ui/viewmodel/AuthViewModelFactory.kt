package com.tegar.submissionaplikasistoryapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegar.submissionaplikasistoryapp.di.Injection
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory

class AuthViewModelFactory(private val repositoryStory: RepositoryStory) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repositoryStory) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repositoryStory) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null

        fun getInstance(context: Context): AuthViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(Injection.provideRepository(context)).also {
                    instance = it
                }
            }
        }
    }
}
