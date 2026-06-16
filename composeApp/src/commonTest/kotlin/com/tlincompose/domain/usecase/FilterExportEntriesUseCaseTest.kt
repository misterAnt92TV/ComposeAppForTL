package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportActivityTypeFilter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class FilterExportEntriesUseCaseTest {
    private val useCase = FilterExportEntriesUseCase()

    @Test
    fun keepsOnlySelectedTypesAndDropsEmptyDays() {
        val entries = listOf(
            DailyEntry(
                date = LocalDate(2026, 5, 10),
                activities = listOf(
                    Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480),
                    Activity(type = EntryType.PERMIT, extCode = "EXT-0002", title = "Permesso", minutes = 120),
                ),
            ),
            DailyEntry(
                date = LocalDate(2026, 5, 11),
                activities = listOf(
                    Activity(type = EntryType.VACATION, extCode = "EXT-0003", title = "Ferie", minutes = 480),
                ),
            ),
        )

        val filtered = useCase(
            entries = entries,
            filter = ExportActivityTypeFilter(setOf(EntryType.PROJECT, EntryType.VACATION)),
        )

        assertEquals(2, filtered.size)
        assertEquals(listOf(EntryType.PROJECT), filtered[0].activities.map(Activity::type))
        assertEquals(listOf(EntryType.VACATION), filtered[1].activities.map(Activity::type))
    }

    @Test
    fun returnsOriginalEntriesWhenAllTypesAreSelected() {
        val entries = listOf(
            DailyEntry(
                date = LocalDate(2026, 5, 10),
                activities = listOf(
                    Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480),
                ),
            ),
        )

        val filtered = useCase(entries, ExportActivityTypeFilter())

        assertEquals(entries, filtered)
    }
}
