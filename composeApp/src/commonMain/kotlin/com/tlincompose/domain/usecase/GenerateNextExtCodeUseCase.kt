package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinition

class GenerateNextExtCodeUseCase {
    operator fun invoke(definitions: List<ActivityDefinition>): String {
        val nextNumber = definitions
            .mapNotNull(::extractExtNumber)
            .maxOrNull()
            ?.plus(1)
            ?: 1

        return "EXT-${nextNumber.toString().padStart(4, '0')}"
    }

    private fun extractExtNumber(definition: ActivityDefinition): Int? =
        definition.extCode
            .removePrefix("EXT-")
            .takeIf { it != definition.extCode }
            ?.toIntOrNull()
}
