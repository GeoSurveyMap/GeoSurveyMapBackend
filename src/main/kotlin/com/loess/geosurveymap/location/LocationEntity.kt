package com.loess.geosurveymap.location

import com.loess.geosurveymap.auditable.Auditable
import com.loess.geosurveymap.survey.SurveyEntity
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "location")
class LocationEntity(
    @Id
    @GeneratedValue(generator = "location_seq")
    @SequenceGenerator(name = "location_seq", allocationSize = 1)
    val id: Long = 0,

    @Column(columnDefinition = "geometry(Point,4326)")
    val location: Point,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    val survey: SurveyEntity
) : Auditable() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as LocationEntity

        return id == other.id
    }

    override fun hashCode(): Int =
        if (this is HibernateProxy)
            this.hibernateLazyInitializer.persistentClass.hashCode()
        else
            javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , location = $location , survey = $survey )"
    }


}
