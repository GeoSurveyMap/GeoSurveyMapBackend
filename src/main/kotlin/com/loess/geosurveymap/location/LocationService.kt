package com.loess.geosurveymap.location

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import com.loess.geosurveymap.survey.Category
import com.loess.geosurveymap.survey.SurveyEntity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
            return locationRepository.findByLocation(point).mapToLocationSimple()
        }

    fun getAllWithinRadius(coordinates: Coordinates, radius: Double): List<Location> {
        with(coordinates) {
            return locationRepository.findAllWithinRadius(x, y, radius).mapToLocationSimple()
        }
    }

    fun getAllWithinBoundingBox(boundingBox: BoundingBox, categories: List<Category>? = null): List<Location> {
        with(boundingBox) {
            return locationRepository.findAllWithinBoundingBox(minX, minY, maxX, maxY, categories?.joinToString(",")).mapToLocationSimple()
        }
    }

    fun getAllLocations(): List<Location> = locationRepository.findAll()
        .map { it.toResponse(LocationSimple(it.location.x, it.location.y, name = it.name)) }

    fun getFilteredLocations(filters: Filters? = null, pageable: Pageable): Page<Location> {
        return filters?.let {
            val specification = LocationSpecification.build(it)
            locationRepository.findAll(specification, pageable).mapToLocationSimple()
        } ?: run {
            locationRepository.findAll(pageable).mapToLocationSimple()
        }
    }

    fun getLocationBySurveyCategory(coordinates: Coordinates, categories: List<Category>): List<Location> {
        with(coordinates) {
            val point = geometryFactory.createPoint(Coordinate(x, y))
            return locationRepository.findByLocationAndSurvey_CategoryIn(point, categories).mapToLocationSimple()
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

    fun getAllUnacceptedSurveys(page: Pageable): Page<Location> = locationRepository.findAllUnacceptedSurveys(page).mapToLocationSimple()

    fun getSimpleLocationByUser(kindeId: String): List<Location> = locationRepository.findByUser(kindeId).mapToLocationSimple()

    fun getLocationsByUser(kindeId: String): List<LocationEntity> = locationRepository.findByUser(kindeId)

    fun deleteLocation(locationEntity: LocationEntity) = locationRepository.delete(locationEntity)

    private fun Page<LocationEntity>.mapToLocationSimple(): Page<Location> = this.map { loc -> loc.toResponse(LocationSimple(loc.location.x, loc.location.y, name = loc.name)) }
    private fun List<LocationEntity>.mapToLocationSimple(): List<Location> = this.map { loc -> loc.toResponse(LocationSimple(loc.location.x, loc.location.y, name = loc.name)) }
}