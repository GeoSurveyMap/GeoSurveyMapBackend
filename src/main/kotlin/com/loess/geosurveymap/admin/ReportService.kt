package com.loess.geosurveymap.admin

import com.loess.geosurveymap.location.Filters
import com.loess.geosurveymap.location.LocationService
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter.*


@Service
class ReportService(private val locationService: LocationService) {

    fun generateSurveyExcelReport(filters: Filters?, pageable: Pageable): Resource {
        val data = locationService.getFilteredLocations(filters, pageable)
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Surveys")

        val styleColumn0 = createColoredCellStyle(workbook, Color(255, 255, 204))
        val styleColumns1to4 = createColoredCellStyle(workbook, Color(204, 255, 204))
        val styleColumns5to7 = createColoredCellStyle(workbook, Color(204, 229, 255))
        val styleColumn8 = createColoredCellStyle(workbook, Color(255, 204, 204))

        val headerRow: Row = sheet.createRow(0)
        val headers = listOf("ID", "Category", "Description", "Solution", "Affected Area", "Location Name", "X", "Y", "User Email", "Created At")

        headers.forEachIndexed { index, header ->
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = styleColumn0
        }

        data.forEachIndexed { rowIndex, locationWithSurvey ->
            val row: Row = sheet.createRow(rowIndex + 1)
            with(locationWithSurvey) {
                val survey = this.survey
                val location = this

                val cell0 = row.createCell(0)
                cell0.setCellValue(survey.id!!.toDouble())
                cell0.cellStyle = styleColumn0

                val cell1 = row.createCell(1)
                cell1.setCellValue(survey.category.name)
                cell1.cellStyle = styleColumns1to4

                val cell2 = row.createCell(2)
                cell2.setCellValue(survey.description)
                cell2.cellStyle = styleColumns1to4

                val cell3 = row.createCell(3)
                cell3.setCellValue(survey.solution)
                cell3.cellStyle = styleColumns1to4

                val cell4 = row.createCell(4)
                cell4.setCellValue(survey.affectedArea)
                cell4.cellStyle = styleColumns1to4

                val cell5 = row.createCell(5)
                cell5.setCellValue(location.name)
                cell5.cellStyle = styleColumns5to7

                val cell6 = row.createCell(6)
                cell6.setCellValue(x)
                cell6.cellStyle = styleColumns5to7

                val cell7 = row.createCell(7)
                cell7.setCellValue(y)
                cell7.cellStyle = styleColumns5to7

                val cell8 = row.createCell(8)
                cell8.setCellValue(survey.user.email)
                cell8.cellStyle = styleColumn8

                val date = Instant.now()
                val formatter = ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of("UTC"))
                val formattedDate = formatter.format(date)

                val cell9 = row.createCell(9)
                cell9.setCellValue(formattedDate)
                cell9.cellStyle = styleColumn0
            }
        }

        headers.indices.forEach { sheet.autoSizeColumn(it) }

        return generateResource(workbook)
    }

    private fun generateResource(workbook: Workbook): Resource {
        val outputStream = ByteArrayOutputStream()
        workbook.use { it.write(outputStream) }
        val resource = ByteArrayResource(outputStream.toByteArray())
        return resource
    }

    private fun createColoredCellStyle(workbook: Workbook, color: Color): CellStyle {
        val style = workbook.createCellStyle()

        style.fillPattern = FillPatternType.SOLID_FOREGROUND

        val rgb = XSSFColor(color, null)
        if (style is XSSFCellStyle) {
            style.setFillForegroundColor(rgb)
        } else {
            style.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        }

        style.borderBottom = BorderStyle.THIN
        style.borderTop = BorderStyle.THIN
        style.borderLeft = BorderStyle.THIN
        style.borderRight = BorderStyle.THIN

        return style
    }
}
