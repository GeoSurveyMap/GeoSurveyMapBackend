package com.loess.geosurveymap.user

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val apiRequestHandler: ApiRequestHandler
) {

    @PostMapping
    fun register(@RequestBody user: User) = apiRequestHandler.handle {
        userService.registerUser(user)
    }

}