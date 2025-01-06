package com.loess.geosurveymap.location

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import com.loess.geosurveymap.exceptions.ForbiddenException
import com.loess.geosurveymap.survey.Category
import com.loess.geosurveymap.survey.SurveyEntity
import com.loess.geosurveymap.user.CountryCode
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.GrantedAuthority
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
            val locationEntity = LocationEntity(location = point, survey = surveyEntity, name = name, countryCode = countryCode)

            return locationRepository.save(locationEntity).toResponse(LocationResponse(x, y, name = name, countryCode = countryCode))
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
        .map { it.toResponse(LocationResponse(it.location.x, it.location.y, name = it.name, countryCode = it.countryCode)) }

    fun getFilteredLocations(
        filters: Filters? = null,
        pageable: Pageable,
        kindeId: String,
        authorities: Collection<GrantedAuthority>,
        userPermissions: List<CountryCode>
    ): Page<Location> {
        val isSuperAdmin = authorities.any { it.authority == "ROLE_SUPER_ADMIN" }

        return filters?.let {
            val specification = LocationSpecification.build(it)
            val locations = locationRepository.findAll(specification, pageable).mapToLocationSimple()
            checkPermissions(isSuperAdmin, userPermissions, locations)
            locations
        } ?: run {
            val locations = locationRepository.findAll(pageable).mapToLocationSimple()
            checkPermissions(isSuperAdmin, userPermissions, locations)
            locations
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
        ).map { it.toResponse(LocationResponse(it.location.x, it.location.y, it.name, countryCode = it.countryCode)) }
    }

    fun getAllUnacceptedSurveys(page: Pageable): Page<Location> = locationRepository.findAllUnacceptedSurveys(page).mapToLocationSimple()

    fun getSimpleLocationByUser(kindeId: String): List<Location> = locationRepository.findByUser(kindeId).mapToLocationSimple()

    fun getLocationsByUser(kindeId: String): List<LocationEntity> = locationRepository.findByUser(kindeId)

    fun deleteLocation(locationEntity: LocationEntity) = locationRepository.delete(locationEntity)

    private fun Page<LocationEntity>.mapToLocationSimple(): Page<Location> = this.map { loc -> loc.toResponse(LocationResponse(loc.location.x, loc.location.y, name = loc.name, countryCode = loc.countryCode)) }
    private fun List<LocationEntity>.mapToLocationSimple(): List<Location> = this.map { loc -> loc.toResponse(LocationResponse(loc.location.x, loc.location.y, name = loc.name, countryCode = loc.countryCode)) }

    private fun checkPermissions(isSuperAdmin: Boolean, userPermissions: List<CountryCode>, locations: Page<Location>) {
        locations.forEach {
            if (!isSuperAdmin && it.countryCode !in userPermissions) {
                throw ForbiddenException("You do not have permissions to download data for this country. Ask the super administrator to assign you the necessary permissions.")
            }
        }
    }
}