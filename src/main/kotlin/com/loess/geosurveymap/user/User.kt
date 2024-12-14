package com.loess.geosurveymap.user

data class User(
    val id: Long = 0,
    val kindeId: String,
    val email: String,
    val status: UserStatus = UserStatus.ACTIVE,
    val permissions: MutableList<DataPermission> = mutableListOf()
)

data class UserRequest(
    val kindeId: String,
    val email: String,
    val permissions: MutableList<DataPermission> = mutableListOf()
)