package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.ExportActivityTypeFilter

class FilterExportEntriesUseCase {
    operator fun invoke(
        entries: List<DailyEntry>,
        filter: ExportActivityTypeFilter,
    ): List<DailyEntry> {
        if (filter.isDefault) return entries

        return entries.mapNotNull { entry ->
            val filteredActivities = entry.activities.filter { activity ->
                filter.includes(activity.type)
            }
            filteredActivities
                .takeIf(List<*>::isNotEmpty)
                ?.let { entry.copy(activities = filteredActivities) }
        }
    }
}
