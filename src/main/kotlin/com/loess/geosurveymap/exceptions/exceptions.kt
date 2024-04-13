package com.loess.geosurveymap.exceptions

class NotReadyYetException(override val message: String) : RuntimeException(message)
class HttpClientException(override val message: String) : RuntimeException(message)
