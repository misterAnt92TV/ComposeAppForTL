package com.tlincompose.domain.repository

import com.tlincompose.domain.model.ActivityDefinition

interface ActivityDefinitionRepository {
    suspend fun loadAll(): List<ActivityDefinition>
    suspend fun upsert(definition: ActivityDefinition, previousExtCode: String? = null)
    suspend fun delete(extCode: String)
    suspend fun replaceAll(definitions: List<ActivityDefinition>)
}
