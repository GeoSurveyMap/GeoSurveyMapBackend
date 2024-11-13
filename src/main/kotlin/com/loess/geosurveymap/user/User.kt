package com.loess.geosurveymap.user

data class User(
    val id: Long = 0,
    val kindeId: String,
    val email: String,
    val role: Role,
    val permissions: MutableList<DataPermission> = mutableListOf()
)
