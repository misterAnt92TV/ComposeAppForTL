package com.tlincompose.presentation.catalog

import com.tlincompose.domain.model.BuiltInActivityDefinitions
import com.tlincompose.domain.model.DefaultWorkdayMinutes
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset

enum class ActivityDefinitionEditorMode {
    CREATE,
    EDIT,
}

data class ActivityDefinitionEditorUiState(
    val mode: ActivityDefinitionEditorMode,
    val originalExtCode: String? = null,
    val extCode: String,
    val type: EntryType = EntryType.PROJECT,
    val title: String = "",
    val description: String = "",
    val durationHoursText: String = DefaultWorkdayMinutes.div(60).toString(),
    val projectUrl: String = "",
    val projectIconPreset: ProjectIconPreset? = null,
    val projectCustomIconBase64: String? = null,
    val extCodeError: String? = null,
    val titleError: String? = null,
    val durationError: String? = null,
    val projectUrlError: String? = null,
) {
    val isEditing: Boolean
        get() = mode == ActivityDefinitionEditorMode.EDIT

    val isProtectedDefinition: Boolean
        get() = originalExtCode?.let(BuiltInActivityDefinitions::isProtectedCode) == true ||
            BuiltInActivityDefinitions.isProtectedCode(extCode)

    val hasProjectCustomIcon: Boolean
        get() = !projectCustomIconBase64.isNullOrBlank()
}
