package com.tlincompose.domain.usecase

import com.tlincompose.domain.repository.ActivityDefinitionRepository

class DeleteActivityDefinitionUseCase(
    private val repository: ActivityDefinitionRepository,
) {
    suspend operator fun invoke(extCode: String) = repository.delete(extCode)
}
