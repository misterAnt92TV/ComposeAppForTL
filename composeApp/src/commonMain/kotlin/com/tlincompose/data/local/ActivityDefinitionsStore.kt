package com.tlincompose.data.local

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
enum class ProjectIconPresetEntity {
    WORK,
    CODE,
    PALETTE,
    BUILD,
    BUG_REPORT,
    FOLDER,
    SETTINGS,
    SCHOOL,
}

@Serializable
data class ActivityDefinitionEntity(
    val extCode: String,
    val type: EntryTypeEntity,
    val title: String,
    val description: String = "",
    val defaultMinutes: Int,
    val createdDate: LocalDate,
    val updatedDate: LocalDate,
    val projectUrl: String? = null,
    val projectIconPreset: ProjectIconPresetEntity? = null,
    val projectCustomIconBase64: String? = null,
)

@Serializable
data class ActivityDefinitionsStore(
    val definitions: List<ActivityDefinitionEntity> = emptyList(),
)
