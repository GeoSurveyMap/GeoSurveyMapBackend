package com.loess.geosurveymap.survey

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import io.minio.messages.Grant
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/survey")
class SurveyController(
    private val surveyService: SurveyService,
    private val apiRequestHandler: ApiRequestHandler,
) {

    @Operation(summary = "Create a new survey", security = [SecurityRequirement(name = "bearerAuth")])
    @PostMapping("/create")
    fun createSurvey(
        @RequestBody surveyRequest: SurveyRequest,
        @RequestParam("filePath", required = false) filePath: String? = null,
    ): ApiResponse<Survey> =
        apiRequestHandler.handle {
            val authentication = SecurityContextHolder.getContext().authentication
            val jwt = authentication.principal as Jwt
            val kindeAuthId = jwt.subject ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found in token")

            surveyService.saveSurvey(surveyRequest, kindeAuthId, filePath)
        }

    @Operation(summary = "Uploads survey file", security = [SecurityRequirement(name = "bearerAuth")])
    @PostMapping(
        value = ["/upload"],
        consumes = [
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
        ]
    )
    fun uploadFile(
        @RequestPart("file") file: MultipartFile,
    ): ApiResponse<String> =
        apiRequestHandler.handle {
            val authentication = SecurityContextHolder.getContext().authentication
            val jwt = authentication.principal as Jwt
            val kindeAuthId = jwt.subject ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found in token")

            surveyService.uploadFile(file, kindeAuthId)
        }

    @Operation(summary = "Get all existing surveys")
    @GetMapping("/all")
    fun getAllSurveys(): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            val authentication = SecurityContextHolder.getContext().authentication
            val authorities = if (authentication !== null && authentication.authorities is Collection<GrantedAuthority>) {
                val authorities: Collection<GrantedAuthority> = authentication.authorities
                authorities
            } else null

            surveyService.getAllSurveys(authorities)
        }

    @Operation(summary = "Get surveys by location and optional parameters")
    @GetMapping
    fun getSurveys(
        @RequestParam(required = true) x: Double,
        @RequestParam(required = true) y: Double,
        @RequestParam(required = false) radius: Double? = null,
        @RequestParam(required = false) categories: List<Category>? = null,
    ): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            val coordinates = Coordinates(x, y)

            when {
                radius != null && categories != null -> surveyService.getAllSurveysByLocationAndRadiusAndCategories(
                    coordinates,
                    radius,
                    categories
                )

                categories != null -> surveyService.getSurveysByCategories(coordinates, categories)
                radius != null -> surveyService.getAllSurveysWithinRadius(coordinates, radius)
                else -> surveyService.getSurveysByLocation(coordinates)
            }
        }

    @Operation(summary = "Get all surveys withing given bounding box")
    @GetMapping("/bounding-box")
    fun getAllSurveysWithinBoundingBox(
        @RequestParam(required = true) minX: Double,
        @RequestParam(required = true) maxX: Double,
        @RequestParam(required = true) minY: Double,
        @RequestParam(required = true) maxY: Double,
        @RequestParam(required = false) categories: List<Category>? = null
    ): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            surveyService.getAllSurveysWithinBoundingBox(
                BoundingBox(minX, maxX, minY, maxY),
                categories
            )
        }


    @Operation(summary = "Get user surveys")
    @GetMapping("/self")
    fun getUserSurveys(): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            val authentication = SecurityContextHolder.getContext().authentication
            val jwt = authentication.principal as Jwt
            val kindeId = jwt.subject ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found in token")
            surveyService.getUserSurveys(kindeId)
        }
}

