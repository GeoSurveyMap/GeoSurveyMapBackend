package com.loess.geosurveymap.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {
    fun registerUser(user: UserRequest): Long
    fun findByKindeId(kindeId: String): UserEntity
    fun updateUser(kindeId: String, permissions: List<CountryCode>): User
    fun getUsers(pageable: Pageable, filters: UserFilters): Page<User>
    fun deleteUser(kindeId: String)
    fun changeUserStatus(kindeId: String, status: UserStatus): User
}