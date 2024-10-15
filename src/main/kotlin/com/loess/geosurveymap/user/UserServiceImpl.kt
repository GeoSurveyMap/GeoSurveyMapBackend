package com.loess.geosurveymap.user

import com.loess.geosurveymap.exceptions.ConflictException
import com.loess.geosurveymap.exceptions.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository
): UserService {

    override fun registerUser(user: User): Long {
        if (userRepository.existsByEmail(user.email)) {
            throw ConflictException("User with this email already exists")
        }

        return userRepository.save(user.toEntity()).id
    }

    @Transactional(readOnly = true)
    override fun findByKindeId(kindeId: String): UserEntity {
        return userRepository.findByKindeId(kindeId) ?: throw NotFoundException("User not found")
    }
}