package com.loess.geosurveymap.survey

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import com.loess.geosurveymap.location.Location
import com.loess.geosurveymap.location.LocationService
import com.loess.geosurveymap.location.LocationSimple
import com.loess.geosurveymap.location.toSimple
import com.loess.geosurveymap.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SurveyService(
    private val surveyRepository: SurveyRepository,
    private val locationService: LocationService,
    private val userService: UserService
) {

    @Transactional
    fun saveSurvey(surveyRequest: SurveyRequest, kindeId: String): Survey {
        val user = userService.findByKindeId(kindeId)
        val survey = surveyRepository.save(surveyRequest.toEntity(user))
        val location = locationService.saveLocationForSurvey(surveyRequest.locationRequest, survey)

        return survey.toResponse(location.toSimple())
    }

    fun getAllSurveys(): List<Survey> = locationService.getAllLocations().map { it.survey }

    fun getAllSurveysWithLocation(): List<Location> = locationService.getAllLocations()

    fun getSurveysByLocation(locationRequest: Coordinates): List<Survey> =
        locationService.getLocationByCoordinates(locationRequest).map { it.survey }

    fun getAllSurveysWithinRadius(locationRequest: Coordinates, radius: Double): List<Survey> =
        locationService.getAllWithinRadius(locationRequest, radius).map {
            it.survey.location = LocationSimple(it.x, it.y, it.name)
            it.survey
        }

    fun getAllSurveysWithinBoundingBox(boundingBox: BoundingBox, categories: List<Category>?): List<Survey> =
        locationService.getAllWithinBoundingBox(boundingBox, categories).map { it.survey }

    fun getAllSurveysByLocationAndRadiusAndCategories(
        locationRequest: Coordinates,
        radius: Double,
        categories: List<Category>
    ): List<Survey> =
        locationService.getByCategories(locationRequest, radius, categories).map { it.survey }

    fun getSurveysByCategories(locationRequest: Coordinates, categories: List<Category>): List<Survey> =
        locationService.getLocationBySurveyCategory(
            locationRequest,
            categories
        ).map { it.survey }
}