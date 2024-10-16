package com.loess.geosurveymap.report

import com.loess.geosurveymap.survey.Survey
import com.loess.geosurveymap.survey.SurveyService
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class ReportService(private val surveyService: SurveyService) {

    fun generateSurveyExcelReport(): ByteArray {
        val surveys: List<Survey> = surveyService.getAllSurveys()
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Surveys")

        val headerRow: Row = sheet.createRow(0)
        val headers = listOf("ID", "Category", "Description", "Solution", "Affected Area", "User ID", "User Email")
        headers.forEachIndexed { index, header ->
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }

        surveys.forEachIndexed { rowIndex, survey ->
            val row: Row = sheet.createRow(rowIndex + 1)
            row.createCell(0).setCellValue(survey.id!!.toDouble())
            row.createCell(1).setCellValue(survey.category.name)
            row.createCell(2).setCellValue(survey.description)
            row.createCell(3).setCellValue(survey.solution)
            row.createCell(4).setCellValue(survey.affectedArea)
            row.createCell(5).setCellValue(survey.user.id.toDouble())
            row.createCell(6).setCellValue(survey.user.email)
        }

        headers.indices.forEach { sheet.autoSizeColumn(it) }

        val outputStream = ByteArrayOutputStream()
        workbook.use { wb ->
            wb.write(outputStream)
        }

        return outputStream.toByteArray()
    }
}
