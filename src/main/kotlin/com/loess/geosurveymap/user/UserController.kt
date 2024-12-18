package com.loess.geosurveymap.user

import com.loess.geosurveymap.admin.PAGEABLE_EXAMPLE
import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.apiutils.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val apiRequestHandler: ApiRequestHandler
) {

    @Operation(summary = "Register a new user")
    @PostMapping
    fun register(@RequestBody user: UserRequest) = apiRequestHandler.handle {
        userService.registerUser(user)
    }

    @Operation(summary = "Delete yours account")
    @DeleteMapping("/self")
    fun deleteAccount(@AuthenticationPrincipal jwt: Jwt) = apiRequestHandler.handle {
        val kindeId = jwt.subject ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found in token")
        userService.deleteUser(kindeId)
    }

    @Operation(summary = "Updates user's permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{kindeId}")
    fun updateUserPermissions(
        @PathVariable kindeId: String,
        @RequestParam permissions: List<DataPermission>
    ): ApiResponse<User> =
        apiRequestHandler.handle {
            userService.updateUser(kindeId, permissions)
        }

    @Operation(summary = "Filter users based on various criteria")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    fun filterUsers(
        @Parameter(description = "Page number (0-based)", example = PAGEABLE_EXAMPLE)
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC, size = 20)
        pageable: Pageable,

        @Parameter(description = "Filter by User ID")
        @RequestParam(required = false) id: Long?,

        @Parameter(description = "Filter by Kinde ID (exact match, case-insensitive)")
        @RequestParam(required = false) kindeId: String?,

        @Parameter(description = "Filter by Role")
        @RequestParam(required = false) role: Role?,

        @Parameter(description = "Filter by User email (supports partial, case-insensitive matches)")
        @RequestParam(required = false) email: String?,

        @Parameter(description = "Filter by Permissions (exact matches)")
        @RequestParam(required = false) permissions: List<DataPermission>?,

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
        @RequestParam(required = false) modifiedAtEnd: Instant?
    ): ApiResponse<List<User>> {
        val filters = buildUserFilters(
            id,
            kindeId,
            role,
            email,
            permissions,
            createdBy,
            createdAtStart,
            createdAtEnd,
            modifiedBy,
            modifiedAtStart,
            modifiedAtEnd
        )

        return apiRequestHandler.handlePage {
            userService.getUsers(pageable, filters)
        }
    }

    private fun buildUserFilters(
        id: Long?,
        kindeId: String?,
        role: Role?,
        email: String?,
        permissions: List<DataPermission>?,
        createdBy: String?,
        createdAtStart: Instant?,
        createdAtEnd: Instant?,
        modifiedBy: String?,
        modifiedAtStart: Instant?,
        modifiedAtEnd: Instant?
    ): UserFilters {
        return UserFilters(
            id = id,
            kindeId = kindeId,
            role = role,
            email = email,
            permissions = permissions,
            createdBy = createdBy,
            createdAtStart = createdAtStart,
            createdAtEnd = createdAtEnd,
            modifiedBy = modifiedBy,
            modifiedAtStart = modifiedAtStart,
            modifiedAtEnd = modifiedAtEnd
        )
    }

}