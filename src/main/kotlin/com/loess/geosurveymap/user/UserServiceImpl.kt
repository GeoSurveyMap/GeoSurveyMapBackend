package com.loess.geosurveymap.user

import com.loess.geosurveymap.exceptions.ConflictException
import com.loess.geosurveymap.exceptions.NotFoundException
import com.loess.geosurveymap.location.LocationService
import com.loess.geosurveymap.survey.SurveyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val surveyRepository: SurveyRepository,
    private val locationService: LocationService
): UserService {

    override fun registerUser(user: UserRequest): Long {
        if (userRepository.existsByEmail(user.email)) {
            throw ConflictException("User with this email already exists")
        }

        return userRepository.save(user.toUser().toEntity()).id
    }

    @Transactional(readOnly = true)
    override fun findByKindeId(kindeId: String): UserEntity {
        return userRepository.findByKindeId(kindeId) ?: throw NotFoundException("User not found")
    }

    override fun updateUser(kindeId: String, permissions: List<DataPermission>): User {
        val user = findByKindeId(kindeId)
        user.apply { this.permissions = permissions.toMutableList() }.also { userRepository.save(it) }
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getUsers(pageable: Pageable, filters: UserFilters): Page<User> {
        val specification = UserSpecification.build(filters)
        return userRepository.findAll(specification, pageable).map { it.toResponse() }
    }

    override fun deleteUser(kindeId: String) {
        findByKindeId(kindeId).let { user ->
            locationService.getLocationsByUser(kindeId).forEach { loc ->
                val survey = loc.survey
                locationService.deleteLocation(loc).also {
                    surveyRepository.delete(survey)
                    userRepository.delete(user)
                }
            }
        }
    }

    override fun changeUserStatus(kindeId: String, status: UserStatus): User {
        val user = findByKindeId(kindeId).let { user ->
            user.status = status
            userRepository.save(user)
        }

        return user.toResponse()
    }
}