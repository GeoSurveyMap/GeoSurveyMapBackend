package com.loess.geosurveymap.apiutils.dto

data class ApiResponsePaging(
    val totalPages: Int,
    val totalElements: Long,
    val page: Int,
    val pageSize: Int
)
