package com.loess.geosurveymap.survey

import com.loess.geosurveymap.location.LocationRequest

data class Survey(
    val id: Long? = null,
    val category: Category,
    val description: String,
    val solution: String,
)

data class SurveyRequest(
    val category: Category,
    val description: String,
    val solution: String,
    val locationRequest: LocationRequest
)