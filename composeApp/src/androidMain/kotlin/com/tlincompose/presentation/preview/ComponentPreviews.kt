package com.tlincompose.presentation.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tlincompose.core.StringKey
import com.tlincompose.presentation.BrandLogoPickerLauncher
import com.tlincompose.presentation.JsonFilePickerLauncher
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.ProjectIconPickerLauncher
import com.tlincompose.presentation.accessibility.AccessibilitySettingsSection
import com.tlincompose.presentation.accessibility.AccessibilityTextScaleUiState
import com.tlincompose.presentation.accessibility.DeveloperContactSection
import com.tlincompose.presentation.accessibility.PdfExportStyleUiState
import com.tlincompose.presentation.accessibility.SettingsBackupSection
import com.tlincompose.presentation.accessibility.SettingsBackupUiState
import com.tlincompose.presentation.calendar.CalendarSection
import com.tlincompose.presentation.calendar.DayEditorDialog
import com.tlincompose.presentation.catalog.ActivityCatalogSection
import com.tlincompose.presentation.catalog.ActivityDefinitionEditorDialog
import com.tlincompose.presentation.catalog.DeleteActivityDefinitionDialog
import com.tlincompose.presentation.catalog.ProjectIconAvatar
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.components.BrandingLogoPreview
import com.tlincompose.presentation.components.HeaderInfoPill
import com.tlincompose.presentation.components.StatusPill
import com.tlincompose.presentation.export.ExportDialog
import com.tlincompose.presentation.header.HeaderSection

private val previewProjectIconPickerLauncher = object : ProjectIconPickerLauncher {
    override fun pickImage(onImagePicked: (ByteArray?) -> Unit) = Unit
}

private val previewBrandLogoPickerLauncher = object : BrandLogoPickerLauncher {
    override fun pickImage(onImagePicked: (ByteArray?) -> Unit) = Unit
}

private val previewJsonFilePickerLauncher = object : JsonFilePickerLauncher {
    override fun pickFile(onFilePicked: (com.tlincompose.presentation.JsonFileSelection?) -> Unit) = Unit
}

