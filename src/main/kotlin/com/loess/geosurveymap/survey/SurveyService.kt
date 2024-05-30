package com.loess.geosurveymap.survey

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import com.loess.geosurveymap.location.LocationService
import com.loess.geosurveymap.location.LocationSimple
import com.loess.geosurveymap.location.toSimple
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
        val location = locationService.saveLocationForSurvey(surveyRequest.locationRequest, survey)

        return survey.toResponse(location.toSimple())
    }

    @Transactional(readOnly = true)
    fun getAllSurveys(): List<Survey> = locationService.getAllLocations().map { it.survey }

    @Transactional(readOnly = true)
    fun getSurveysByLocation(locationRequest: Coordinates): List<Survey> =
        locationService.getLocationByCoordinates(locationRequest).map { it.survey }

    @Transactional(readOnly = true)
    fun getAllSurveysWithinRadius(locationRequest: Coordinates, radius: Double): List<Survey> =
        locationService.getAllWithinRadius(locationRequest, radius).map {
            it.survey.location = LocationSimple(it.x, it.y, it.name)
            it.survey
        }

    @Transactional(readOnly = true)
    fun getAllSurveysWithinBoundingBox(boundingBox: BoundingBox, categories: List<Category>?): List<Survey> =
        locationService.getAllWithinBoundingBox(boundingBox, categories).map { it.survey }

    @Transactional(readOnly = true)
    fun getAllSurveysByLocationAndRadiusAndCategories(
        locationRequest: Coordinates,
        radius: Double,
        categories: List<Category>
    ): List<Survey> =
        locationService.getByCategories(locationRequest, radius, categories).map { it.survey }

    @Transactional(readOnly = true)
    fun getSurveysByCategories(locationRequest: Coordinates, categories: List<Category>): List<Survey> =
        locationService.getLocationBySurveyCategory(
            locationRequest,
            categories
        ).map { it.survey }
}