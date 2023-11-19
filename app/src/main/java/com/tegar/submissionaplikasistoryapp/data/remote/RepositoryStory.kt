package com.tegar.submissionaplikasistoryapp.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.tegar.submissionaplikasistoryapp.data.local.database.StoriesDatabase
import com.tegar.submissionaplikasistoryapp.data.remote.paging.StoriesPagingSource
import com.tegar.submissionaplikasistoryapp.data.remote.paging.StoriesRemoteMediator
import com.tegar.submissionaplikasistoryapp.data.remote.response.ErrorResponse
import com.tegar.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.tegar.submissionaplikasistoryapp.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class RepositoryStory(
    private val apiService: ApiService,
    private val storiesDatabase: StoriesDatabase,
) {
    fun register(name: String, email: String, password: String) = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.register(name, email, password)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            emit(ResultMessage.Error(Exception(errorMessage)))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun login(email: String, password: String) = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.login(email, password)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            emit(ResultMessage.Error(Exception(errorMessage)))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStoriesWithPaging(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoriesRemoteMediator(storiesDatabase, apiService),
            pagingSourceFactory = {
                StoriesPagingSource(apiService)
                storiesDatabase.storiesDao().getAllStories()
            },
        ).liveData
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStoriesWithPagingOffline(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoriesRemoteMediator(storiesDatabase, apiService),
            pagingSourceFactory = {
                storiesDatabase.storiesDao().getAllStories()
            },
        ).liveData
    }


    fun getAllStoriesWithLocation() = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.getAllStoriesWithLocation()
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            emit(ResultMessage.Error(Exception(errorMessage)))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun newPost(description: String, photo: File, lat: Float?, lon: Float?) = liveData {
        val requestImageFile = photo.asRequestBody("image/jpeg".toMediaType())
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestImageFile
        )
        try {
            emit(ResultMessage.Loading)
            val response = apiService.postStory(requestBody, multipartBody, lat, lon)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            emit(ResultMessage.Error(Exception(errorMessage)))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    companion object {
        @Volatile
        private var instance: RepositoryStory? = null
        fun getInstance(
            apiService: ApiService,
            database: StoriesDatabase,
        ): RepositoryStory =
            instance ?: synchronized(this) {
                instance ?: RepositoryStory(apiService, database)
            }.also { instance = it }
    }
}
