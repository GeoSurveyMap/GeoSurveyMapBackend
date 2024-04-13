package com.loess.geosurveymap.location

import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeoConfig {
    @Bean
    fun geometryFactory(): GeometryFactory {
        return GeometryFactory(
            PrecisionModel(PrecisionModel.FLOATING),
            4326
        )
    }
}