package com.tlincompose.data.mapper

import com.tlincompose.data.local.ActivityEntity
import com.tlincompose.data.local.DailyEntryEntity
import com.tlincompose.data.local.EntryTypeEntity
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class TimesheetEntityMapperTest {
    @Test
    fun entryEntityMapsToDomainAndBack() {
        val entity = DailyEntryEntity(
            date = LocalDate(2026, 5, 5),
            activities = listOf(
                ActivityEntity(
                    type = EntryTypeEntity.COURSE,
                    extCode = "EXT-C001",
                    title = "Corso Kotlin",
                    description = "Formazione Compose Multiplatform",
                    projectUrl = "https://example.com/corso-kotlin",
                    projectLabel = null,
                    minutes = 240,
                ),
            ),
        )

        val domain = entity.toDomain()
        val rebuilt = domain.toEntity()

        assertEquals(
            DailyEntry(
                date = LocalDate(2026, 5, 5),
                activities = listOf(
                    Activity(
                        type = EntryType.COURSE,
                        extCode = "EXT-C001",
                        title = "Corso Kotlin",
                        description = "Formazione Compose Multiplatform",
                        projectUrl = "https://example.com/corso-kotlin",
                        minutes = 240,
                    ),
                ),
            ),
            domain,
        )
        assertEquals(entity, rebuilt)
    }
}
