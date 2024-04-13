package com.loess.geosurveymap.location

fun LocationEntity.toResponse(): Location =
    Location(
        id = id,
        x = location.x,
        y = location.y
    )