package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinitionDraftInput
import com.tlincompose.domain.model.ActivityDefinitionFieldError
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ValidateActivityDefinitionUseCaseTest {
    private val useCase = ValidateActivityDefinitionUseCase()

    @Test
    fun rejectsBlankTitleAndInvalidDuration() {
        val result = useCase(
            ActivityDefinitionDraftInput(
                extCode = " ",
                type = EntryType.PROJECT,
                title = " ",
                description = "",
                durationHoursText = "0",
                projectUrl = "nota-url",
            ),
        )

        assertEquals(true, result.hasErrors)
        assertEquals(ActivityDefinitionFieldError.BLANK_EXT_CODE, result.extCodeError)
        assertEquals(ActivityDefinitionFieldError.BLANK_TITLE, result.titleError)
        assertEquals(ActivityDefinitionFieldError.NON_POSITIVE_DURATION, result.durationError)
        assertEquals(ActivityDefinitionFieldError.INVALID_PROJECT_URL, result.projectUrlError)
    }

    @Test
    fun normalizesValidDraft() {
        val result = useCase(
            ActivityDefinitionDraftInput(
                extCode = "EXT-0007",
                type = EntryType.VACATION,
                title = " Ferie estive ",
                description = " Agosto ",
                durationHoursText = "7,5",
                projectUrl = "https://example.com/ferie",
                projectIconPreset = ProjectIconPreset.PALETTE,
            ),
        )

        val validatedDraft = result.validatedDraft
        assertNotNull(validatedDraft)
        assertEquals("EXT-0007", validatedDraft.extCode)
        assertEquals("Ferie estive", validatedDraft.title)
        assertEquals("Agosto", validatedDraft.description)
        assertEquals(450, validatedDraft.defaultMinutes)
        assertEquals("https://example.com/ferie", validatedDraft.projectUrl)
        assertEquals(null, validatedDraft.projectIconPreset)
    }

    @Test
    fun keepsProjectIconOnlyForProjectDefinitions() {
        val result = useCase(
            ActivityDefinitionDraftInput(
                extCode = "EXT-0009",
                type = EntryType.PROJECT,
                title = " Portale clienti ",
                description = " Sprint corrente ",
                durationHoursText = "8",
                projectUrl = "https://example.com/clienti",
                projectIconPreset = ProjectIconPreset.CODE,
            ),
        )

        val validatedDraft = result.validatedDraft
        assertNotNull(validatedDraft)
        assertEquals(ProjectIconPreset.CODE, validatedDraft.projectIconPreset)
    }

    @Test
    fun rejectsDuplicateExtCodeWhenCreatingNewEntity() {
        val result = useCase(
            input = ActivityDefinitionDraftInput(
                extCode = "ext-0001",
                type = EntryType.PROJECT,
                title = " Portale clienti ",
                description = " Sprint corrente ",
                durationHoursText = "8",
                projectUrl = "https://example.com/clienti",
            ),
            existingExtCodes = setOf("EXT-0001", "EXT-0002"),
        )

        assertEquals(ActivityDefinitionFieldError.DUPLICATE_EXT_CODE, result.extCodeError)
        assertEquals(true, result.hasErrors)
    }
}
