package com.loess.geosurveymap.user

data class User(
    val id: Long = 0,
    var kindeId: String,
    var email: String,
    val status: UserStatus = UserStatus.ACTIVE,
    val permissions: MutableList<CountryCode> = mutableListOf()
)

data class UserRequest(
    val kindeId: String,
    val email: String,
    val permissions: MutableList<CountryCode> = mutableListOf()
)