package com.loess.geosurveymap.survey

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SurveyRepository : JpaRepository<SurveyEntity, Long> {

    @Query(
        value = """
            SELECT s.* 
            FROM location l, survey s
            JOIN public.location l2 on s.id = l2.survey_id
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
    ): List<SurveyEntity>

}