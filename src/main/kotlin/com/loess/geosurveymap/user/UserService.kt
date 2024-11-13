package com.loess.geosurveymap.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {
    fun registerUser(user: User): Long
    fun findByKindeId(kindeId: String): UserEntity
    fun updateUser(kindeId: String, permissions: List<DataPermission>): User
    fun getUsers(pageable: Pageable, filters: UserFilters): Page<User>
}