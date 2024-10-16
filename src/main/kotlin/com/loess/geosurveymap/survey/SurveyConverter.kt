package com.loess.geosurveymap.survey

import com.loess.geosurveymap.location.LocationSimple
import com.loess.geosurveymap.user.UserEntity
import com.loess.geosurveymap.user.toResponse

fun SurveyRequest.toEntity(user: UserEntity): SurveyEntity =
    SurveyEntity(
        category = category,
        description = description,
        solution = solution,
        affectedArea = affectedArea,
        user = user
    )

fun SurveyEntity.toResponse(location: LocationSimple): Survey =
    Survey(
        id = id,
        category = category,
        description = description,
        solution = solution,
        location = location,
        affectedArea = affectedArea,
        user = user.toResponse(),
        createdAt = createdAt
    )
