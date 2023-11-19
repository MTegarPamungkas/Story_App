package com.tegar.submissionaplikasistoryapp.data.remote.retrofit

import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseAdd
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseGetAll
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseLogin
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): ResponseRegister

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): ResponseLogin

    @GET("stories")
    suspend fun getAllStoriesWithPagging(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
    ): ResponseGetAll

    @GET("stories?location=1")
    suspend fun getAllStoriesWithLocation(
        @Query("size") size: Int = 40,
    ): ResponseGetAll

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
    ): ResponseAdd

}