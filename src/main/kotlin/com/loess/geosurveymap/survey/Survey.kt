package com.loess.geosurveymap.survey

import com.loess.geosurveymap.location.LocationRequest
import com.loess.geosurveymap.location.LocationSimple

data class Survey(
    val id: Long? = null,
    val category: Category,
    val description: String,
    val solution: String,
    var location: LocationSimple
)

data class SurveyRequest(
    val category: Category,
    val description: String,
    val solution: String,
    val locationRequest: LocationRequest
)