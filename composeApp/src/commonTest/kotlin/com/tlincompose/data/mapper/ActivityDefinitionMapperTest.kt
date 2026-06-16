package com.tlincompose.data.mapper

import com.tlincompose.data.local.ActivityDefinitionEntity
import com.tlincompose.data.local.EntryTypeEntity
import com.tlincompose.data.local.ProjectIconPresetEntity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class ActivityDefinitionMapperTest {
    @Test
    fun entityMapsToDomainAndBack() {
        val entity = ActivityDefinitionEntity(
            extCode = "EXT-0001",
            type = EntryTypeEntity.PROJECT,
            title = "Apollo",
            description = "Cliente premium",
            defaultMinutes = 480,
            createdDate = LocalDate(2026, 5, 1),
            updatedDate = LocalDate(2026, 5, 2),
            projectUrl = "https://example.com/apollo",
            projectIconPreset = ProjectIconPresetEntity.CODE,
            projectCustomIconBase64 = null,
        )

        val domain = entity.toDomain()
        val rebuilt = domain.toEntity()

        assertEquals(
            ActivityDefinition(
                extCode = "EXT-0001",
                type = EntryType.PROJECT,
                title = "Apollo",
                description = "Cliente premium",
                defaultMinutes = 480,
                createdDate = LocalDate(2026, 5, 1),
                updatedDate = LocalDate(2026, 5, 2),
                projectUrl = "https://example.com/apollo",
                projectIconPreset = ProjectIconPreset.CODE,
            ),
            domain,
        )
        assertEquals(entity, rebuilt)
    }
}
