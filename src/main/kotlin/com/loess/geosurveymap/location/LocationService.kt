package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.SurveyEntity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val geometryFactory: GeometryFactory,
) {

    @Transactional
    fun saveLocationForSurvey(locationRequest: LocationRequest, surveyEntity: SurveyEntity): Location =
        with (locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            val locationEntity = LocationEntity(location = point, survey = surveyEntity)

            return locationRepository.save(locationEntity).toResponse()
    }
}