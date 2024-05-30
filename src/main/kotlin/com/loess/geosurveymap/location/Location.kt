package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.Survey

data class Location(
    val id: Long? = null,
    val x: Double,
    val y: Double,
    val name: String,
    val survey: Survey
)

data class LocationRequest(
    val x: Double,
    val y: Double,
    val name: String
)

data class LocationSimple(
    val x: Double,
    val y: Double,
    val name: String
)
