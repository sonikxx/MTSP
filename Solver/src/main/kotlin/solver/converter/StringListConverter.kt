package solver.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import solver.dto.City

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

