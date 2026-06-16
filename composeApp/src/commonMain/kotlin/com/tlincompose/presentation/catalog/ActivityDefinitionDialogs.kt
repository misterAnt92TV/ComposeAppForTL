package com.tlincompose.presentation.catalog

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tlincompose.core.*
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.ProjectIconPickerLauncher
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun ActivityDefinitionEditorDialog(
    state: ActivityDefinitionEditorUiState,
    layoutSpec: AccessibilityLayoutSpec,
    projectIconPickerLauncher: ProjectIconPickerLauncher,
    onDismiss: () -> Unit,
    onTypeChanged: (EntryType) -> Unit,
    onExtCodeChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDurationChanged: (String) -> Unit,
    onProjectUrlChanged: (String) -> Unit,
    onProjectIconPresetChanged: (ProjectIconPreset) -> Unit,
    onPickProjectCustomIcon: (ByteArray) -> Unit,
    onClearProjectIcon: () -> Unit,
    onSave: () -> Unit,
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = strings.editEntityTitle(state.isEditing),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = strings.uniqueCode(state.extCode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = layoutSpec.dialogMaxHeight)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    EntryType.entries.forEach { type ->
                        FilterChip(
                            selected = state.type == type,
                            onClick = { onTypeChanged(type) },
                            label = { Text(type.displayName(strings.language)) },
                        )
                    }
                }
                OutlinedTextField(
                    value = state.extCode,
                    onValueChange = onExtCodeChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.extCodeLabel) },
                    isError = state.extCodeError != null,
                    singleLine = true,
                )
                state.extCodeError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                OutlinedTextField(
                    value = state.title,
                    onValueChange = onTitleChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.titleFieldLabel) },
                    placeholder = { Text(strings.titlePlaceholder) },
                    isError = state.titleError != null,
                    singleLine = true,
                )
                state.titleError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                OutlinedTextField(
                    value = state.description,
                    onValueChange = onDescriptionChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.descriptionFieldLabel) },
                    placeholder = { Text(strings.descriptionPlaceholder) },
                    minLines = 2,
                )
                OutlinedTextField(
                    value = state.durationHoursText,
                    onValueChange = onDurationChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.defaultDurationHoursLabel) },
                    placeholder = { Text(strings.hoursPlaceholder) },
                    isError = state.durationError != null,
                    singleLine = true,
                )
                state.durationError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                if (state.type == EntryType.PROJECT) {
                    Text(
                        text = strings.projectIconTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = strings.projectIconDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ProjectIconAvatar(
                                entryType = state.type,
                                projectIconPreset = state.projectIconPreset,
                                projectCustomIconBase64 = state.projectCustomIconBase64,
                                title = state.title,
                                size = 56.dp,
                                showPlaceholderWhenEmpty = true,
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = when {
                                        state.hasProjectCustomIcon -> strings.customIconActive
                                        state.projectIconPreset != null -> strings.presetIconActive(state.projectIconPreset)
                                        else -> strings.noIconSelected
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = when {
                                        state.hasProjectCustomIcon -> strings.customIconHelp
                                        state.projectIconPreset != null -> strings.presetIconHelp
                                        else -> strings.noIconHelp
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ProjectIconPreset.entries.forEach { preset ->
                            FilterChip(
                                selected = state.projectIconPreset == preset,
                                onClick = { onProjectIconPresetChanged(preset) },
                                label = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = preset.imageVector(),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Text(
                                            text = preset.displayLabel(strings.language),
                                            maxLines = 2,
                                        )
                                    }
                                },
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AppActionButton(
                            text = strings.uploadFromFile,
                            onClick = {
                                projectIconPickerLauncher.pickImage { bytes ->
                                    bytes?.let(onPickProjectCustomIcon)
                                }
                            },
                            variant = AppButtonVariant.SECONDARY,
                            minHeight = layoutSpec.buttonMinHeight,
                            modifier = Modifier.weight(1f),
                        )
                        AppActionButton(
                            text = strings.removeIconLabel,
                            onClick = onClearProjectIcon,
                            enabled = state.projectIconPreset != null || state.hasProjectCustomIcon,
                            variant = AppButtonVariant.TERTIARY,
                            minHeight = layoutSpec.buttonMinHeight,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                OutlinedTextField(
                    value = state.projectUrl,
                    onValueChange = onProjectUrlChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(strings.projectUrlLabel) },
                    placeholder = { Text(strings.projectUrlPlaceholder) },
                    isError = state.projectUrlError != null,
                    singleLine = true,
                )
                state.projectUrlError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        dismissButton = {
            AppActionButton(
                text = strings.cancelLabel,
                onClick = onDismiss,
                variant = AppButtonVariant.TERTIARY,
                minHeight = layoutSpec.buttonMinHeight,
            )
        },
        confirmButton = {
            AppActionButton(
                text = strings.saveEntityLabel(state.isEditing),
                onClick = onSave,
                minHeight = layoutSpec.buttonMinHeight,
            )
        },
    )
}

@Composable
internal fun DeleteActivityDefinitionDialog(
    definition: ActivityDefinition,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.deleteExtCode(definition.extCode),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Text(
                text = strings.deleteEntityDescription(definition.title),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        dismissButton = {
            AppActionButton(
                text = strings.cancelLabel,
                onClick = onDismiss,
                variant = AppButtonVariant.TERTIARY,
                minHeight = 48.dp,
            )
        },
        confirmButton = {
            AppActionButton(
                text = strings.deleteLabel,
                onClick = onConfirmDelete,
                minHeight = 48.dp,
            )
        },
    )
}
