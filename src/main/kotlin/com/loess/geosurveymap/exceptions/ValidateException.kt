package com.loess.geosurveymap.exceptions

import com.loess.geosurveymap.apiutils.dto.ApiResponseErrorElement

open class ValidateException(
    override val message: String,
    open val errors: List<ApiResponseErrorElement> = emptyList()
): RuntimeException()
