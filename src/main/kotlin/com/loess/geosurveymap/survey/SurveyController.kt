package com.loess.geosurveymap.survey

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/survey")
class SurveyController(
    private val surveyService: SurveyService,
    private val apiRequestHandler: ApiRequestHandler
) {

    @Operation(
        summary = "Create a new survey",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @PostMapping("/create")
    fun createSurvey(@RequestBody surveyRequest: SurveyRequest): ApiResponse<Survey> =
        apiRequestHandler.handle {
            surveyService.saveSurvey(surveyRequest)
        }

    @Operation(summary = "Get all existing surveys")
    @GetMapping("/all")
    fun getAllSurveys(): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            surveyService.getAllSurveys()
        }

    @Operation(summary = "General endpoint for getting surveys by location and optional parameters")
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
                radius != null && categories != null -> surveyService.getAllSurveysByLocationAndRadiusAndCategories(coordinates, radius, categories)
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

}

