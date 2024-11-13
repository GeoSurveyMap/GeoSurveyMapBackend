package com.loess.geosurveymap.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    fun existsByEmail(email: String): Boolean
    fun findByKindeId(kindeId: String): UserEntity?
}