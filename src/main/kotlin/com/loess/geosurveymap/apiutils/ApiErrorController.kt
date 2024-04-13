package com.loess.geosurveymap.apiutils

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/error")
@Hidden
class ApiErrorController (
    errorAttributes: ErrorAttributes,
    private val apiResponseBuilder: ApiResponseBuilder
){
}
