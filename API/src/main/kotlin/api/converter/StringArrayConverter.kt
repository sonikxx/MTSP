package api.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringArrayConverter : AttributeConverter<Array<String>, String> {
    override fun convertToDatabaseColumn(attribute: Array<String>?): String {
        return attribute?.joinToString("|") ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): Array<String> {
        return dbData?.split("|")?.toTypedArray() ?: emptyArray()
    }
}
