package com.tegar.submissionaplikasistoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("loginResult")
	val loginResult: LoginResult? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

