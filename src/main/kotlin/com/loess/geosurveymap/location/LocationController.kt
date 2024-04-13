package com.loess.geosurveymap.location

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/locations")
class LocationController(
    private val locationService: LocationService,
    private val apiRequestHandler: ApiRequestHandler
) {

    @PostMapping
    fun createLocation(@RequestBody locationDto: LocationRequest): ApiResponse<Location> =
        apiRequestHandler.handle {
            locationService.saveLocation(locationDto.x, locationDto.y)
        }
}

