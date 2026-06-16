package com.tlincompose.presentation.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tlincompose.core.*
import com.tlincompose.presentation.BrandLogoPickerLauncher
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.ProjectIconPickerLauncher
import com.tlincompose.presentation.accessibility.AccessibilitySettingsSection
import com.tlincompose.presentation.accessibility.AccessibilityTextScaleUiState
import com.tlincompose.presentation.accessibility.PdfExportStyleUiState
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

@PreviewLightDark
@Composable
private fun AppButtonsPreview() {
    TLInComposePreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppActionButton(
                text = "Primario",
                onClick = {},
                modifier = androidx.compose.ui.Modifier.weight(1f),
            )
            AppActionButton(
                text = "Secondario",
                onClick = {},
                variant = AppButtonVariant.SECONDARY,
                modifier = androidx.compose.ui.Modifier.weight(1f),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HeaderSectionWithBrandingLogoPreview() {
    val state = previewAccessibilityState(brandingLogoBase64 = previewBrandingLogoBase64)
    TLInComposePreviewSurface(state = state) {
        val strings = LocalAppStrings.current
        HeaderSection(
            month = previewMonth,
            monthSummary = previewMonthSummary,
            intervalMessage = strings.selectedMonthsReadyMessage("12/05/2026 - 16/05/2026"),
            isSelectingRange = true,
            isExportEnabled = true,
            accessibilityState = state,
            brandingLogoBase64 = state.brandingLogoBase64,
            isSettingsVisible = false,
            layoutSpec = previewLayoutSpec(state),
            onPreviousMonth = {},
            onNextMonth = {},
            onToggleRangeSelection = {},
            onExport = {},
            onToggleSettings = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HeaderSectionWithoutBrandingLogoPreview() {
    val state = previewAccessibilityState()
    TLInComposePreviewSurface(state = state) {
        val strings = LocalAppStrings.current
        HeaderSection(
            month = previewMonth,
            monthSummary = previewMonthSummary,
            intervalMessage = strings.selectedMonthsReadyMessage("12/05/2026 - 16/05/2026"),
            isSelectingRange = false,
            isExportEnabled = true,
            accessibilityState = state,
            brandingLogoBase64 = null,
            isSettingsVisible = false,
            layoutSpec = previewLayoutSpec(state),
            onPreviousMonth = {},
            onNextMonth = {},
            onToggleRangeSelection = {},
            onExport = {},
            onToggleSettings = {},
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
            grid = previewCalendarCells(strings),
            accessibilityState = state,
            layoutSpec = previewLayoutSpec(state),
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
private fun AccessibilitySettingsSectionWithBrandingLogoPreview() {
    val state = previewAccessibilityState(
        textScale = AccessibilityTextScaleUiState.LARGE,
        comfortableSpacing = true,
        standardWorkdayMinutes = 450,
        brandingLogoBase64 = previewBrandingLogoBase64,
        pdfExportStyle = PdfExportStyleUiState.SIMPLE_TABLE,
    )
    TLInComposePreviewSurface(state = state) {
        AccessibilitySettingsSection(
            state = state,
            layoutSpec = previewLayoutSpec(state),
            brandLogoPickerLauncher = previewBrandLogoPickerLauncher,
            onThemeModeChanged = {},
            onTextScaleChanged = {},
            onHighContrastChanged = {},
            onComfortableSpacingChanged = {},
            onFocusModeChanged = {},
            onLanguageChanged = {},
            onStandardWorkdayChanged = {},
            onPdfExportStyleChanged = {},
            onPickBrandingLogo = {},
            onClearBrandingLogo = {},
            onClose = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun AccessibilitySettingsSectionWithoutBrandingLogoPreview() {
    val state = previewAccessibilityState(
        textScale = AccessibilityTextScaleUiState.LARGE,
        comfortableSpacing = true,
        standardWorkdayMinutes = 450,
    )
    TLInComposePreviewSurface(state = state) {
        AccessibilitySettingsSection(
            state = state,
            layoutSpec = previewLayoutSpec(state),
            brandLogoPickerLauncher = previewBrandLogoPickerLauncher,
            onThemeModeChanged = {},
            onTextScaleChanged = {},
            onHighContrastChanged = {},
            onComfortableSpacingChanged = {},
            onFocusModeChanged = {},
            onLanguageChanged = {},
            onStandardWorkdayChanged = {},
            onPdfExportStyleChanged = {},
            onPickBrandingLogo = {},
            onClearBrandingLogo = {},
            onClose = {},
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
            onSave = {},
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
            title = strings.exportPeriodTitle("12/05/2026 - 16/05/2026"),
            description = strings.exportSelectedMonthsDescription,
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
                HeaderInfoPill(text = strings.weekStartsMonday)
                HeaderInfoPill(text = strings.standardWorkday("8"))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusPill(
                    text = strings.todayLabel,
                    background = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                StatusPill(
                    text = strings.activityCountPhrase(3),
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
                    title = strings.activityCatalogTitle,
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
                    title = strings.activityCatalogTitle,
                    size = 36.dp,
                    showPlaceholderWhenEmpty = true,
                )
            }
            BrandingLogoPreview(
                brandingLogoBase64 = previewBrandingLogoBase64,
                contentDescription = strings.brandingLogoContentDescription(strings.appName),
                modifier = androidx.compose.ui.Modifier.sizeIn(minWidth = 160.dp, minHeight = 56.dp),
                showPlaceholderWhenEmpty = true,
            )
        }
    }
}
