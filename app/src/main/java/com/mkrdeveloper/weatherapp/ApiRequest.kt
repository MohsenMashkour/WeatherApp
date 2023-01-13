package com.mkrdeveloper.weatherapp

import com.mkrdeveloper.weatherapp.forecastModels.WeatherForecastData
import com.mkrdeveloper.weatherapp.models.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequest {

    @GET("weather?")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<WeatherData>

    @GET("forecast?")
    fun getForecastWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<WeatherForecastData>

}