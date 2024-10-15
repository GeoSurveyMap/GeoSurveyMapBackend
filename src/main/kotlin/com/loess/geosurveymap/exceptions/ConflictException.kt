package com.loess.geosurveymap.exceptions

import com.loess.geosurveymap.apiutils.dto.ApiResponseErrorElement

class ConflictException(
    override val message: String,
    override val errors: List<ApiResponseErrorElement> = emptyList()
) : ApiException(message, errors)
