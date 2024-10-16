package com.loess.geosurveymap.report

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import io.swagger.v3.oas.annotations.Operation
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

@RestController("/api/v1/reports")
class ReportController(
    private val excelReportService: ReportService,
    private val apiRequestHandler: ApiRequestHandler
) {

    @Operation(summary = "Get report from all existing surveys in xlsx format")
    @GetMapping("/surveys")
    fun downloadReport(): ResponseEntity<Resource> {
        val date = Instant.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm").withZone(ZoneId.of("UTC"))
        val formattedDate = formatter.format(date)
        val fileName = "survey-$formattedDate.xlsx"

        return apiRequestHandler.handleResource(EXCEL_TYPE, fileName) {
            excelReportService.generateSurveyExcelReport()
        }
    }

}