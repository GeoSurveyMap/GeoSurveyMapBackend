package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.Category
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<LocationEntity, Long>, JpaSpecificationExecutor<LocationEntity> {
    fun findByLocation(location: Point): List<LocationEntity>
    fun findByLocationAndSurvey_CategoryIn(location: Point, categories: List<Category>): List<LocationEntity>

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
       SELECT l.*
       FROM location l
       JOIN public.survey s ON s.id = l.survey_id
       WHERE ST_Within(
         l.location,
         ST_MakeEnvelope(:minX, :minY, :maxX, :maxY, 4326)
       )
       AND (:categories IS NULL OR s.category = ANY (string_to_array(:categories, ',')))
    """,
        nativeQuery = true
    )
    fun findAllWithinBoundingBox(
        @Param("minX") minX: Double,
        @Param("minY") minY: Double,
        @Param("maxX") maxX: Double,
        @Param("maxY") maxY: Double,
        @Param("categories") categories: String?
    ): List<LocationEntity>

    @Query(
        value = """
            SELECT l.* 
            FROM location l
            JOIN public.survey s ON s.id = l.survey_id
            WHERE ST_DWithin(
                 cast(l.location as geography),
                 ST_SetSRID(ST_Point(:x, :y), 4326),
                 :radius
            )
            AND s.category = ANY (string_to_array(:categories, ','))
        """,
        nativeQuery = true
    )
    fun findSurveysByLocationAndCategories(
        @Param("x") x: Double,
        @Param("y") y: Double,
        @Param("radius") radius: Double,
        @Param("categories") categories: String
    ): List<LocationEntity>
}