package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.EntryType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class GenerateNextExtCodeUseCaseTest {
    private val useCase = GenerateNextExtCodeUseCase()

    @Test
    fun generatesFirstCodeWhenCatalogIsEmpty() {
        assertEquals("EXT-0001", useCase(emptyList()))
    }

    @Test
    fun generatesCodeAfterHighestExistingNumber() {
        val definitions = listOf(
            definition(extCode = "EXT-0002"),
            definition(extCode = "EXT-0010"),
            definition(extCode = "EXT-0004"),
        )

        assertEquals("EXT-0011", useCase(definitions))
    }

    private fun definition(extCode: String): ActivityDefinition =
        ActivityDefinition(
            extCode = extCode,
            type = EntryType.PROJECT,
            title = "Catalog item",
            description = "",
            defaultMinutes = 480,
            createdDate = LocalDate(2026, 5, 1),
            updatedDate = LocalDate(2026, 5, 1),
        )
}
