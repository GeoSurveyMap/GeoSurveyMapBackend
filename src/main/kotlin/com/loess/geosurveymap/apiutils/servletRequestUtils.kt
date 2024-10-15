package com.loess.geosurveymap.apiutils

import jakarta.servlet.ServletRequest
import java.time.Instant

private const val processingStart = "processingStart"

fun ServletRequest.setProcessingStart(instant: Instant) =
    this.setAttribute(processingStart, instant)

fun ServletRequest.getProcessingStart(): Instant =
    this.getAttribute(processingStart) as Instant
