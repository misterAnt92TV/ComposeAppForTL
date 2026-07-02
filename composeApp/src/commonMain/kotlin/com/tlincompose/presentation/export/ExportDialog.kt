package com.tlincompose.presentation.export

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tlincompose.core.closeLabel
import com.tlincompose.core.displayName
import com.tlincompose.core.exportTypeFilterDescription
import com.tlincompose.core.exportTypeFilterRequiredMessage
import com.tlincompose.core.exportTypeFilterTitle
import com.tlincompose.core.label
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportActivityTypeFilter
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ExportDialog(
    title: String,
    description: String,
    layoutSpec: AccessibilityLayoutSpec,
    onDismiss: () -> Unit,
    onExport: (ExportFormat, ExportActivityTypeFilter) -> Unit,
) {
    val strings = LocalAppStrings.current
    var selectedTypes by remember {
        mutableStateOf(EntryType.entries.toSet())
    }
    val isSelectionValid = selectedTypes.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.exportTypeFilterTitle,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = strings.exportTypeFilterDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        EntryType.entries.forEach { type ->
                            FilterChip(
                                selected = type in selectedTypes,
                                onClick = {
                                    selectedTypes = if (type in selectedTypes) {
                                        selectedTypes - type
                                    } else {
                                        selectedTypes + type
                                    }
                                },
                                label = {
                                    Text(type.displayName(strings.language))
                                },
                            )
                        }
                    }
                    if (!isSelectionValid) {
                        Text(
                            text = strings.exportTypeFilterRequiredMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ExportFormat.entries.forEach { format ->
                        AppActionButton(
                            text = format.label(strings.language),
                            onClick = {
                                onExport(
                                    format,
                                    ExportActivityTypeFilter(selectedTypes),
                                )
                            },
                            enabled = isSelectionValid,
                            minHeight = layoutSpec.buttonMinHeight,
                            modifier = Modifier.sizeIn(minWidth = 112.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            AppActionButton(
                text = strings.closeLabel,
                onClick = onDismiss,
                variant = AppButtonVariant.TERTIARY,
                minHeight = layoutSpec.buttonMinHeight,
            )
        },
    )
}
