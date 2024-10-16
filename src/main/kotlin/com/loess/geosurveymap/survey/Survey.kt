package com.loess.geosurveymap.survey

import com.loess.geosurveymap.location.LocationRequest
import com.loess.geosurveymap.location.LocationSimple
import com.loess.geosurveymap.user.User

data class Survey(
    val id: Long? = null,
    val category: Category,
    val description: String,
    val solution: String,
    var location: LocationSimple,
    val affectedArea: Double,
    val user: User
)

data class SurveyRequest(
    val affectedArea: Double,
    val category: Category,
    val description: String,
    val solution: String,
    val locationRequest: LocationRequest,
)