package com.loess.geosurveymap.location

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.exceptions.NotFoundException
import com.loess.geosurveymap.survey.SurveyEntity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val geometryFactory: GeometryFactory,
) {

    @Transactional
    fun saveLocationForSurvey(locationRequest: LocationRequest, surveyEntity: SurveyEntity): Location =
        with(locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            val locationEntity = LocationEntity(location = point, survey = surveyEntity)

            return locationRepository.save(locationEntity).toResponse()
        }

    @Transactional(readOnly = true)
    fun getLocationByCoordinates(locationRequest: LocationRequest): Location =
        with(locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            return locationRepository.findByLocation(point)?.toResponse()
                ?: throw NotFoundException("Location with those coordinates [x: ${x}, y: ${y}] has not been found")
        }

    @Transactional(readOnly = true)
    fun getAllWithinRadius(locationRequest: LocationRequest, radius: Double): List<Location> {
        with(locationRequest) {
            return locationRepository.findAllWithinRadius(x, y, radius).map { it.toResponse() }
        }
    }

    @Transactional(readOnly = true)
    fun getAllWithinBoundingBox(boundingBox: BoundingBox): List<Location> {
        with(boundingBox) {
            return locationRepository.findAllWithinBoundingBox(minX, minY, maxX, maxY).map { it.toResponse() }
        }
    }
}