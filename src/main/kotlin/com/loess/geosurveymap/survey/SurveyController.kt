package com.loess.geosurveymap.survey

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.location.LocationRequest
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

data class CoordinatesRequest(
    val lat: Double,
    val lng: Double,
    val radius: Double? = null
)

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
    @GetMapping
    fun getSurveyByLocation(@RequestBody coordinatesRequest: CoordinatesRequest): ApiResponse<Survey> =
        apiRequestHandler.handle {
            with(coordinatesRequest) {
                surveyService.getSurveyByLocation(LocationRequest(lat, lng))
            }
        }

    @Operation(summary = "Get all surveys withing given radius in meters")
    @GetMapping("/within-radius")
    fun getAllSurveysWithinRadius(@RequestBody coordinatesRequest: CoordinatesRequest): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            with(coordinatesRequest) {
                radius?.let {
                    surveyService.getAllSurveysWithinRadius(LocationRequest(lng, lat), radius)
                } ?: throw IllegalArgumentException("Radius must be not be null")
            }
        }

    @Operation(summary = "Get all surveys withing given bounding box")
    @GetMapping("/bounding-box")
    fun getAllSurveysWithinRadius(@RequestBody boundingBox: BoundingBox): ApiResponse<List<Survey>> =
        apiRequestHandler.handle {
            surveyService.getAllSurveysWithinBoundingBox(boundingBox)
        }

}

