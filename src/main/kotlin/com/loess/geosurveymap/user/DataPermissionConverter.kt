package com.loess.geosurveymap.user

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class DataPermissionListConverter : AttributeConverter<MutableList<DataPermission>, String> {

    override fun convertToDatabaseColumn(attribute: MutableList<DataPermission>?): String {
        return attribute?.joinToString(",") { it.name } ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): MutableList<DataPermission> {
        return dbData?.split(",")
            ?.filter { it.isNotBlank() }
            ?.map { DataPermission.valueOf(it) }
            ?.toMutableList() ?: mutableListOf()
    }
}