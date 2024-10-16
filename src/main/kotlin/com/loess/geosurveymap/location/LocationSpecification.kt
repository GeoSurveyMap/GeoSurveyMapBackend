package com.loess.geosurveymap.location

import com.loess.geosurveymap.survey.SurveyEntity
import com.loess.geosurveymap.user.UserEntity
import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification
import org.locationtech.jts.geom.Point

class LocationSpecification(private val filter: Filters) : Specification<LocationEntity> {

    override fun toPredicate(
        root: Root<LocationEntity>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {

        val predicates = mutableListOf<Predicate>()

        filter.id?.let {
            predicates.add(criteriaBuilder.equal(root.get<Long>("id"), it))
        }

        filter.name?.let {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%${it.lowercase()}%"
                )
            )
        }

        val surveyJoin = root.join<SurveyEntity, SurveyEntity>("survey", JoinType.LEFT)

        filter.surveyId?.let {
            predicates.add(criteriaBuilder.equal(surveyJoin.get<Long>("id"), it))
        }

        filter.category?.let {
            predicates.add(criteriaBuilder.equal(surveyJoin.get<Enum<*>>("category"), it))
        }

        filter.description?.let {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(surveyJoin.get("description")),
                    "%${it.lowercase()}%"
                )
            )
        }

        filter.solution?.let {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(surveyJoin.get("solution")),
                    "%${it.lowercase()}%"
                )
            )
        }

        filter.affectedAreaMin?.let {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(surveyJoin.get("affectedArea"), it))
        }
        filter.affectedAreaMax?.let {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(surveyJoin.get("affectedArea"), it))
        }

        val userJoin = surveyJoin.join<UserEntity, UserEntity>("user", JoinType.LEFT)

        filter.userId?.let {
            predicates.add(criteriaBuilder.equal(userJoin.get<Long>("id"), it))
        }

        filter.kindeId?.let {
            predicates.add(
                criteriaBuilder.equal(
                    criteriaBuilder.lower(userJoin.get("kindeId")),
                    it.lowercase()
                )
            )
        }

        filter.email?.let {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(userJoin.get("email")),
                    "%${it.lowercase()}%"
                )
            )
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

        if (filter.centralX != null && filter.centralY != null && filter.radiusInMeters != null) {
            val stMakePoint = criteriaBuilder.function(
                "ST_MakePoint",
                Point::class.java,
                criteriaBuilder.literal(filter.centralX),
                criteriaBuilder.literal(filter.centralY)
            )

            val stMakePointWithSrid = criteriaBuilder.function(
                "ST_SetSRID",
                Point::class.java,
                stMakePoint,
                criteriaBuilder.literal(4326)
            )

            val stDWithin = criteriaBuilder.function(
                "ST_DWithin",
                Boolean::class.java,
                root.get<Point>("location"),
                stMakePointWithSrid,
                criteriaBuilder.literal(filter.radiusInMeters)
            )

            predicates.add(criteriaBuilder.isTrue(stDWithin))
        }


        return if (predicates.isEmpty()) null else criteriaBuilder.and(*predicates.toTypedArray())
    }

    companion object {
        fun build(filters: Filters): Specification<LocationEntity> {
            return LocationSpecification(filters)
        }
    }
}
