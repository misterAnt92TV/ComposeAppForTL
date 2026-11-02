package com.tlincompose.presentation.configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.tlincompose.core.appConfigurationDescription
import com.tlincompose.core.appConfigurationImport
import com.tlincompose.core.appConfigurationExport
import com.tlincompose.core.appConfigurationTitle
import com.tlincompose.core.appConfigurationWarning
import com.tlincompose.core.cancelLabel
import com.tlincompose.core.closeLabel
import com.tlincompose.core.backupImportConfirmTitle
import com.tlincompose.core.backupImportConfirmBody
import com.tlincompose.core.backupImportReplaceLabel
import com.tlincompose.presentation.JsonFilePickerLauncher
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec

@Composable
internal fun AppConfigurationDialog(
    state: AppConfigurationUiState,
    layoutSpec: AccessibilityLayoutSpec,
    jsonFilePickerLauncher: JsonFilePickerLauncher,
    onExport: () -> Unit,
    onImportSelected: (com.tlincompose.presentation.JsonFileSelection?) -> Unit,
    onConfirmImport: () -> Unit,
    onDismissImport: () -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = { if (!state.isBusy) onDismiss() },
        title = { Text(strings.appConfigurationTitle) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(strings.appConfigurationDescription); Text(strings.appConfigurationWarning, color = MaterialTheme.colorScheme.error) } },
        confirmButton = { TextButton(onClick = onDismiss, enabled = !state.isBusy, modifier = Modifier.testTag("close-app-configuration-button")) { Text(strings.closeLabel) } },
        dismissButton = {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppActionButton(text = strings.appConfigurationExport, onClick = onExport, enabled = !state.isBusy, variant = AppButtonVariant.SECONDARY, minHeight = layoutSpec.buttonMinHeight, minWidth = 44.dp, leadingIcon = Icons.AutoMirrored.Outlined.ExitToApp, modifier = Modifier.testTag("export-app-configuration-button"))
                AppActionButton(text = strings.appConfigurationImport, onClick = { jsonFilePickerLauncher.pickFile(onImportSelected) }, enabled = !state.isBusy, variant = AppButtonVariant.TERTIARY, minHeight = layoutSpec.buttonMinHeight, minWidth = 44.dp, leadingIcon = Icons.Outlined.Code, modifier = Modifier.testTag("import-app-configuration-button"))
            }
        },
    )
    if (state.pendingFileName != null) AlertDialog(
        onDismissRequest = onDismissImport,
        title = { Text(strings.backupImportConfirmTitle) },
        text = { Text(strings.backupImportConfirmBody(state.pendingFileName)) },
        confirmButton = { TextButton(onClick = onConfirmImport, modifier = Modifier.testTag("confirm-app-configuration-button")) { Text(strings.backupImportReplaceLabel) } },
        dismissButton = { TextButton(onClick = onDismissImport, modifier = Modifier.testTag("cancel-app-configuration-button")) { Text(strings.cancelLabel) } },
    )
}
