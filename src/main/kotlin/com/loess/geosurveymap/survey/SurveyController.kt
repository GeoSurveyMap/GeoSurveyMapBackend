package com.loess.geosurveymap.survey

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import com.loess.geosurveymap.location.LocationRequest
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/survey")
class SurveyController(
    private val surveyService: SurveyService,
    private val apiRequestHandler: ApiRequestHandler
) {

    @Operation(summary = "Create a new survey")
    @PostMapping
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

    @Operation(summary = "Get survey by location")
    @GetMapping("/x/{x}/y/{y}")
    fun getSurveyByLocation(@PathVariable x: Double, @PathVariable y: Double): ApiResponse<Survey> =
        apiRequestHandler.handle {
            surveyService.getSurveyByLocation(LocationRequest(x, y))
        }

    @Operation(summary = "Get all surveys withing given radius in meters")
    @GetMapping("/x/{x}/y/{y}/radius/{radius}")
    fun getAllSurveysWithinRadius(
        @PathVariable x: Double,
        @PathVariable y: Double,
        @PathVariable radius: Double
    ): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            surveyService.getAllSurveysWithinRadius(LocationRequest(x, y), radius)
        }

}

