package com.loess.geosurveymap.location

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.exceptions.NotFoundException
import com.loess.geosurveymap.survey.Category
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
        with(locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            val locationEntity = LocationEntity(location = point, survey = surveyEntity)

            return locationRepository.save(locationEntity).toResponse(LocationSimple(x, y))
        }

    @Transactional(readOnly = true)
    fun getLocationByCoordinates(locationRequest: LocationRequest): List<Location> =
        with(locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            return locationRepository.findByLocation(point).map { it.toResponse(LocationSimple(x, y)) }
        }

    @Transactional(readOnly = true)
    fun getAllWithinRadius(locationRequest: LocationRequest, radius: Double): List<Location> {
        with(locationRequest) {
            return locationRepository.findAllWithinRadius(x, y, radius).map { it.toResponse(LocationSimple(x, y)) }
        }
    }

    @Transactional(readOnly = true)
    fun getAllWithinBoundingBox(boundingBox: BoundingBox, categories: List<Category>? = null): List<Location> {
        with(boundingBox) {
            return locationRepository.findAllWithinBoundingBox(minX, minY, maxX, maxY, categories?.joinToString(","))
                .map { it.toResponse(LocationSimple(it.location.x, it.location.y)) }
        }
    }

    @Transactional(readOnly = true)
    fun getAllLocations(): List<Location> = locationRepository.findAll()
        .map { it.toResponse(LocationSimple(it.location.x, it.location.y)) }

    @Transactional(readOnly = true)
    fun getLocationBySurveyCategory(locationRequest: LocationRequest, categories: List<Category>): List<Location> {
        with(locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            return locationRepository.findByLocationAndSurvey_CategoryIn(point, categories).map { it.toResponse(LocationSimple(x, y)) }
        }
    }

}