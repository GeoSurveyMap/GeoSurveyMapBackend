package com.loess.geosurveymap.report

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import com.loess.geosurveymap.location.Filters
import com.loess.geosurveymap.location.Location
import com.loess.geosurveymap.location.LocationService
import com.loess.geosurveymap.survey.Category
import io.swagger.v3.oas.annotations.Operation
import org.springframework.core.io.Resource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

@RestController("/api/v1/reports")
class ReportController(
    private val excelReportService: ReportService,
    private val apiRequestHandler: ApiRequestHandler,
) {

    @Operation(summary = "Get report from all existing surveys in xlsx format")
    @GetMapping("/surveys")
    fun downloadReport(
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC, size = 20)
        pageable: Pageable,
        @RequestParam(required = false) id: Long?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) surveyId: Long?,
        @RequestParam(required = false) category: Category?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) solution: String?,
        @RequestParam(required = false) affectedAreaMin: Double?,
        @RequestParam(required = false) affectedAreaMax: Double?,
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) kindeId: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) createdBy: String?,
        @RequestParam(required = false) createdAtStart: Instant?,
        @RequestParam(required = false) createdAtEnd: Instant?,
        @RequestParam(required = false) modifiedBy: String?,
        @RequestParam(required = false) modifiedAtStart: Instant?,
        @RequestParam(required = false) modifiedAtEnd: Instant?,
        @RequestParam(required = false) centralX: Double?,
        @RequestParam(required = false) centralY: Double?,
        @RequestParam(required = false) radiusInMeters: Double?
    ): ResponseEntity<Resource> {
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


        val date = Instant.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm").withZone(ZoneId.of("UTC"))
        val formattedDate = formatter.format(date)
        val fileName = "survey-$formattedDate.xlsx"

        return apiRequestHandler.handleResource(EXCEL_TYPE, fileName) {
            excelReportService.generateSurveyExcelReport(filters, pageable)
        }
    }


}