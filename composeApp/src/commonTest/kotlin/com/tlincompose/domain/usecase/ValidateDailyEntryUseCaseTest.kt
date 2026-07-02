package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDraftInput
import com.tlincompose.domain.model.EntryType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ValidateDailyEntryUseCaseTest {
    private val useCase = ValidateDailyEntryUseCase()

    @Test
    fun rejectsRowWithoutSelectedExtEntity() {
        val result = useCase(
            listOf(
                ActivityDraftInput(
                    type = EntryType.PROJECT,
                    extCode = null,
                    title = "",
                    description = "",
                    projectUrl = null,
                    hoursText = "4",
                ),
            ),
        )

        assertEquals(true, result.hasErrors)
        assertNull(result.activities.firstOrNull())
    }

    @Test
    fun convertsHoursToMinutesForValidRows() {
        val result = useCase(
            listOf(
                ActivityDraftInput(
                    type = EntryType.PERMIT,
                    extCode = "EXT-0002",
                    title = "Permesso visita medica",
                    description = "",
                    projectUrl = null,
                    hoursText = "1.5",
                ),
            ),
        )

        assertEquals(false, result.hasErrors)
        assertEquals(90, result.activities.single().minutes)
    }

    @Test
    fun keepsMultipleActivitiesAndIgnoresCompletelyBlankRows() {
        val result = useCase(
            listOf(
                ActivityDraftInput(
                    type = EntryType.PROJECT,
                    extCode = "EXT-0001",
                    title = "Apollo",
                    description = "Sprint",
                    projectUrl = "https://example.com/apollo",
                    hoursText = "6",
                ),
                ActivityDraftInput(
                    type = EntryType.PERMIT,
                    extCode = "EXT-0002",
                    title = "Permesso",
                    description = "",
                    projectUrl = null,
                    hoursText = "2",
                ),
                ActivityDraftInput(
                    type = EntryType.VACATION,
                    extCode = null,
                    title = "",
                    description = "",
                    projectUrl = null,
                    hoursText = "",
                ),
            ),
        )

        assertEquals(false, result.hasErrors)
        assertEquals(2, result.activities.size)
        assertEquals(listOf("EXT-0001", "EXT-0002"), result.activities.mapNotNull { it.extCode })
        assertEquals(listOf(360, 120), result.activities.map { it.minutes })
        assertNotNull(result.activities.firstOrNull())
    }
}
