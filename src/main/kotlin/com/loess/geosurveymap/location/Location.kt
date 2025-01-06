package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.Survey
import com.loess.geosurveymap.user.CountryCode

data class Location(
    val id: Long? = null,
    val x: Double,
    val y: Double,
    val name: String,
    val survey: Survey,
    val countryCode: CountryCode
)

data class LocationRequest(
    val x: Double,
    val y: Double,
    val name: String,
    val countryCode: CountryCode
)

data class LocationResponse(
    val x: Double,
    val y: Double,
    val name: String,
    val countryCode: CountryCode
)
