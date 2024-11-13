package com.loess.geosurveymap.user

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.time.Instant

data class UserFilters(
    val id: Long? = null,
    val kindeId: String? = null,
    val role: Role? = null,
    val email: String? = null,
    val permissions: List<DataPermission>? = null,
    val createdBy: String? = null,
    val createdAtStart: Instant? = null,
    val createdAtEnd: Instant? = null,
    val modifiedBy: String? = null,
    val modifiedAtStart: Instant? = null,
    val modifiedAtEnd: Instant? = null
)

class UserSpecification(private val filter: UserFilters) : Specification<UserEntity> {

    override fun toPredicate(
        root: Root<UserEntity>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {

        val predicates = mutableListOf<Predicate>()

        filter.id?.let {
            predicates.add(criteriaBuilder.equal(root.get<Long>("id"), it))
        }

        filter.kindeId?.let {
            predicates.add(
                criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("kindeId")),
                    it.lowercase()
                )
            )
        }

        filter.role?.let {
            predicates.add(criteriaBuilder.equal(root.get<Role>("role"), it))
        }

        filter.email?.let {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%${it.lowercase()}%"
                )
            )
        }

        filter.permissions?.let { perms ->
            perms.forEach { perm ->
                predicates.add(
                    criteriaBuilder.isMember(perm, root.get<Set<DataPermission>>("permissions"))
                )
            }
        }

        filter.createdBy?.let {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("createdBy")),
                    "%${it.lowercase()}%"
                )
            )
        }

        filter.createdAtStart?.let {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), it))
        }

        filter.createdAtEnd?.let {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), it))
        }

        filter.modifiedBy?.let {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("modifiedBy")),
                    "%${it.lowercase()}%"
                )
            )
        }

        filter.modifiedAtStart?.let {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("modifiedAt"), it))
        }

        filter.modifiedAtEnd?.let {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("modifiedAt"), it))
        }

        return if (predicates.isEmpty()) {
            null
        } else {
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    companion object {
        fun build(filters: UserFilters): Specification<UserEntity> {
            return UserSpecification(filters)
        }
    }
}