package com.bayupratama.spotgacor.data.retrofit

import com.bayupratama.spotgacor.data.response.AddLokasiResponse
import com.bayupratama.spotgacor.data.response.ApiAddStoryResponse
import com.bayupratama.spotgacor.data.response.ApiAddUlsanResponse
import com.bayupratama.spotgacor.data.response.ApiResponseListLokasi
import com.bayupratama.spotgacor.data.response.ApiResponseLogin
import com.bayupratama.spotgacor.data.response.ApiResponseLogout
import com.bayupratama.spotgacor.data.response.ApiResponseLokasiDetail
import com.bayupratama.spotgacor.data.response.ApiResponseRegister
import com.bayupratama.spotgacor.data.response.ApiStoryResponse
import com.bayupratama.spotgacor.data.response.Ulasan
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("api/register/")
        fun postRegister(
        @Field("email") email: String,
        @Field("password") password : String,
    ): Call<ApiResponseLogin>

    @FormUrlEncoded
    @POST("api/register/")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("nomor_telpon") nomor_telpon : String,
        @Field("password") password : String,
        @Field("password_confirmation") password_confirmation : String,
    ): Call<ApiResponseRegister>


    @FormUrlEncoded
    @POST("api/login/")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password : String,
    ): Call<ApiResponseLogin>

    @GET("api/lokasi")
    suspend fun getLokasiPaged(
        @Query("page") page: Int,
        @Query("nama_tempat") nama_tempat: String? = null,


    ): ApiResponseListLokasi

    @GET("api/lokasi")
    suspend fun getListLokasi(
        ): ApiResponseListLokasi

    @GET("api/lokasi/{id}")
    suspend fun getLokasiDetail(
        @Path("id") lokasiId: Int,
        @Header("Authorization") token: String,

    ): Response<ApiResponseLokasiDetail>

    @Multipart
    @POST("api/add_lokasi")
    suspend fun addLokasi(
        @Part("nama_tempat") namaTempat: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("perlengkapan") perlengkapan: RequestBody,
        @Part("rute") rute: RequestBody,
        @Part("umpan") umpan: RequestBody,
        @Part("jenis_ikan") jenisIkan: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("long") long: RequestBody?,
        @Part images: List<MultipartBody.Part>
    ): Response<AddLokasiResponse>

    @POST("api/add_ulasan")
    suspend fun storeUlasan(
        @Query("lokasi_id") lokasiId: Int,
        @Query("rating") rating: Int,
        @Query("komentar") komentar: String
    ): Response<ApiAddUlsanResponse>

    @GET("api/stories")
    suspend fun getStoryPaged(
        @Query("page") page: Int,
        ): ApiStoryResponse

    @Multipart
    @POST("api/add_story")
    suspend fun addStory(
        @Part("isi_story") isiStory: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<ApiAddStoryResponse>


        @POST("api/logout")
        fun logout(): Call<ApiResponseLogout>


}