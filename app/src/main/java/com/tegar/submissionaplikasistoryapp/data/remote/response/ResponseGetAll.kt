package com.tegar.submissionaplikasistoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseGetAll(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

