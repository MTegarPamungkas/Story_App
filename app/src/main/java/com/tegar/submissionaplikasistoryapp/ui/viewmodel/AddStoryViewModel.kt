package com.tegar.submissionaplikasistoryapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tegar.submissionaplikasistoryapp.data.remote.RepositoryStory
import com.tegar.submissionaplikasistoryapp.data.remote.ResultMessage
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseAdd
import java.io.File

class AddStoryViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> get() = _selectedImageUri
    var lat: Double? = null
    var lon: Double? = null

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun newPost(description: String, photo: File, lat: Float?, lon: Float?): LiveData<ResultMessage<ResponseAdd>> {
        return repositoryStory.newPost(description, photo, lat, lon)
    }
}
