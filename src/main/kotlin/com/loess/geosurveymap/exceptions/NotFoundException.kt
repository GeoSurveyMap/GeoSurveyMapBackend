package com.loess.geosurveymap.exceptions

import com.loess.geosurveymap.apiutils.dto.ApiResponseErrorElement

class NotFoundException(
    override val message: String,
    override val errors: List<ApiResponseErrorElement> = mutableListOf()
) : ApiException(message, errors)
