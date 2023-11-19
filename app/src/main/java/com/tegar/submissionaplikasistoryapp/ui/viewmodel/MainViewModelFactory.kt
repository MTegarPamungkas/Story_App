package com.tegar.submissionaplikasistoryapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegar.submissionaplikasistoryapp.di.Injection
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory

class MainViewModelFactory(private val repositoryStory: RepositoryStory) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repositoryStory) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repositoryStory) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repositoryStory) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null

        fun getInstance(context: Context): MainViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(Injection.provideRepository(context)).also {
                    instance = it
                }
            }
        }

        fun clearInstance() {
            instance = null
        }
    }
}
