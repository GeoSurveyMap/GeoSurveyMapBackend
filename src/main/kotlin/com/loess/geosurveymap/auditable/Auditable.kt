package com.loess.geosurveymap.auditable

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable {
    @Column(name = "created_by")
    @CreatedBy
    lateinit var createdBy: String

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    lateinit var createdAt: Instant

    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    lateinit var modifiedAt: Instant

    @Column(name = "modified_by")
    @LastModifiedBy
    lateinit var modifiedBy: String
}
