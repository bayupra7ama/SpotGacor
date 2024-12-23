package com.bayupratama.spotgacor.data.retrofit

import android.content.Context
import com.bayupratama.spotgacor.helper.Sharedpreferencetoken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object{
        fun getApiService(context: Context): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val token = Sharedpreferencetoken(context).getToken()

            // Interceptor to add Authorization header
            val authInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder().apply {
                    // Add Authorization header only if token is available
                    token?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                }.build()

                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor) // Add this interceptor to handle Authorization
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://brief-sawfly-square.ngrok-free.app/") // Update with your base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
