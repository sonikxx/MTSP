package api.converter

import api.dto.City
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringListConverter : AttributeConverter<List<City>, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<City>?): String {
        return objectMapper.writeValueAsString(attribute ?: emptyList<City>())
    }

    override fun convertToEntityAttribute(dbData: String?): List<City> {
        return if (!dbData.isNullOrBlank()) {
            objectMapper.readValue(dbData)
        } else {
            emptyList()
        }
    }
}
