package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.toResponse

fun LocationEntity.toResponse(location: LocationSimple): Location =
    Location(
        id = id,
        x = location.x,
        y = location.y,
        survey = survey.toResponse(location)
    )

fun Location.toSimple(): LocationSimple =
    LocationSimple(
        x = x,
        y = y,
    )