package com.loess.geosurveymap.apiutils.dto
import java.time.Duration
import java.time.Instant

data class ApiResponseMetadata(
    val error: ApiResponseError? = null,
    val paging: ApiResponsePaging? = null,
    val threadId: String? = null,
    val assistantId: String? = null,
    val processingStart: Instant,
    val processingEnd: Instant,
    val processingDuration: String = Duration.between(processingStart, processingEnd).toString()
)
