package com.tegar.submissionaplikasistoryapp.di

import android.content.Context
import com.tegar.submissionaplikasistoryapp.data.local.database.StoriesDatabase
import com.tegar.submissionaplikasistoryapp.data.local.preference.UserDataStore
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory
import com.tegar.submissionaplikasistoryapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): RepositoryStory {
        val pref = UserDataStore.getInstance(context)
        val user = runBlocking { pref.getUserData().first() }
        val database = StoriesDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(user.token)
        return RepositoryStory(apiService, database)
    }

}