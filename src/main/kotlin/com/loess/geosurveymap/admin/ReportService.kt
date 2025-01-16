package com.loess.geosurveymap.admin

import com.loess.geosurveymap.exceptions.ForbiddenException
import com.loess.geosurveymap.location.Filters
import com.loess.geosurveymap.location.LocationService
import com.loess.geosurveymap.user.UserService
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.Pageable
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter.*


@Service
class ReportService(
    private val locationService: LocationService,
    private val userService: UserService
) {

    fun generateSurveyExcelReport(
        filters: Filters?,
        pageable: Pageable,
        kindeId: String,
        authorities: Collection<GrantedAuthority>
    ): Resource {
        val userPermissions = userService.findByKindeId(kindeId).permissions
        val data = locationService.getFilteredLocations(filters, pageable, kindeId, authorities, userPermissions)

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Surveys")

        val styleColumn0 = createColoredCellStyle(workbook, Color(255, 255, 204))
        val styleColumns1to4 = createColoredCellStyle(workbook, Color(204, 255, 204))
        val styleColumns5to8 = createColoredCellStyle(workbook, Color(204, 229, 255))
        val styleColumn9 = createColoredCellStyle(workbook, Color(255, 204, 204))

        val headerRow: Row = sheet.createRow(0)
        val headers = listOf("ID", "Category", "Description", "Solution", "Affected Area", "Location Name", "X", "Y", "Country", "User Email", "Created At")

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

                val idCell = row.createCell(0)
                idCell.setCellValue(survey.id!!.toDouble())
                idCell.cellStyle = styleColumn0

                val categoryNameCell = row.createCell(1)
                categoryNameCell.setCellValue(survey.category.name)
                categoryNameCell.cellStyle = styleColumns1to4

                val descriptionCell = row.createCell(2)
                descriptionCell.setCellValue(survey.description)
                descriptionCell.cellStyle = styleColumns1to4

                val solutionCell = row.createCell(3)
                solutionCell.setCellValue(survey.solution)
                solutionCell.cellStyle = styleColumns1to4

                val affectedAreaCell = row.createCell(4)
                affectedAreaCell.setCellValue(survey.affectedArea)
                affectedAreaCell.cellStyle = styleColumns1to4

                val locationNameCell = row.createCell(5)
                locationNameCell.setCellValue(location.name)
                locationNameCell.cellStyle = styleColumns5to8

                val xCoordCell = row.createCell(6)
                xCoordCell.setCellValue(x)
                xCoordCell.cellStyle = styleColumns5to8

                val yCoordCell = row.createCell(7)
                yCoordCell.setCellValue(y)
                yCoordCell.cellStyle = styleColumns5to8

                val country = row.createCell(8)
                country.setCellValue(countryCode.fullName)
                country.cellStyle = styleColumns5to8

                val userEmailCell = row.createCell(9)
                userEmailCell.setCellValue(survey.user.email)
                userEmailCell.cellStyle = styleColumn9

                val formatter = ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of("UTC"))
                val formattedDate = formatter.format(survey.createdAt)

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
