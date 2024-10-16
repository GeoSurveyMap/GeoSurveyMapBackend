package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.Category
import io.swagger.v3.oas.annotations.media.Schema

import java.time.Instant

@Schema(description = "Filter criteria for generating survey reports and filtering data")
data class Filters(
    @Schema(description = "Filter by Location ID", example = "123")
    val id: Long? = null,

    @Schema(description = "Filter by Location name (supports partial, case-insensitive matches)", example = "Central")
    val name: String? = null,

    @Schema(description = "Filter by Survey ID", example = "456")
    val surveyId: Long? = null,

    @Schema(description = "Filter by Survey category", example = "TECHNOLOGY")
    val category: Category? = null,

    @Schema(description = "Filter by Survey description (supports partial, case-insensitive matches)", example = "Improve infrastructure")
    val description: String? = null,

    @Schema(description = "Filter by Survey solution (supports partial, case-insensitive matches)", example = "Upgrade servers")
    val solution: String? = null,

    @Schema(description = "Minimum affected area radius for filtering", example = "5.0")
    val affectedAreaMin: Double? = null,

    @Schema(description = "Maximum affected area radius for filtering", example = "15.0")
    val affectedAreaMax: Double? = null,

    @Schema(description = "Filter by User ID", example = "789")
    val userId: Long? = null,

    @Schema(description = "Filter by User Kinde ID (exact match, case-insensitive)", example = "kinde123")
    val kindeId: String? = null,

    @Schema(description = "Filter by User email (supports partial, case-insensitive matches)", example = "user@example.com")
    val email: String? = null,

    @Schema(description = "Filter by the creator's username (supports partial, case-insensitive matches)", example = "admin")
    val createdBy: String? = null,

    @Schema(description = "Filter by creation date (start range)", example = "2024-04-01T12:00:00Z")
    val createdAtStart: Instant? = null,

    @Schema(description = "Filter by creation date (end range)", example = "2024-04-30T12:00:00Z")
    val createdAtEnd: Instant? = null,

    @Schema(description = "Filter by the modifier's username (supports partial, case-insensitive matches)", example = "editor")
    val modifiedBy: String? = null,

    @Schema(description = "Filter by modification date (start range)", example = "2024-04-02T15:30:00Z")
    val modifiedAtStart: Instant? = null,

    @Schema(description = "Filter by modification date (end range)", example = "2024-04-30T15:30:00Z")
    val modifiedAtEnd: Instant? = null,

    @Schema(description = "X coordinate (longitude) for spatial filtering - works only if centralY and radiusInMeters are filled too", example = "12.34")
    val centralX: Double? = null,

    @Schema(description = "Y coordinate (latitude) for spatial filtering - works only if centralX and radiusInMeters are filled too", example = "56.78")
    val centralY: Double? = null,

    @Schema(description = "Radius in meters for spatial filtering - works only if centralX and centralY are filled too", example = "1000.0")
    val radiusInMeters: Double? = null
)

