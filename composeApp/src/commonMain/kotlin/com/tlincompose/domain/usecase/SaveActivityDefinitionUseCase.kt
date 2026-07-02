package com.tlincompose.domain.usecase

import com.tlincompose.core.AppTimeZone
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.ValidatedActivityDefinitionDraft
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SaveActivityDefinitionUseCase(
    private val repository: ActivityDefinitionRepository,
    private val todayProvider: () -> LocalDate = currentDateProvider(),
) {
    suspend operator fun invoke(
        draft: ValidatedActivityDefinitionDraft,
        existingDefinition: ActivityDefinition?,
    ): ActivityDefinition {
        val today = todayProvider()
        val projectIconPreset = draft.projectIconPreset.takeIf { draft.type == com.tlincompose.domain.model.EntryType.PROJECT }
        val projectCustomIconBase64 = draft.projectCustomIconBase64
            ?.takeIf { draft.type == com.tlincompose.domain.model.EntryType.PROJECT && projectIconPreset == null }
        val definition = ActivityDefinition(
            extCode = draft.extCode,
            type = draft.type,
            title = draft.title,
            description = draft.description,
            defaultMinutes = draft.defaultMinutes,
            createdDate = existingDefinition?.createdDate ?: today,
            updatedDate = today,
            projectUrl = draft.projectUrl,
            projectIconPreset = projectIconPreset,
            projectCustomIconBase64 = projectCustomIconBase64,
        )

        repository.upsert(
            definition = definition,
            previousExtCode = existingDefinition?.extCode,
        )
        return definition
    }

    companion object {
        @OptIn(ExperimentalTime::class)
        private fun currentDateProvider(): () -> LocalDate = {
            Clock.System.todayIn(AppTimeZone)
        }
    }
}
