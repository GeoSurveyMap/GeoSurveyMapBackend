package com.loess.geosurveymap.survey

import com.loess.geosurveymap.location.LocationSimple

fun SurveyRequest.toEntity(): SurveyEntity =
    SurveyEntity(
        category = category,
        description = description,
        solution = solution,
        affectedArea = affectedArea,
    )

fun SurveyEntity.toResponse(location: LocationSimple): Survey =
    Survey(
        id = id,
        category = category,
        description = description,
        solution = solution,
        location = location,
        affectedArea = affectedArea,
    )

fun Survey.toEntity(): SurveyEntity =
    SurveyEntity(
        id = id ?: 0,
        category = category,
        description = description,
        solution = solution,
        affectedArea = affectedArea
    )