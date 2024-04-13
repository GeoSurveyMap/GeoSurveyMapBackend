package com.loess.geosurveymap.location

data class Location(
    val id: Long? = null,
    val x: Double,
    val y: Double
)

data class LocationRequest(
    val x: Double,
    val y: Double
)