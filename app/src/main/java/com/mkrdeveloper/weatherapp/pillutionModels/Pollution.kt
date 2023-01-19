package com.mkrdeveloper.weatherapp.pillutionModels

data class Pollution(
    val coord: Coord,
    val list: List<PollutionData>
)