package com.loess.geosurveymap.survey

fun SurveyRequest.toEntity(): SurveyEntity =
    SurveyEntity(
        category = category,
        description = description,
        solution = solution
    )

fun SurveyEntity.toResponse(): Survey =
    Survey(
        id = id,
        category = category,
        description = description,
        solution = solution
    )

fun Survey.toEntity(): SurveyEntity =
    SurveyEntity(
        id = id ?: 0,
        category = category,
        description = description,
        solution = solution
    )