package com.loess.geosurveymap.apiutils

import com.loess.geosurveymap.apiutils.dto.*
import org.springframework.stereotype.Service
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import java.time.Instant

@Service
class ApiResponseBuilder {

    fun <T : Any> buildObjectResponse(data: T, processingStart: Instant): ApiResponse<T> =
        ApiResponse(data = data, metadata = buildBasicMetadata(processingStart))

    fun <T : Any> buildPageResponse(page: Page<T>, processingStart: Instant): ApiResponse<List<T>> {
        return ApiResponse(
            data = page.content,
            metadata = buildBasicMetadata(processingStart).copy(
                paging = ApiResponsePaging(
                    totalPages = page.totalPages,
                    totalElements = page.totalElements,
                    page = page.pageable.pageNumber,
                    pageSize = page.pageable.pageSize
                )
            )
        )
    }

    fun <T : Any> buildErrorResponse(
        processingStart: Instant,
        httpStatus: HttpStatus,
        message: String,
        errors: List<ApiResponseErrorElement> = emptyList()
    ): ApiResponse<T> =
        ApiResponse(
            data = null,
            metadata = buildBasicMetadata(processingStart).copy(
                error = ApiResponseError(
                    httpStatusCode = httpStatus.value(),
                    message = message,
                    errors = errors
                )
            )
        )

    private fun buildBasicMetadata(processingStart: Instant) = ApiResponseMetadata(
        processingStart = processingStart,
        processingEnd = Instant.now()
    )
}
