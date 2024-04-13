package com.loess.geosurveymap.survey

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/survey")
class SurveyController(
    private val surveyService: SurveyService,
    private val apiRequestHandler: ApiRequestHandler
) {

    @PostMapping
    fun createSurvey(@RequestBody surveyRequest: SurveyRequest): ApiResponse<Survey> =
        apiRequestHandler.handle {
            surveyService.saveSurvey(surveyRequest)
        }
}

