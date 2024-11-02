package com.loess.geosurveymap.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class CustomJwtGrantedAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        val rolesList = jwt.getClaim<List<Map<String, Any>>>("roles")
        if (rolesList != null) {
            for (role in rolesList) {
                val roleKey = role["key"] as? String
                if (roleKey != null) {
                    authorities.add(SimpleGrantedAuthority("ROLE_$roleKey"))
                }
            }
        }

        return authorities
    }
}