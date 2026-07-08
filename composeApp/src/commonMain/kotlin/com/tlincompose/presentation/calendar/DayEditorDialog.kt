package com.tlincompose.presentation.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tlincompose.core.activityRowLabel
import com.tlincompose.core.addActivityLabel
import com.tlincompose.core.baseDuration
import com.tlincompose.core.cancelLabel
import com.tlincompose.core.chooseEntityForType
import com.tlincompose.core.closeLabel
import com.tlincompose.core.coverIncompleteMonthDescription
import com.tlincompose.core.coverIncompleteMonthLabel
import com.tlincompose.core.dayDialogDescription
import com.tlincompose.core.dayDialogTitle
import com.tlincompose.core.dayRangeDialogDescription
import com.tlincompose.core.dayRangeDialogTitle
import com.tlincompose.core.displayName
import com.tlincompose.core.extEntityDisplayLabel
import com.tlincompose.core.formatHours
import com.tlincompose.core.hoursOrFractionsLabel
import com.tlincompose.core.hoursPlaceholder
import com.tlincompose.core.noEntitiesForType
import com.tlincompose.core.noEntityAvailableForType
import com.tlincompose.core.rangeWeekendDayDialogNote
import com.tlincompose.core.removeLabel
import com.tlincompose.core.saveLabel
import com.tlincompose.core.saveSelectedDaysLabel
import com.tlincompose.core.selectExtEntity
import com.tlincompose.core.weekendDayDialogNote
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.ActivityWorkLocation
import com.tlincompose.domain.model.EntryType
import com.tlincompose.core.workLocationLabel
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.catalog.ProjectIconAvatar
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DayEditorDialog(
    state: DayEditorUiState,
    availableDefinitions: List<ActivityDefinition>,
    layoutSpec: AccessibilityLayoutSpec,
    onDismiss: () -> Unit,
    onAddRow: () -> Unit,
    onRemoveRow: (Int) -> Unit,
    onTypeChanged: (Int, EntryType) -> Unit,
    onDefinitionSelected: (Int, ActivityDefinition) -> Unit,
    onHoursChanged: (Int, String) -> Unit,
    onWorkLocationChanged: (Int, ActivityWorkLocation) -> Unit,
    onSave: () -> Unit,
    onCoverIncompleteMonth: () -> Unit,
) {
    val strings = LocalAppStrings.current
    var pickerRowIndex by remember(state.target.sourceDate, state.rows.size) { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (state.target.isRange) {
                        strings.dayRangeDialogTitle(
                            startDate = state.target.startDate,
                            endDate = state.target.endDate,
                        )
                    } else {
                        strings.dayDialogTitle(state.target.sourceDate)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = if (state.target.isRange) {
                        strings.dayRangeDialogDescription(state.target.affectedDayCount)
                    } else {
                        strings.dayDialogDescription
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (state.target.includesWeekend) {
                    Text(
                        text = if (state.target.isRange) {
                            strings.rangeWeekendDayDialogNote
                        } else {
                            strings.weekendDayDialogNote
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = layoutSpec.dialogMaxHeight)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val canSubmitIncompleteMonthCoverage = state.rows.size == 1 &&
                    state.rows.single().extCode != null &&
                    state.rows.single().title.isNotBlank() &&
                    state.rows.single().hoursText.isNotBlank()

                state.rows.forEachIndexed { index, row ->
                    val errorMessage = state.errors.getOrNull(index)
                    val typeDefinitions = availableDefinitions.filter { it.type == row.type }
                    val selectedDefinition = availableDefinitions.firstOrNull { it.extCode == row.extCode }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = strings.activityRowLabel(index),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                if (state.rows.size > 1) {
                                    AppActionButton(
                                        text = strings.removeLabel,
                                        onClick = { onRemoveRow(index) },
                                        variant = AppButtonVariant.TERTIARY,
                                        minHeight = layoutSpec.buttonMinHeight,
                                        modifier = Modifier.testTag("remove-activity-row-$index"),
                                    )
                                }
                            }
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                EntryType.entries.forEach { type ->
                                    FilterChip(
                                        selected = row.type == type,
                                        onClick = { onTypeChanged(index, type) },
                                        label = { Text(type.displayName(strings.language)) },
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { pickerRowIndex = index },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sizeIn(minHeight = layoutSpec.buttonMinHeight),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    ProjectIconAvatar(
                                        entryType = selectedDefinition?.type ?: row.type,
                                        projectIconPreset = selectedDefinition?.projectIconPreset,
                                        projectCustomIconBase64 = selectedDefinition?.projectCustomIconBase64,
                                        title = selectedDefinition?.title ?: row.title,
                                        size = 36.dp,
                                    )
                                    Text(
                                        text = row.extCode?.let { strings.extEntityDisplayLabel(it, row.title) }
                                            ?: strings.selectExtEntity,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start,
                                        maxLines = 2,
                                    )
                                }
                            }
                            if (row.extCode != null) {
                                Surface(
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        ProjectIconAvatar(
                                            entryType = selectedDefinition?.type ?: row.type,
                                            projectIconPreset = selectedDefinition?.projectIconPreset,
                                            projectCustomIconBase64 = selectedDefinition?.projectCustomIconBase64,
                                            title = row.title,
                                            size = 40.dp,
                                        )
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            Text(
                                                text = row.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            if (row.description.isNotBlank()) {
                                                Text(
                                                    text = row.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                            row.projectUrl.takeIf(String::isNotBlank)?.let { projectUrl ->
                                                Text(
                                                    text = projectUrl,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.tertiary,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }
                                    }
                                }
                            } else if (typeDefinitions.isEmpty()) {
                                Text(
                                    text = strings.noEntityAvailableForType(row.type),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            if (row.extCode != null) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = strings.workLocationLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        ActivityWorkLocation.entries.forEach { workLocation ->
                                            FilterChip(
                                                selected = row.workLocation == workLocation,
                                                onClick = { onWorkLocationChanged(index, workLocation) },
                                                label = {
                                                    Text(workLocation.displayName(strings.language))
                                                },
                                                modifier = Modifier.testTag(
                                                    "work-location-$index-${workLocation.name.lowercase()}",
                                                ),
                                            )
                                        }
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = row.hoursText,
                                onValueChange = { onHoursChanged(index, it) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(strings.hoursOrFractionsLabel) },
                                placeholder = { Text(strings.hoursPlaceholder) },
                                isError = errorMessage != null,
                                singleLine = true,
                            )
                            errorMessage?.let { error ->
                                Surface(
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = error,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(8.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                AppActionButton(
                    text = strings.addActivityLabel,
                    onClick = onAddRow,
                    variant = AppButtonVariant.SECONDARY,
                    minHeight = layoutSpec.buttonMinHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add-activity-row-button"),
                )

                if (state.canCoverIncompleteMonth && !state.target.isRange && state.rows.size == 1) {
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = strings.coverIncompleteMonthDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                            AppActionButton(
                                text = strings.coverIncompleteMonthLabel,
                                onClick = onCoverIncompleteMonth,
                                variant = AppButtonVariant.SECONDARY,
                                minHeight = layoutSpec.buttonMinHeight,
                                enabled = canSubmitIncompleteMonthCoverage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cover-incomplete-month-button"),
                            )
                        }
                    }
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
                text = if (state.target.isRange) {
                    strings.saveSelectedDaysLabel(state.target.affectedDayCount)
                } else {
                    strings.saveLabel
                },
                onClick = onSave,
                minHeight = layoutSpec.buttonMinHeight,
            )
        },
    )

    pickerRowIndex?.let { rowIndex ->
        ActivityDefinitionPickerDialog(
            definitions = availableDefinitions.filter { it.type == state.rows[rowIndex].type },
            rowType = state.rows[rowIndex].type,
            onDismiss = { pickerRowIndex = null },
            onSelect = { definition ->
                onDefinitionSelected(rowIndex, definition)
                pickerRowIndex = null
            },
        )
    }
}

@Composable
private fun ActivityDefinitionPickerDialog(
    definitions: List<ActivityDefinition>,
    rowType: EntryType,
    onDismiss: () -> Unit,
    onSelect: (ActivityDefinition) -> Unit,
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.chooseEntityForType(rowType),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (definitions.isEmpty()) {
                    Text(
                        text = strings.noEntitiesForType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    definitions.forEach { definition ->
                        OutlinedButton(
                            onClick = { onSelect(definition) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 56.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ProjectIconAvatar(
                                    entryType = definition.type,
                                    projectIconPreset = definition.projectIconPreset,
                                    projectCustomIconBase64 = definition.projectCustomIconBase64,
                                    title = definition.title,
                                    size = 40.dp,
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = strings.extEntityDisplayLabel(definition.extCode, definition.title),
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = strings.baseDuration(formatHours(definition.defaultMinutes)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            AppActionButton(
                text = strings.closeLabel,
                onClick = onDismiss,
                variant = AppButtonVariant.TERTIARY,
                minHeight = 48.dp,
            )
        },
    )
}
