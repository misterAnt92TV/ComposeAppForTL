package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.repository.ActivityDefinitionRepository

class LoadActivityDefinitionsUseCase(
    private val repository: ActivityDefinitionRepository,
) {
    suspend operator fun invoke(): List<ActivityDefinition> = repository.loadAll()
}
