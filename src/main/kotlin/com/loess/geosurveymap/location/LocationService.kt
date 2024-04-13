package com.loess.geosurveymap.location

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val geometryFactory: GeometryFactory
) {

    @Transactional
    fun saveLocation(x: Double, y: Double): Location {
        val point = geometryFactory.createPoint(Coordinate(x, y))
        val locationEntity = LocationEntity(location = point)
        return locationRepository.save(locationEntity).toResponse()
    }
}