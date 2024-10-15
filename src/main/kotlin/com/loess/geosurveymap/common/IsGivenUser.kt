package com.loess.geosurveymap.common

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("authentication.name.toString().equals(#kindeId.toString())")
annotation class IsGivenUser
