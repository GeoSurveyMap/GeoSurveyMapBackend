package com.loess.geosurveymap.survey

import com.loess.geosurveymap.location.LocationRequest
import com.loess.geosurveymap.location.LocationResponse
import com.loess.geosurveymap.user.User
import java.time.Instant

data class Survey(
    val id: Long? = null,
    val category: Category,
    val description: String,
    val solution: String,
    val problemSolution: String,
    var location: LocationResponse,
    val affectedArea: Double,
    val user: User,
    val createdAt: Instant,
    val filePath: String?,
    val status: SurveyStatus
)

data class SurveyRequest(
    val affectedArea: Double,
    val category: Category,
    val description: String,
    val solution: String,
    val problemSolution: String,
    val locationRequest: LocationRequest,
)