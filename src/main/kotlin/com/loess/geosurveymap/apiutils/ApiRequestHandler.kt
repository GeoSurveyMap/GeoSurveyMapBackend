package com.loess.geosurveymap.apiutils

import com.loess.geosurveymap.apiutils.dto.ApiResponse
import org.springframework.core.io.Resource
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ApiRequestHandler(private val apiResponseBuilder: ApiResponseBuilder) {

    fun <T : Any> handle(data: () -> T): ApiResponse<T> {
        val processingStart = Instant.now()
        val result = data()
        return apiResponseBuilder.buildObjectResponse(result, processingStart)
    }

    fun <T : Any> handlePage(data: () -> Page<T>): ApiResponse<List<T>> {
        val processingStart = Instant.now()
        val result = data()
        return apiResponseBuilder.buildPageResponse(result, processingStart)
    }

    fun handleResource(contentType: String, customHeaders: HttpHeaders? = null, data: () -> Resource): ResponseEntity<Resource> {
        val resource = data()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${resource.filename}")
            .body(resource)
    }
}
