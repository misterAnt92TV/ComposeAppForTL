package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.repository.TimesheetRepository

class SyncActivitiesWithDefinitionUseCase(
    private val repository: TimesheetRepository,
) {
    suspend operator fun invoke(
        previousExtCode: String,
        definition: ActivityDefinition,
    ): Int = repository.syncActivitiesWithDefinition(
        previousExtCode = previousExtCode,
        definition = definition,
    )
}
