package com.loess.geosurveymap.kinde

import com.kinde.KindeClient
import com.kinde.KindeClientBuilder
import com.kinde.spring.sdk.KindeSdkClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

data class Header(
    val header: String,
    val value: String
)

@Configuration
class KindeConfig(
    @Value("\${kinde.oauth2.client-id}") private val clientId: String,
    @Value("\${kinde.oauth2.client-secret}") private val secret: String,
    @Value("\${kinde.oauth2.domain}") private val domain: String,
    @Value("\${kinde.oauth2.audience}") private val audience: String,
) {

    @Bean
    fun kindeSdkClient(): KindeSdkClient {
        return KindeSdkClient()
    }

    @Bean
    fun kindeClient(): KindeClient {
        val kindeClient = KindeClientBuilder
            .builder()
            .clientId(clientId)
            .clientSecret(secret)
            .domain(domain)
            .audience(audience)
            .build()

        return kindeClient
    }

    @Bean
    fun restClient(): RestClient {
        val kindeClient = kindeClient()
        val kindeClientSession = kindeClient.clientSession()
        val tokens = kindeClientSession.retrieveTokens()
        val authorizationHeader = Header("Authorization", "Bearer ${tokens.accessToken.token()}")
        val contentTypeJsonHeader = Header("Content-Type", "application/json")
        val baseUrl = kindeClient.kindeConfig().domain() + "/api/v1/user"

        return RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(authorizationHeader.header, authorizationHeader.value)
            .defaultHeader(contentTypeJsonHeader.header, contentTypeJsonHeader.value)
            .build()
    }
}