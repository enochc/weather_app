package com.example.weather.util

import com.example.weather.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface WeatherService {
    //api.openweathermap.org/data/2.5/forecast?q=salt lake city&APPID=da65fafb6cb9242168b7724fb5ab75e7&cnt=1
    @GET("data/2.5/forecast?APPID=c87aa0a81a65b025ba64901b64347e80&units=imperial&cnt=1")
    suspend fun getWeather(@Query("id") id: Int): WeatherResponse

    @GET("data/2.5/forecast?APPID=c87aa0a81a65b025ba64901b64347e80&units=imperial&cnt=1")
    suspend fun getWeatherByCity(@Query("q") cityName: String): WeatherResponse

    @GET("data/2.5/forecast?APPID=c87aa0a81a65b025ba64901b64347e80&units=imperial&cnt=1")
    suspend fun getWeatherByLoc(
        @Query("lat") lat: Double,
        @Query("lon") lng: Double): WeatherResponse

}