package com.loess.geosurveymap.apiutils.dto

data class ApiResponse<T: Any>(
    val data: T?,
    val metadata: ApiResponseMetadata
)
