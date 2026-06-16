package com.tlincompose.data.local

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
enum class EntryTypeEntity {
    PROJECT,
    COURSE,
    VACATION,
    PERMIT,
}

@Serializable
data class ActivityEntity(
    val type: EntryTypeEntity,
    val extCode: String? = null,
    val title: String? = null,
    val description: String = "",
    val projectUrl: String? = null,
    val projectLabel: String? = null,
    val minutes: Int,
)

@Serializable
data class DailyEntryEntity(
    val date: LocalDate,
    val activities: List<ActivityEntity>,
)

@Serializable
data class TimesheetStore(
    val entries: List<DailyEntryEntity> = emptyList(),
)
