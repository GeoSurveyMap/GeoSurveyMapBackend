package com.loess.geosurveymap.user

import com.loess.geosurveymap.auditable.Auditable
import com.loess.geosurveymap.survey.SurveyEntity
import jakarta.persistence.*
import org.hibernate.annotations.NaturalId
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "app_user")
class UserEntity(
    @Id
    @GeneratedValue(generator = "app_user_seq")
    @SequenceGenerator(name = "app_user_seq", allocationSize = 1)
    val id: Long = 0,

    @NaturalId
    val kindeId: String,

    @Enumerated(value = EnumType.STRING)
    val role: Role = Role.ROLE_USER,
    val email: String
) : Auditable() {

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as SurveyEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode()
        else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   kindeId = $kindeId   ,   role = $role   ,   email = $email )"
    }


}