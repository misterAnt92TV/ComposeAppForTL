package com.tlincompose.domain.usecase

import com.tlincompose.core.AppTimeZone
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.BuiltInActivityDefinitions
import com.tlincompose.domain.model.DefaultWorkdayMinutes
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EnsureDefaultActivityDefinitionsUseCase(
    private val repository: ActivityDefinitionRepository,
    private val todayProvider: () -> LocalDate = currentDateProvider(),
) {
    suspend operator fun invoke() {
        val existingDefinitions = repository.loadAll()
        val existingCodes = existingDefinitions.map(ActivityDefinition::extCode).toSet()
        val today = todayProvider()

        defaultDefinitions(today)
            .filterNot { it.extCode in existingCodes }
            .forEach { definition ->
                repository.upsert(definition)
            }
    }

    private fun defaultDefinitions(today: LocalDate): List<ActivityDefinition> = listOf(
        ActivityDefinition(
            extCode = BuiltInActivityDefinitions.BloodDonationCode,
            type = EntryType.PERMIT,
            title = "Donazione sangue",
            description = "Permesso dedicato alla donazione sangue.",
            defaultMinutes = DefaultWorkdayMinutes,
            createdDate = today,
            updatedDate = today,
        ),
        ActivityDefinition(
            extCode = BuiltInActivityDefinitions.MedicalVisitCode,
            type = EntryType.PERMIT,
            title = "Visita medica",
            description = "Permesso per visita medica o accertamenti sanitari.",
            defaultMinutes = 120,
            createdDate = today,
            updatedDate = today,
        ),
    )

    companion object {
        @OptIn(ExperimentalTime::class)
        private fun currentDateProvider(): () -> LocalDate = {
            Clock.System.todayIn(AppTimeZone)
        }
    }
}
