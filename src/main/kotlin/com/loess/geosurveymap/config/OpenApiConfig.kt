package com.loess.geosurveymap.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server

const val LOCALHOST = "http://localhost:8080"
const val PRODUCTION_SERVER = "http://ec2-3-75-215-88.eu-central-1.compute.amazonaws.com:8080"

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val serverList = listOf(LOCALHOST, PRODUCTION_SERVER).map { url -> Server().url(url) }

        return OpenAPI()
            .components(
                Components().addSecuritySchemes(
                    "bearerAuth",
                    SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                )
            )
            .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
            .servers(serverList)
    }
}
