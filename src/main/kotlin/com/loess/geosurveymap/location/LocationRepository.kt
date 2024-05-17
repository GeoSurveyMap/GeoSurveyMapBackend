package com.loess.geosurveymap.location

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<LocationEntity, Long> {
    fun findByLocation(location: Point): LocationEntity?

    @Query(
        value = """
           SELECT *
           FROM location l
           WHERE ST_DWithin(
             cast(l.location as geography),
             ST_SetSRID(ST_Point(:x, :y), 4326),
             :radius
           )
        """,
        nativeQuery = true
    )
    fun findAllWithinRadius(
        @Param("x") x: Double,
        @Param("y") y: Double,
        @Param("radius") radius: Double // Radius in meters
    ): List<LocationEntity>

    @Query(
        value = """
           SELECT *
           FROM location l
           WHERE ST_Within(
             l.location,
             ST_MakeEnvelope(:minX, :minY, :maxX, :maxY, 4326)
           )
        """,
        nativeQuery = true
    )
    fun findAllWithinBoundingBox(
        @Param("minX") minX: Double,
        @Param("minY") minY: Double,
        @Param("maxX") maxX: Double,
        @Param("maxY") maxY: Double
    ): List<LocationEntity>
}