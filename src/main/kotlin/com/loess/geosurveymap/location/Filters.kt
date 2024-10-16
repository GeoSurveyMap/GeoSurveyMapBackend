package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.Category

import java.time.Instant

data class Filters(
    val id: Long? = null,
    val name: String? = null,
    val surveyId: Long? = null,
    val category: Category? = null,
    val description: String? = null,
    val solution: String? = null,
    val affectedAreaMin: Double? = null,
    val affectedAreaMax: Double? = null,
    val userId: Long? = null,
    val kindeId: String? = null,
    val email: String? = null,
    val createdBy: String? = null,
    val createdAtStart: Instant? = null,
    val createdAtEnd: Instant? = null,
    val modifiedBy: String? = null,
    val modifiedAtStart: Instant? = null,
    val modifiedAtEnd: Instant? = null,
    val centralX: Double? = null,     // For spatial filtering
    val centralY: Double? = null,     // For spatial filtering
    val radiusInMeters: Double? = null // For spatial filtering
)

