package solver.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import solver.dto.SolutionStatus


@Converter(autoApply = true)
class StatusConverter : AttributeConverter<SolutionStatus, String> {

    override fun convertToDatabaseColumn(attribute: SolutionStatus): String {
        return attribute.name
    }

    override fun convertToEntityAttribute(dbData: String): SolutionStatus {
        return SolutionStatus.valueOf(dbData)
    }
}