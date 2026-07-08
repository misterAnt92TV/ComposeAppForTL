package com.tlincompose.data.mapper

import com.tlincompose.data.local.ActivityDefinitionEntity
import com.tlincompose.data.local.ActivityEntity
import com.tlincompose.data.local.ActivityWorkLocationEntity
import com.tlincompose.data.local.DailyEntryEntity
import com.tlincompose.data.local.EntryTypeEntity
import com.tlincompose.data.local.ProjectIconPresetEntity
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.ActivityWorkLocation
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset

fun ActivityEntity.toDomain(): Activity =
    Activity(
        type = type.toDomain(),
        extCode = extCode,
        title = title ?: projectLabel.orEmpty(),
        description = description,
        projectUrl = projectUrl,
        minutes = minutes,
        workLocation = workLocation.toDomain(),
    )

fun Activity.toEntity(): ActivityEntity =
    ActivityEntity(
        type = type.toEntity(),
        extCode = extCode,
        title = title,
        description = description,
        projectUrl = projectUrl,
        projectLabel = title.takeIf { type == EntryType.PROJECT },
        minutes = minutes,
        workLocation = workLocation.toEntity(),
    )

fun DailyEntryEntity.toDomain(): DailyEntry =
    DailyEntry(
        date = date,
        activities = activities.map(ActivityEntity::toDomain),
    )

fun DailyEntry.toEntity(): DailyEntryEntity =
    DailyEntryEntity(
        date = date,
        activities = activities.map(Activity::toEntity),
    )

fun EntryTypeEntity.toDomain(): EntryType = when (this) {
    EntryTypeEntity.PROJECT -> EntryType.PROJECT
    EntryTypeEntity.COURSE -> EntryType.COURSE
    EntryTypeEntity.VACATION -> EntryType.VACATION
    EntryTypeEntity.PERMIT -> EntryType.PERMIT
}

fun EntryType.toEntity(): EntryTypeEntity = when (this) {
    EntryType.PROJECT -> EntryTypeEntity.PROJECT
    EntryType.COURSE -> EntryTypeEntity.COURSE
    EntryType.VACATION -> EntryTypeEntity.VACATION
    EntryType.PERMIT -> EntryTypeEntity.PERMIT
}

fun ActivityWorkLocationEntity.toDomain(): ActivityWorkLocation = when (this) {
    ActivityWorkLocationEntity.SMART_WORKING -> ActivityWorkLocation.SMART_WORKING
    ActivityWorkLocationEntity.OFFICE -> ActivityWorkLocation.OFFICE
    ActivityWorkLocationEntity.CLIENT_SITE -> ActivityWorkLocation.CLIENT_SITE
}

fun ActivityWorkLocation.toEntity(): ActivityWorkLocationEntity = when (this) {
    ActivityWorkLocation.SMART_WORKING -> ActivityWorkLocationEntity.SMART_WORKING
    ActivityWorkLocation.OFFICE -> ActivityWorkLocationEntity.OFFICE
    ActivityWorkLocation.CLIENT_SITE -> ActivityWorkLocationEntity.CLIENT_SITE
}

fun ActivityDefinitionEntity.toDomain(): ActivityDefinition =
    ActivityDefinition(
        extCode = extCode,
        type = type.toDomain(),
        title = title,
        description = description,
        defaultMinutes = defaultMinutes,
        createdDate = createdDate,
        updatedDate = updatedDate,
        projectUrl = projectUrl,
        projectIconPreset = projectIconPreset?.toDomain(),
        projectCustomIconBase64 = projectCustomIconBase64,
    )

fun ActivityDefinition.toEntity(): ActivityDefinitionEntity =
    ActivityDefinitionEntity(
        extCode = extCode,
        type = type.toEntity(),
        title = title,
        description = description,
        defaultMinutes = defaultMinutes,
        createdDate = createdDate,
        updatedDate = updatedDate,
        projectUrl = projectUrl,
        projectIconPreset = projectIconPreset?.toEntity(),
        projectCustomIconBase64 = projectCustomIconBase64,
    )

private fun ProjectIconPresetEntity.toDomain(): ProjectIconPreset = when (this) {
    ProjectIconPresetEntity.WORK -> ProjectIconPreset.WORK
    ProjectIconPresetEntity.CODE -> ProjectIconPreset.CODE
    ProjectIconPresetEntity.PALETTE -> ProjectIconPreset.PALETTE
    ProjectIconPresetEntity.BUILD -> ProjectIconPreset.BUILD
    ProjectIconPresetEntity.BUG_REPORT -> ProjectIconPreset.BUG_REPORT
    ProjectIconPresetEntity.FOLDER -> ProjectIconPreset.FOLDER
    ProjectIconPresetEntity.SETTINGS -> ProjectIconPreset.SETTINGS
    ProjectIconPresetEntity.SCHOOL -> ProjectIconPreset.SCHOOL
}

private fun ProjectIconPreset.toEntity(): ProjectIconPresetEntity = when (this) {
    ProjectIconPreset.WORK -> ProjectIconPresetEntity.WORK
    ProjectIconPreset.CODE -> ProjectIconPresetEntity.CODE
    ProjectIconPreset.PALETTE -> ProjectIconPresetEntity.PALETTE
    ProjectIconPreset.BUILD -> ProjectIconPresetEntity.BUILD
    ProjectIconPreset.BUG_REPORT -> ProjectIconPresetEntity.BUG_REPORT
    ProjectIconPreset.FOLDER -> ProjectIconPresetEntity.FOLDER
    ProjectIconPreset.SETTINGS -> ProjectIconPresetEntity.SETTINGS
    ProjectIconPreset.SCHOOL -> ProjectIconPresetEntity.SCHOOL
}
