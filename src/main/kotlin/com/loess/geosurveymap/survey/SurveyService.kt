package com.loess.geosurveymap.survey

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.location.LocationRequest
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

    @Transactional(readOnly = true)
    fun getAllSurveys(): List<Survey> = surveyRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getSurveyByLocation(locationRequest: LocationRequest): Survey =
        locationService.getLocationByCoordinates(locationRequest).survey

    @Transactional(readOnly = true)
    fun getAllSurveysWithinRadius(locationRequest: LocationRequest, radius: Double): List<Survey> =
        locationService.getAllWithinRadius(locationRequest, radius).map { it.survey }

    @Transactional(readOnly = true)
    fun getAllSurveysWithinBoundingBox(boundingBox: BoundingBox): List<Survey> =
        locationService.getAllWithinBoundingBox(boundingBox).map { it.survey }

}