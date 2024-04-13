package com.loess.geosurveymap.apiutils
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.loess.geosurveymap.apiutils.dto.ApiResponseErrorElement
import com.loess.geosurveymap.exceptions.ApiException
import com.loess.geosurveymap.exceptions.BadRequestException
import com.loess.geosurveymap.exceptions.ConflictException
import com.loess.geosurveymap.exceptions.NotFoundException
import com.mobile.amigoapp.api.utils.getProcessingStart
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.Locale
import kotlin.Exception

@ControllerAdvice
class RestExceptionHandler(
    private val apiResponseBuilder: ApiResponseBuilder,
) {

    private val log = KotlinLogging.logger { }

    @ExceptionHandler(Exception::class)
    fun handleCustomExceptions(
        exception: Exception,
        request: HttpServletRequest,
        locale: Locale
    ): ResponseEntity<Any> {
        val (httpStatus, message, errors) = when (exception) {
            is HttpMessageNotReadableException -> handleHttpMessageNotReadableException(exception)
            is ApiException -> ExceptionHttpMapping(exception.getHttpStatus(), exception.message, exception.errors)
            is MethodArgumentTypeMismatchException -> ExceptionHttpMapping(
                HttpStatus.BAD_REQUEST,
                "Provided value [${exception.value}] for parameter ${exception.name} is invalid. It must be valid [${exception.requiredType?.simpleName}]."
            )
            is MissingServletRequestParameterException -> ExceptionHttpMapping(
                HttpStatus.BAD_REQUEST,
                "Parameter [${exception.parameterName}] has not been provided."
            )
            is AccessDeniedException -> ExceptionHttpMapping(
                HttpStatus.FORBIDDEN,
                "Access is denied."
            )
            else -> ExceptionHttpMapping(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error [${exception::class.simpleName}] occurred. Please contact application support."
            )
        }
        val requestDetails = "User: user, Method: ${request.method}, Path: ${request.requestURI}" // TODO: add user when security will be implemented
        if (httpStatus.is5xxServerError) {
            log.error(exception) { "Handled server error exception. $requestDetails" }
        } else {
            log.warn(exception) { "Handled exception. $requestDetails" }
        }
        val response = apiResponseBuilder.buildErrorResponse<Any>(request.getProcessingStart(), httpStatus, message, errors)
        return ResponseEntity(response, httpStatus)
    }

    private fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ExceptionHttpMapping =
        when (val cause = exception.cause) {

            is JsonProcessingException -> ExceptionHttpMapping(
                HttpStatus.BAD_REQUEST,
                "Message not readable, syntax error somewhere around line: ${cause.location.lineNr}, column: ${cause.location.columnNr}"
            )

            else -> ExceptionHttpMapping(
                HttpStatus.BAD_REQUEST,
                "Bad request. Caused by error [${exception::class.simpleName}]. Presumably something is wrong with the request, if you need help please contact application support."
            )
        }

    private fun JsonMappingException.getSimplePath() =
        this.path.joinToString(separator = "") { if (it.fieldName != null) ".${it.fieldName}" else "[${it.index}]" }
            .removePrefix(".")

    data class ExceptionHttpMapping(
        val httpStatus: HttpStatus,
        val message: String,
        val errors: List<ApiResponseErrorElement> = emptyList()
    )
}

fun ApiException.getHttpStatus(): HttpStatus =
    when (this) {
        is ConflictException -> HttpStatus.CONFLICT
        is BadRequestException -> HttpStatus.BAD_REQUEST
        is NotFoundException -> HttpStatus.NOT_FOUND
        else -> HttpStatus.INTERNAL_SERVER_ERROR
    }
