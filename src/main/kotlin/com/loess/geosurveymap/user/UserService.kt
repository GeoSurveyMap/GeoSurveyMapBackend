package com.loess.geosurveymap.user

interface UserService {
    fun registerUser(user: User): Long
    fun findByKindeId(kindeId: String): UserEntity
}