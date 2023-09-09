package com.woojun.post_test

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("설정")
    fun uploadImage(
        @Part FixImage: MultipartBody.Part,
        @Part("id") id: String
    ): Call<String>
}