package com.example.weather.util

import android.content.Context
import com.example.weather.debug
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitBuilder {

    private const val BASE_URL = "http://api.openweathermap.org/"
    private const val cacheSize = (10 * 1024 * 1024).toLong()

    private fun getRetrofit(context: Context): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val myCache = Cache(context.cacheDir, cacheSize)

        val client: OkHttpClient = OkHttpClient.Builder()
            .cache(myCache)
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                // Cache a network request
                val original = chain.request()
                val request = original.newBuilder().header( // not sure if cashing is really working
                    "Cache-Control",
                    "public, max-stale=" + 5 // 5 seconds... I  think
                ).build()
                val response = chain.proceed(request)
                if (!response.isSuccessful) {
                    debug("no worky!! blahhhh")
                }
                response

            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun weatherService(context: Context): WeatherService =
        getRetrofit(context).create(WeatherService::class.java)
}