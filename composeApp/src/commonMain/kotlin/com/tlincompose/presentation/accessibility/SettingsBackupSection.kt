package com.tlincompose.presentation.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.tlincompose.core.backupImportConfirmBody
import com.tlincompose.core.backupImportConfirmTitle
import com.tlincompose.core.backupImportReplaceLabel
import com.tlincompose.core.backupImportingLabel
import com.tlincompose.core.backupJsonDescription
import com.tlincompose.core.backupJsonExportLabel
import com.tlincompose.core.backupJsonImportLabel
import com.tlincompose.core.backupJsonTitle
import com.tlincompose.core.backupJsonWarning
import com.tlincompose.core.backupJsonExportingLabel
import com.tlincompose.core.cancelLabel
import com.tlincompose.presentation.JsonFilePickerLauncher
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun SettingsBackupSection(
    state: SettingsBackupUiState,
    layoutSpec: AccessibilityLayoutSpec,
    jsonFilePickerLauncher: JsonFilePickerLauncher,
    onExportBackup: () -> Unit,
    onImportBackupSelected: (com.tlincompose.presentation.JsonFileSelection?) -> Unit,
    onConfirmImport: () -> Unit,
    onDismissImportConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings-backup-section"),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = strings.backupJsonTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = strings.backupJsonDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = strings.backupJsonWarning,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppActionButton(
                    text = if (state.isExporting) {
                        strings.backupJsonExportingLabel
                    } else {
                        strings.backupJsonExportLabel
                    },
                    onClick = onExportBackup,
                    enabled = !state.isBusy,
                    variant = AppButtonVariant.SECONDARY,
                    minHeight = layoutSpec.buttonMinHeight,
                    minWidth = layoutSpec.buttonPreferredWidth,
                    maxWidth = 240.dp,
                    modifier = Modifier
                        .widthIn(min = 180.dp)
                        .testTag("export-backup-button"),
                    leadingIcon = Icons.AutoMirrored.Outlined.ExitToApp,
                )
                AppActionButton(
                    text = if (state.isImporting) {
                        strings.backupImportingLabel
                    } else {
                        strings.backupJsonImportLabel
                    },
                    onClick = {
                        jsonFilePickerLauncher.pickFile(onImportBackupSelected)
                    },
                    enabled = !state.isBusy,
                    variant = AppButtonVariant.TERTIARY,
                    minHeight = layoutSpec.buttonMinHeight,
                    minWidth = layoutSpec.buttonPreferredWidth,
                    maxWidth = 240.dp,
                    modifier = Modifier
                        .widthIn(min = 180.dp)
                        .testTag("import-backup-button"),
                    leadingIcon = Icons.Outlined.Code,
                )
            }
        }
    }

    if (state.isImportConfirmationVisible) {
        AlertDialog(
            onDismissRequest = onDismissImportConfirmation,
            title = {
                Text(strings.backupImportConfirmTitle)
            },
            text = {
                Text(
                    text = strings.backupImportConfirmBody(state.pendingImportFileName.orEmpty()),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmImport,
                    modifier = Modifier.testTag("confirm-import-backup-button"),
                ) {
                    Text(strings.backupImportReplaceLabel)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissImportConfirmation,
                    modifier = Modifier.testTag("cancel-import-backup-button"),
                ) {
                    Text(strings.cancelLabel)
                }
            },
        )
    }
}
