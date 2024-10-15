package com.loess.geosurveymap.location

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import com.loess.geosurveymap.survey.Category
import com.loess.geosurveymap.survey.SurveyEntity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LocationService(
    private val locationRepository: LocationRepository,
    private val geometryFactory: GeometryFactory,
) {

    @Transactional
    fun saveLocationForSurvey(locationRequest: LocationRequest, surveyEntity: SurveyEntity): Location =
        with(locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            val locationEntity = LocationEntity(location = point, survey = surveyEntity, name = name)

            return locationRepository.save(locationEntity).toResponse(LocationSimple(x, y, name = name))
        }

    fun getLocationByCoordinates(locationRequest: Coordinates): List<Location> =
        with(locationRequest) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            return locationRepository.findByLocation(point).map { it.toResponse(LocationSimple(x, y, name = it.name)) }
        }

    fun getAllWithinRadius(coordinates: Coordinates, radius: Double): List<Location> {
        with(coordinates) {
            return locationRepository.findAllWithinRadius(x, y, radius)
                .map { it.toResponse(LocationSimple(x, y, name = it.name)) }
        }
    }

    fun getAllWithinBoundingBox(boundingBox: BoundingBox, categories: List<Category>? = null): List<Location> {
        with(boundingBox) {
            return locationRepository.findAllWithinBoundingBox(minX, minY, maxX, maxY, categories?.joinToString(","))
                .map { it.toResponse(LocationSimple(it.location.x, it.location.y, name = it.name)) }
        }
    }

    fun getAllLocations(): List<Location> = locationRepository.findAll()
        .map { it.toResponse(LocationSimple(it.location.x, it.location.y, name = it.name)) }

    fun getLocationBySurveyCategory(coordinates: Coordinates, categories: List<Category>): List<Location> {
        with(coordinates) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            return locationRepository.findByLocationAndSurvey_CategoryIn(point, categories)
                .map { it.toResponse(LocationSimple(x, y, name = it.name)) }
        }
    }

    fun getByCategories(
        coordinates: Coordinates,
        radius: Double,
        categories: List<Category>
    ): List<Location> {
        return locationRepository.findSurveysByLocationAndCategories(
            coordinates.x,
            coordinates.y,
            radius,
            categories.joinToString(",")
        ).map { it.toResponse(LocationSimple(it.location.x, it.location.y, it.name)) }
    }

}