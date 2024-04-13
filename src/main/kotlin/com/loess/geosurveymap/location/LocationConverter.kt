package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.toResponse

fun LocationEntity.toResponse(): Location =
    Location(
        id = id,
        x = location.x,
        y = location.y,
        survey = survey.toResponse()
    )