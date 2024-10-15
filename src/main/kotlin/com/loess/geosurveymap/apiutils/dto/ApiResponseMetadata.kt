package com.loess.geosurveymap.apiutils.dto

import java.time.Instant
import java.time.Duration

data class ApiResponseMetadata(
    val error: ApiResponseError? = null,
    val paging: ApiResponsePaging? = null,
    val processingStart: Instant,
    val processingEnd: Instant,
    val processingDuration: String = Duration.between(processingStart, processingEnd).toString()
)
