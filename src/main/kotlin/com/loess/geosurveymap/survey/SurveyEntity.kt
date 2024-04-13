package com.loess.geosurveymap.survey

import com.loess.geosurveymap.auditable.Auditable
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "survey")
class SurveyEntity(

    @Id
    @GeneratedValue(generator = "survey_seq")
    @SequenceGenerator(name = "survey_seq", allocationSize = 1)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    val category: Category,
    val description: String,
    val solution: String,
) : Auditable() {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as SurveyEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode()
        else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , category = $category , description = $description , solution = $solution )"
    }

}