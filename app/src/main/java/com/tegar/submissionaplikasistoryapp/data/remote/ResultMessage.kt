package com.tegar.submissionaplikasistoryapp.data.remote

sealed class ResultMessage<out T> {
    data class Success<out T>(val data: T) : ResultMessage<T>()
    data class Error(val exception: Exception) : ResultMessage<Nothing>()
    data object Loading : ResultMessage<Nothing>()
}