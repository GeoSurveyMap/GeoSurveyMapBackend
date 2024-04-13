package com.loess.geosurveymap.survey

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SurveyRepository : JpaRepository<SurveyEntity, Long>