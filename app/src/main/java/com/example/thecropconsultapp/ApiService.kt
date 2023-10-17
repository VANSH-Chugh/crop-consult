package com.example.thecropconsultapp

import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.MultipartBody

interface ApiService {
    @Multipart
    @POST("upload") // Replace with the actual endpoint URL
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<JsonResponse> // Replace with your response model
}
