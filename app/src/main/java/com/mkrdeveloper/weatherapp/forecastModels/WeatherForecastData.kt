package com.mkrdeveloper.weatherapp.forecastModels

data class WeatherForecastData(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Forecast>,
    val message: Int
)