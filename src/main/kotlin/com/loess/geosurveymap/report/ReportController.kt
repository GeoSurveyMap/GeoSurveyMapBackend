package com.loess.geosurveymap.report

import com.loess.geosurveymap.apiutils.ApiRequestHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/reports")
class ReportController(
    private val excelReportService: ReportService,
    private val apiRequestHandler: ApiRequestHandler
) {

//    // CSV Report Endpoint
//    @GetMapping("/surveys/csv")
//    fun downloadSurveyCsvReport() {
//        val csvContent = csvReportService.generateSurveyReport()
//        val headers = HttpHeaders().apply {
//            contentType = MediaType.TEXT_PLAIN
//            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=survey_report.csv")
//        }
//        return ResponseEntity.ok()
//            .headers(headers)
//            .body(csvContent.toByteArray())
//    }
//
//    // Excel Report Endpoint
//    @GetMapping("/api/v1/reports/surveys/excel")
//    fun downloadSurveyExcelReport(): {
//        val excelContent = excelReportService.generateSurveyExcelReport()
//        val headers = HttpHeaders().apply {
//            contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=survey_report.xlsx")
//        }
//        return ResponseEntity.ok()
//            .headers(headers)
//            .body(excelContent)
//    }
}