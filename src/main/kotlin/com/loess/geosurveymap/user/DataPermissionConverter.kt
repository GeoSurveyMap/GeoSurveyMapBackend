package com.loess.geosurveymap.user

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class DataPermissionListConverter : AttributeConverter<MutableList<CountryCode>, String> {

    override fun convertToDatabaseColumn(attribute: MutableList<CountryCode>?): String {
        return attribute?.joinToString(",") { it.name } ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): MutableList<CountryCode> {
        return dbData?.split(",")
            ?.filter { it.isNotBlank() }
            ?.map { CountryCode.valueOf(it) }
            ?.toMutableList() ?: mutableListOf()
    }
}