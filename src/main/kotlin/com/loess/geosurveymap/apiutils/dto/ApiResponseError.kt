package com.loess.geosurveymap.apiutils.dto

data class ApiResponseError(
    val httpStatusCode: Int,
    val message: String,
    val errors: List<ApiResponseErrorElement> = emptyList()
)

data class ApiResponseErrorElement(
    val key: String,
    val cause: String,
    val value: Any? = null
)
