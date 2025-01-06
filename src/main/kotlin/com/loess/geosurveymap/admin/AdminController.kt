package com.loess.geosurveymap.admin

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import com.loess.geosurveymap.config.CustomJwtGrantedAuthoritiesConverter
import com.loess.geosurveymap.location.Filters
import com.loess.geosurveymap.location.Location
import com.loess.geosurveymap.location.LocationService
import com.loess.geosurveymap.survey.Category
import com.loess.geosurveymap.survey.Survey
import com.loess.geosurveymap.survey.SurveyService
import com.loess.geosurveymap.survey.SurveyStatus
import com.loess.geosurveymap.user.User
import com.loess.geosurveymap.user.UserService
import com.loess.geosurveymap.user.UserStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.core.io.Resource
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
const val PAGEABLE_EXAMPLE = """
            {
              "page": 0,
              "size": 20,
              "sort": ["createdAt"]
            }
        """

@RestController("/api/v1/admin")
class AdminController(
    private val excelReportService: ReportService,
    private val locationService: LocationService,
    private val apiRequestHandler: ApiRequestHandler,
    private val surveyService: SurveyService,
    private val userService: UserService,
) {

    @Operation(summary = "Get report from collected data in xlsx format")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/report")
    fun downloadReport(
        @Parameter(description = "Page number (0-based)", example = PAGEABLE_EXAMPLE)
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC, size = 20)
        pageable: Pageable,

        @Parameter(description = "Filter by Location ID")
        @RequestParam(required = false) id: Long?,

        @Parameter(description = "Filter by Location name (supports partial, case-insensitive matches)")
        @RequestParam(required = false) name: String?,

        @Parameter(description = "Filter by Survey ID")
        @RequestParam(required = false) surveyId: Long?,

        @Parameter(description = "Filter by Survey category")
        @RequestParam(required = false) category: Category?,

        @Parameter(description = "Filter by Survey description (supports partial, case-insensitive matches)")
        @RequestParam(required = false) description: String?,

        @Parameter(description = "Filter by Survey solution (supports partial, case-insensitive matches)")
        @RequestParam(required = false) solution: String?,

        @Parameter(description = "Minimum affected area radius for filtering", example = "5.0")
        @RequestParam(required = false) affectedAreaMin: Double?,

        @Parameter(description = "Maximum affected area radius for filtering", example = "15.0")
        @RequestParam(required = false) affectedAreaMax: Double?,

        @Parameter(description = "Filter by User ID")
        @RequestParam(required = false) userId: Long?,

        @Parameter(description = "Filter by User Kinde ID (exact match, case-insensitive)")
        @RequestParam(required = false) kindeId: String?,

        @Parameter(description = "Filter by User email (supports partial, case-insensitive matches)")
        @RequestParam(required = false) email: String?,

        @Parameter(description = "Filter by the creator's username (supports partial, case-insensitive matches)")
        @RequestParam(required = false) createdBy: String?,

        @Parameter(description = "Filter by creation date (start range)", example = "2024-04-01T12:00:00Z")
        @RequestParam(required = false) createdAtStart: Instant?,

        @Parameter(description = "Filter by creation date (end range)", example = "2024-04-30T12:00:00Z")
        @RequestParam(required = false) createdAtEnd: Instant?,

        @Parameter(description = "Filter by the modifier's username (supports partial, case-insensitive matches)")
        @RequestParam(required = false) modifiedBy: String?,

        @Parameter(description = "Filter by modification date (start range)", example = "2024-04-02T15:30:00Z")
        @RequestParam(required = false) modifiedAtStart: Instant?,

        @Parameter(description = "Filter by modification date (end range)", example = "2024-04-30T15:30:00Z")
        @RequestParam(required = false) modifiedAtEnd: Instant?,

        @Parameter(description = "X coordinate (longitude) for spatial filtering - works only if centralY and radiusInMeters are filled too", example = "12.34")
        @RequestParam(required = false) centralX: Double?,

        @Parameter(description = "Y coordinate (latitude) for spatial filtering - works only if centralX and radiusInMeters are filled too", example = "56.78")
        @RequestParam(required = false) centralY: Double?,

        @Parameter(description = "Radius in meters for spatial filtering - works only if centralX and centralY are filled too", example = "1000.0")
        @RequestParam(required = false) radiusInMeters: Double?
    ): ResponseEntity<Resource> {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwt = authentication.principal as Jwt
        val kindeId = jwt.subject ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found in token")

        val authorities: Collection<GrantedAuthority> = authentication.authorities

        val filters = buildFilter(
            id,
            name,
            surveyId,
            category,
            description,
            solution,
            affectedAreaMin,
            affectedAreaMax,
            userId,
            kindeId,
            email,
            createdBy,
            createdAtStart,
            createdAtEnd,
            modifiedBy,
            modifiedAtStart,
            modifiedAtEnd,
            centralX,
            centralY,
            radiusInMeters
        )

        val date = Instant.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm").withZone(ZoneId.of("UTC"))
        val formattedDate = formatter.format(date)
        val fileName = "survey-$formattedDate.xlsx"

        return apiRequestHandler.handleResource(EXCEL_TYPE, fileName) {
            excelReportService.generateSurveyExcelReport(filters, pageable, kindeId, authorities)
        }
    }

    @Operation(summary = "Filter collected data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/data/filter")
    fun filterData(
        @Parameter(description = "Page number (0-based)", example = PAGEABLE_EXAMPLE)
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC, size = 20)
        pageable: Pageable,

        @Parameter(description = "Filter by Location ID")
        @RequestParam(required = false) id: Long?,

        @Parameter(description = "Filter by Location name (supports partial, case-insensitive matches)")
        @RequestParam(required = false) name: String?,

        @Parameter(description = "Filter by Survey ID")
        @RequestParam(required = false) surveyId: Long?,

        @Parameter(description = "Filter by Survey category")
        @RequestParam(required = false) category: Category?,

        @Parameter(description = "Filter by Survey description (supports partial, case-insensitive matches)")
        @RequestParam(required = false) description: String?,

        @Parameter(description = "Filter by Survey solution (supports partial, case-insensitive matches)")
        @RequestParam(required = false) solution: String?,

        @Parameter(description = "Minimum affected area radius for filtering", example = "5.0")
        @RequestParam(required = false) affectedAreaMin: Double?,

        @Parameter(description = "Maximum affected area radius for filtering", example = "15.0")
        @RequestParam(required = false) affectedAreaMax: Double?,

        @Parameter(description = "Filter by User ID")
        @RequestParam(required = false) userId: Long?,

        @Parameter(description = "Filter by User Kinde ID (exact match, case-insensitive)")
        @RequestParam(required = false) kindeId: String?,

        @Parameter(description = "Filter by User email (supports partial, case-insensitive matches)")
        @RequestParam(required = false) email: String?,

        @Parameter(description = "Filter by the creator's username (supports partial, case-insensitive matches)")
        @RequestParam(required = false) createdBy: String?,

        @Parameter(description = "Filter by creation date (start range)", example = "2024-04-01T12:00:00Z")
        @RequestParam(required = false) createdAtStart: Instant?,

        @Parameter(description = "Filter by creation date (end range)", example = "2024-04-30T12:00:00Z")
        @RequestParam(required = false) createdAtEnd: Instant?,

        @Parameter(description = "Filter by the modifier's username (supports partial, case-insensitive matches)")
        @RequestParam(required = false) modifiedBy: String?,

        @Parameter(description = "Filter by modification date (start range)", example = "2024-04-02T15:30:00Z")
        @RequestParam(required = false) modifiedAtStart: Instant?,

        @Parameter(description = "Filter by modification date (end range)", example = "2024-04-30T15:30:00Z")
        @RequestParam(required = false) modifiedAtEnd: Instant?,

        @Parameter(description = "X coordinate (longitude) for spatial filtering - works only if centralY and radiusInMeters are filled too", example = "12.34")
        @RequestParam(required = false) centralX: Double?,

        @Parameter(description = "Y coordinate (latitude) for spatial filtering - works only if centralX and radiusInMeters are filled too", example = "56.78")
        @RequestParam(required = false) centralY: Double?,

        @Parameter(description = "Radius in meters for spatial filtering - works only if centralX and centralY are filled too", example = "1000.0")
        @RequestParam(required = false) radiusInMeters: Double?
    ): ApiResponse<List<Location>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwt = authentication.principal as Jwt
        val kindeId = jwt.subject ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found in token")

        val authorities: Collection<GrantedAuthority> = authentication.authorities
        val userPermissions = userService.findByKindeId(kindeId).permissions

        val filters = buildFilter(
            id,
            name,
            surveyId,
            category,
            description,
            solution,
            affectedAreaMin,
            affectedAreaMax,
            userId,
            kindeId,
            email,
            createdBy,
            createdAtStart,
            createdAtEnd,
            modifiedBy,
            modifiedAtStart,
            modifiedAtEnd,
            centralX,
            centralY,
            radiusInMeters
        )


        return apiRequestHandler.handlePage {
            locationService.getFilteredLocations(filters, pageable, kindeId, authorities, userPermissions)
        }
    }

    private fun buildFilter(
        id: Long?,
        name: String?,
        surveyId: Long?,
        category: Category?,
        description: String?,
        solution: String?,
        affectedAreaMin: Double?,
        affectedAreaMax: Double?,
        userId: Long?,
        kindeId: String?,
        email: String?,
        createdBy: String?,
        createdAtStart: Instant?,
        createdAtEnd: Instant?,
        modifiedBy: String?,
        modifiedAtStart: Instant?,
        modifiedAtEnd: Instant?,
        centralX: Double?,
        centralY: Double?,
        radiusInMeters: Double?
    ): Filters {
        val filters = Filters(
            id = id,
            name = name,
            surveyId = surveyId,
            category = category,
            description = description,
            solution = solution,
            affectedAreaMin = affectedAreaMin,
            affectedAreaMax = affectedAreaMax,
            userId = userId,
            kindeId = kindeId,
            email = email,
            createdBy = createdBy,
            createdAtStart = createdAtStart,
            createdAtEnd = createdAtEnd,
            modifiedBy = modifiedBy,
            modifiedAtStart = modifiedAtStart,
            modifiedAtEnd = modifiedAtEnd,
            centralX = centralX,
            centralY = centralY,
            radiusInMeters = radiusInMeters
        )
        return filters
    }

    @Operation(summary = "Accept/Reject survey")
    @PutMapping("/{surveyId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    fun acceptSurvey(@PathVariable surveyId: Long, @PathVariable status: SurveyStatus) =
        apiRequestHandler.handle {
            surveyService.accept(surveyId, status)
        }

    @Operation(summary = "Get unaccepted surveys")
    @GetMapping("/surveys/unaccepted")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    fun getUnacceptedSurveys(
        @Parameter(description = "Page number (0-based)", example = PAGEABLE_EXAMPLE)
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC, size = 20)
        pageable: Pageable,
    ): ApiResponse<List<Survey>> =
        apiRequestHandler.handlePage {
            surveyService.getUnacceptedSurveys(pageable)
        }

    @Operation(summary = "Delete user and all surveys he created")
    @DeleteMapping("/users/{kindeId}/delete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    fun deleteUser(@PathVariable kindeId: String) = apiRequestHandler.handle {
        userService.deleteUser(kindeId)
    }

    @Operation(summary = "Ban/reactivate user account")
    @DeleteMapping("/users/{kindeId}/status/{userStatus}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    fun changeUserStatus(
        @PathVariable kindeId: String,
        @PathVariable userStatus: UserStatus
    ): ApiResponse<User> =
        apiRequestHandler.handle {
            userService.changeUserStatus(kindeId, userStatus)
        }
}