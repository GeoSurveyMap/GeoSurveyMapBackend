package com.loess.geosurveymap.survey

import com.loess.geosurveymap.dto.BoundingBox
import com.loess.geosurveymap.dto.Coordinates
import com.loess.geosurveymap.exceptions.ForbiddenException
import com.loess.geosurveymap.exceptions.NotFoundException
import com.loess.geosurveymap.location.*
import com.loess.geosurveymap.user.UserEntity
import com.loess.geosurveymap.user.UserService
import com.loess.geosurveymap.user.UserStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SurveyService(
    private val surveyRepository: SurveyRepository,
    private val locationService: LocationService,
    private val userService: UserService,
    private val storageService: StorageService,
) {

    @Transactional
    fun saveSurvey(surveyRequest: SurveyRequest, kindeId: String, filePath: String? = null): Survey {
        val user = validateUserStatus(kindeId)
        val survey = surveyRepository.save(surveyRequest.toEntity(user))
        val location = locationService.saveLocationForSurvey(surveyRequest.locationRequest, survey)
        filePath?.let { path -> survey.apply { this.filePath = path } }
        surveyRepository.save(survey)
        return survey.toResponse(location.toSimple())
    }

    @Transactional
    fun uploadFile(file: MultipartFile, kindeId: String): String {
        validateUserStatus(kindeId)
        val uuid = UUID.randomUUID()
        val path = "${uuid}-survey"
        file.let { storageService.uploadFile(path, file.inputStream, file.contentType!!) }
        return path
    }

    private fun validateUserStatus(kindeId: String): UserEntity {
        val user = userService.findByKindeId(kindeId)
        if (user.status == UserStatus.BANNED) {
            throw ForbiddenException("User is banned and cannot upload a new file.")
        }

        return user
    }

    fun getAllSurveys(authorities: Collection<GrantedAuthority>?): List<Survey> {
        val isNotAdmin = authorities?.none { it.authority == "ROLE_SUPER_ADMIN" || it.authority == "ROLE_ADMIN" } ?: true
        return locationService.getAllLocations().map {
            val survey = it.survey
            if (isNotAdmin) {
                // TODO: this is just a quick fix to not display this data to all users in a `/all` request.
                // Think of a new DTO or split the request into two versions - one for the admin and one for “regular” users.
                survey.id = 0L
                survey.user.id = 0L
                survey.user.email = "***"
                survey.user.kindeId = "***"
            }
            survey
        }
    }

    fun getSurveysByLocation(locationRequest: Coordinates): List<Survey> =
        locationService.getLocationByCoordinates(locationRequest).map {
            it.survey.location = LocationResponse(it.x, it.y, it.name, it.countryCode)
            it.survey
        }

    fun getAllSurveysWithinRadius(locationRequest: Coordinates, radius: Double): List<Survey> =
        locationService.getAllWithinRadius(locationRequest, radius).map {
            it.survey.location = LocationResponse(it.x, it.y, it.name, it.countryCode)
            it.survey
        }

    fun getAllSurveysWithinBoundingBox(boundingBox: BoundingBox, categories: List<Category>?): List<Survey> =
        locationService.getAllWithinBoundingBox(boundingBox, categories).map { it.survey }

    fun getAllSurveysByLocationAndRadiusAndCategories(
        locationRequest: Coordinates,
        radius: Double,
        categories: List<Category>
    ): List<Survey> =
        locationService.getByCategories(locationRequest, radius, categories).map { it.survey }

    fun getSurveysByCategories(locationRequest: Coordinates, categories: List<Category>): List<Survey> =
        locationService.getLocationBySurveyCategory(
            locationRequest,
            categories
        ).map { it.survey }

    @Transactional
    fun accept(surveyId: Long, status: SurveyStatus) {
        if (status == SurveyStatus.PENDING) {
            throw IllegalArgumentException("You can set status to Accepted or Rejected only")
        }

        surveyRepository.findByIdOrNull(surveyId)?.let {
            it.status = status
            surveyRepository.save(it)
        } ?: throw NotFoundException("Survey with given id not found")
    }

    fun getUnacceptedSurveys(page: Pageable): Page<Survey> {
        return locationService.getAllUnacceptedSurveys(page).map {
            it.survey.location = LocationResponse(it.x, it.y, it.name, it.countryCode)
            it.survey
        }
    }

    fun getUserSurveys(kindeId: String): List<Survey> {
        userService.findByKindeId(kindeId).let {
            return locationService.getSimpleLocationByUser(kindeId).map { it.survey }
        }
    }
}