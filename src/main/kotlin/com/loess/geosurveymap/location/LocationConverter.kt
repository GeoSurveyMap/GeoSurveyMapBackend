package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.toResponse

fun LocationEntity.toResponse(location: LocationResponse): Location =
    Location(
        id = id,
        x = location.x,
        y = location.y,
        survey = survey.toResponse(location),
        name = name,
        countryCode = countryCode
    )

fun Location.toSimple(): LocationResponse =
    LocationResponse(
        x = x,
        y = y,
        name = name,
        countryCode = countryCode
    )