package com.loess.geosurveymap.survey

import com.loess.geosurveymap.location.LocationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SurveyService(
    private val surveyRepository: SurveyRepository,
    private val locationService: LocationService
) {

    @Transactional
    fun saveSurvey(surveyRequest: SurveyRequest): Survey {
        val survey = surveyRepository.save(surveyRequest.toEntity())
        locationService.saveLocationForSurvey(surveyRequest.locationRequest, survey)

        return survey.toResponse()
    }

}