package com.bayupratama.spotgacor.data.retrofit

import com.bayupratama.spotgacor.data.response.ApiResponseListLokasi
import com.bayupratama.spotgacor.data.response.ApiResponseLokasiDetail
import com.bayupratama.spotgacor.data.response.ApiResponseRegister
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("api/register/")
        fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("nomor_telpon") nomor_telpon : String,
        @Field("password") password : String,
        @Field("password_confirmation") password_confirmation : String,
    ): Call<ApiResponseRegister>


    @GET("api/lokasi")
    fun getLokasi(
        @Header("Authorization") token: String // Pass the token in the header
    ): Call<ApiResponseListLokasi>

    @GET("api/lokasi")
    suspend fun getLokasiPaged(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): ApiResponseListLokasi

    @GET("api/lokasi/{id}")
    suspend fun getLokasiDetail(
        @Path("id") lokasiId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponseLokasiDetail>



}