@PreviewLightDark
@Composable
private fun AppButtonsPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(
        state = state,
        modifier = Modifier.widthIn(max = 520.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppActionButton(
                text = "Primario",
                onClick = {},
                minWidth = previewLayoutSpec(state).buttonPreferredWidth,
                maxWidth = previewLayoutSpec(state).buttonMaxWidth,
                modifier = Modifier.weight(1f),
            )
            AppActionButton(
                text = "Secondario",
                onClick = {},
                variant = AppButtonVariant.SECONDARY,
                minWidth = previewLayoutSpec(state).buttonPreferredWidth,
                maxWidth = previewLayoutSpec(state).buttonMaxWidth,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HeaderSectionWithBrandingLogoPreview() {
    val state = previewAccessibilityState(brandingLogoBase64 = previewBrandingLogoBase64)
    TLInComposePreviewSurface(
        state = state,
        modifier = Modifier.widthIn(min = 920.dp, max = 920.dp),
    ) {
        val strings = LocalAppStrings.current
        HeaderSection(
            monthSummary = previewMonthSummary,
            intervalMessage = strings[StringKey.SelectedMonthsReadyMessage("12/05/2026 - 16/05/2026")],
            isSelectingRange = true,
            isExportEnabled = true,
            accessibilityState = state,
            brandingLogoBase64 = state.brandingLogoBase64,
            isSettingsVisible = false,
            layoutSpec = previewLayoutSpec(state),
            onToggleRangeSelection = {},
            onExport = {},
            onToggleSettings = {},
            isActivityCatalogVisible = true,
            onToggleActivityCatalog = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HeaderSectionWithoutBrandingLogoPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(
        state = state,
        modifier = Modifier.widthIn(max = 520.dp),
    ) {
        val strings = LocalAppStrings.current
        HeaderSection(
            monthSummary = previewMonthSummary,
            intervalMessage = strings[StringKey.SelectedMonthsReadyMessage("12/05/2026 - 16/05/2026")],
            isSelectingRange = false,
            isExportEnabled = true,
            accessibilityState = state,
            brandingLogoBase64 = null,
            isSettingsVisible = false,
            layoutSpec = previewLayoutSpec(state),
            onToggleRangeSelection = {},
            onExport = {},
            onToggleSettings = {},
            isActivityCatalogVisible = false,
            onToggleActivityCatalog = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HeaderSectionCompactWithBrandingLogoPreview() {
    val state = previewAccessibilityState(
        brandingLogoBase64 = previewBrandingLogoBase64,
        comfortableSpacing = true,
    )
    TLInComposePreviewSurface(
        state = state,
        modifier = Modifier.widthIn(max = 460.dp),
    ) {
        val strings = LocalAppStrings.current
        HeaderSection(
            monthSummary = previewMonthSummary,
            intervalMessage = strings[StringKey.SelectedMonthsReadyMessage("12/05/2026 - 16/05/2026")],
            isSelectingRange = false,
            isExportEnabled = true,
            accessibilityState = state,
            brandingLogoBase64 = state.brandingLogoBase64,
            isSettingsVisible = false,
            layoutSpec = previewLayoutSpec(state),
            onToggleRangeSelection = {},
            onExport = {},
            onToggleSettings = {},
            isActivityCatalogVisible = true,
            onToggleActivityCatalog = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HeaderSectionCompactLargeTextPreview() {
    val state = previewAccessibilityState(
        brandingLogoBase64 = previewBrandingLogoBase64,
        comfortableSpacing = true,
        textScale = AccessibilityTextScaleUiState.LARGE,
    )
    TLInComposePreviewSurface(
        state = state,
        modifier = Modifier.widthIn(max = 420.dp),
    ) {
        val strings = LocalAppStrings.current
        HeaderSection(
            monthSummary = previewMonthSummary,
            intervalMessage = strings[StringKey.SelectedMonthsReadyMessage("12/05/2026 - 16/05/2026")],
            isSelectingRange = true,
            isExportEnabled = true,
            accessibilityState = state,
            brandingLogoBase64 = state.brandingLogoBase64,
            isSettingsVisible = false,
            layoutSpec = previewLayoutSpec(state),
            onToggleRangeSelection = {},
            onExport = {},
            onToggleSettings = {},
            isActivityCatalogVisible = true,
            onToggleActivityCatalog = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun CalendarSectionPreview() {
    val state = previewAccessibilityState(
        textScale = AccessibilityTextScaleUiState.LARGE,
    )
    TLInComposePreviewSurface(state = state) {
        val strings = LocalAppStrings.current
        CalendarSection(
            month = previewMonth,
            grid = previewCalendarCells(strings),
            accessibilityState = state,
            layoutSpec = previewLayoutSpec(state),
            onPreviousMonth = {},
            onNextMonth = {},
            onMonthSelected = {},
            onDaySelected = {},
            onDayDragStarted = {},
            onDayDragMoved = {},
            onDayDragCompleted = {},
            onDayDragCancelled = {},
            onActivityDragStarted = { _, _ -> },
            onActivityDragMoved = {},
            onActivityDragCompleted = {},
            onActivityDragCancelled = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun AccessibilitySettingsSectionWidePreview() {
    val state = previewAccessibilityState(
        textScale = AccessibilityTextScaleUiState.LARGE,
        comfortableSpacing = true,
        standardWorkdayMinutes = 450,
        exportUserFullName = "Mario Rossi",
        exportOfficeName = "Sede Milano",
        exportEmployeeId = "EMP-12345",
        exportPersonId = "P-67890",
        brandingLogoBase64 = previewBrandingLogoBase64,
        pdfExportStyle = PdfExportStyleUiState.SIMPLE_TABLE,
    )
    TLInComposePreviewSurface(
        state = state,
        modifier = Modifier.widthIn(min = 1040.dp, max = 1040.dp),
    ) {
        AccessibilitySettingsSection(
            state = state,
            layoutSpec = previewLayoutSpec(state),
            brandLogoPickerLauncher = previewBrandLogoPickerLauncher,
            onThemeModeChanged = {},
            onTextScaleChanged = {},
            onHighContrastChanged = {},
            onComfortableSpacingChanged = {},
            onFocusModeChanged = {},
            onReduceMotionChanged = {},
            onLanguageChanged = {},
            onStandardWorkdayChanged = {},
            onExportUserFullNameChanged = {},
            onExportOfficeNameChanged = {},
            onExportEmployeeIdChanged = {},
            onExportPersonIdChanged = {},
            onPdfExportStyleChanged = {},
            onPickBrandingLogo = {},
            onClearBrandingLogo = {},
            settingsBackupState = SettingsBackupUiState(),
            jsonFilePickerLauncher = previewJsonFilePickerLauncher,
            onExportBackup = {},
            onImportBackupSelected = {},
            onConfirmImport = {},
            onDismissImportConfirmation = {},
            onClose = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun AccessibilitySettingsSectionNarrowPreview() {
    val state = previewAccessibilityState(
        textScale = AccessibilityTextScaleUiState.LARGE,
        comfortableSpacing = true,
        standardWorkdayMinutes = 450,
    )
    TLInComposePreviewSurface(
        state = state,
        modifier = Modifier.widthIn(max = 520.dp),
    ) {
        AccessibilitySettingsSection(
            state = state,
            layoutSpec = previewLayoutSpec(state),
            brandLogoPickerLauncher = previewBrandLogoPickerLauncher,
            onThemeModeChanged = {},
            onTextScaleChanged = {},
            onHighContrastChanged = {},
            onComfortableSpacingChanged = {},
            onFocusModeChanged = {},
            onReduceMotionChanged = {},
            onLanguageChanged = {},
            onStandardWorkdayChanged = {},
            onExportUserFullNameChanged = {},
            onExportOfficeNameChanged = {},
            onExportEmployeeIdChanged = {},
            onExportPersonIdChanged = {},
            onPdfExportStyleChanged = {},
            onPickBrandingLogo = {},
            onClearBrandingLogo = {},
            settingsBackupState = SettingsBackupUiState(),
            jsonFilePickerLauncher = previewJsonFilePickerLauncher,
            onExportBackup = {},
            onImportBackupSelected = {},
            onConfirmImport = {},
            onDismissImportConfirmation = {},
            onClose = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsBackupSectionPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(state = state, modifier = Modifier.widthIn(max = 560.dp)) {
        SettingsBackupSection(
            state = SettingsBackupUiState(),
            layoutSpec = previewLayoutSpec(state),
            jsonFilePickerLauncher = previewJsonFilePickerLauncher,
            onExportBackup = {},
            onImportBackupSelected = {},
            onConfirmImport = {},
            onDismissImportConfirmation = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsBackupSectionConfirmPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(state = state, modifier = Modifier.widthIn(max = 560.dp)) {
        SettingsBackupSection(
            state = SettingsBackupUiState(pendingImportFileName = "TLInCompose_backup_2026-07-01_10-15-00.json"),
            layoutSpec = previewLayoutSpec(state),
            jsonFilePickerLauncher = previewJsonFilePickerLauncher,
            onExportBackup = {},
            onImportBackupSelected = {},
            onConfirmImport = {},
            onDismissImportConfirmation = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DeveloperContactSectionPreview() {
    val state = previewAccessibilityState(
        textScale = AccessibilityTextScaleUiState.LARGE,
        comfortableSpacing = true,
    )
    TLInComposePreviewSurface(state = state, modifier = Modifier.widthIn(max = 560.dp)) {
        DeveloperContactSection(
            strings = LocalAppStrings.current,
        )
    }
}

@PreviewLightDark
@Composable
private fun ActivityCatalogSectionPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(state = state) {
        val strings = LocalAppStrings.current
        ActivityCatalogSection(
            definitions = previewActivityDefinitions(strings),
            layoutSpec = previewLayoutSpec(state),
            defaultWorkdayMinutes = state.standardWorkdayMinutes,
            onCreateDefinition = {},
            onEditDefinition = {},
            onDeleteDefinition = {},
            onClose = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DayEditorDialogPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(state = state) {
        val strings = LocalAppStrings.current
        DayEditorDialog(
            state = previewDayEditorState(strings),
            availableDefinitions = previewActivityDefinitions(strings),
            layoutSpec = previewLayoutSpec(state),
            onDismiss = {},
            onAddRow = {},
            onRemoveRow = {},
            onTypeChanged = { _, _ -> },
            onDefinitionSelected = { _, _ -> },
            onHoursChanged = { _, _ -> },
            onWorkLocationChanged = { _, _ -> },
            onSave = {},
            onCoverIncompleteMonth = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun ActivityDefinitionEditorDialogPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(state = state) {
        val strings = LocalAppStrings.current
        ActivityDefinitionEditorDialog(
            state = previewActivityDefinitionEditorState(strings),
            layoutSpec = previewLayoutSpec(state),
            projectIconPickerLauncher = previewProjectIconPickerLauncher,
            onDismiss = {},
            onTypeChanged = {},
            onExtCodeChanged = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onDurationChanged = {},
            onProjectUrlChanged = {},
            onProjectIconPresetChanged = {},
            onPickProjectCustomIcon = {},
            onClearProjectIcon = {},
            onSave = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DeleteActivityDefinitionDialogPreview() {
    TLInComposePreviewSurface {
        val strings = LocalAppStrings.current
        DeleteActivityDefinitionDialog(
            definition = previewActivityDefinitions(strings).first(),
            onDismiss = {},
            onConfirmDelete = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun ExportDialogPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(state = state) {
        val strings = LocalAppStrings.current
        ExportDialog(
            title = strings[StringKey.ExportPeriodTitle("12/05/2026 - 16/05/2026")],
            description = strings[StringKey.ExportSelectedMonthsDescription],
            layoutSpec = previewLayoutSpec(state),
            onDismiss = {},
            onExport = { _, _ -> },
        )
    }
}

@PreviewLightDark
@Composable
private fun ReusableComponentsPreview() {
    TLInComposePreviewSurface {
        val strings = LocalAppStrings.current
        val definitions = previewActivityDefinitions(strings)
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HeaderInfoPill(text = strings[StringKey.WeekStartsMonday])
                HeaderInfoPill(text = strings[StringKey.StandardWorkday("8")])
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusPill(
                    text = strings[StringKey.TodayLabel],
                    background = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                StatusPill(
                    text = strings[StringKey.ActivityCount(3)],
                    background = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProjectIconAvatar(
                    entryType = definitions.first().type,
                    projectIconPreset = definitions.first().projectIconPreset,
                    projectCustomIconBase64 = null,
                    title = definitions.first().title,
                    size = 56.dp,
                )
                ProjectIconAvatar(
                    entryType = definitions[1].type,
                    projectIconPreset = definitions[1].projectIconPreset,
                    projectCustomIconBase64 = null,
                    title = definitions[1].title,
                    size = 56.dp,
                )
                ProjectIconAvatar(
                    entryType = definitions.last().type,
                    projectIconPreset = definitions.last().projectIconPreset,
                    projectCustomIconBase64 = null,
                    title = definitions.last().title,
                    size = 56.dp,
                )
                ProjectIconAvatar(
                    entryType = definitions.first().type,
                    projectIconPreset = null,
                    projectCustomIconBase64 = null,
                    title = definitions.first().title,
                    size = 56.dp,
                    showPlaceholderWhenEmpty = true,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProjectIconAvatar(
                    entryType = definitions.first().type,
                    projectIconPreset = definitions.first().projectIconPreset,
                    projectCustomIconBase64 = null,
                    title = strings[StringKey.ActivityCatalogTitle],
                    size = 36.dp,
                )
                ProjectIconAvatar(
                    entryType = definitions[1].type,
                    projectIconPreset = definitions[1].projectIconPreset,
                    projectCustomIconBase64 = null,
                    title = definitions[1].title,
                    size = 36.dp,
                )
                ProjectIconAvatar(
                    entryType = definitions.last().type,
                    projectIconPreset = definitions.last().projectIconPreset,
                    projectCustomIconBase64 = null,
                    title = definitions.last().title,
                    size = 36.dp,
                )
                ProjectIconAvatar(
                    entryType = definitions.first().type,
                    projectIconPreset = null,
                    projectCustomIconBase64 = null,
                    title = strings[StringKey.ActivityCatalogTitle],
                    size = 36.dp,
                    showPlaceholderWhenEmpty = true,
                )
            }
                BrandingLogoPreview(
                    brandingLogoBase64 = previewBrandingLogoBase64,
                    contentDescription = strings[StringKey.BrandingLogoDescription("TLInCompose")],
                modifier = androidx.compose.ui.Modifier.sizeIn(minWidth = 160.dp, minHeight = 56.dp),
                showPlaceholderWhenEmpty = true,
            )
        }
    }
}
