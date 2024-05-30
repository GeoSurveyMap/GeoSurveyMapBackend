package com.loess.geosurveymap.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
class SecurityConfig(
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val issuer: String
) {

    @Bean
    @Override
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService {
            throw UnsupportedOperationException("unsupported");
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/survey/create").authenticated()
                it.anyRequest().permitAll()
            }
            .oauth2ResourceServer {
                it.jwt { jwt ->
                    jwt.decoder(JwtDecoders.fromIssuerLocation(issuer))
                }
            }
            .build()
    }


}