package com.tlincompose.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tlincompose.core.APP_VERSION
import com.tlincompose.core.checkDayFieldsBeforeSaving
import com.tlincompose.core.checkEntityFieldsBeforeSaving
import com.tlincompose.core.chooseLighterImage
import com.tlincompose.core.hideActivityCatalog
import com.tlincompose.core.showActivityCatalog
import com.tlincompose.core.unableToCopyDraggedActivity
import com.tlincompose.core.unableToDeleteEntity
import com.tlincompose.core.unableToPrepareExport
import com.tlincompose.core.unableToSaveEntity
import com.tlincompose.core.unableToSaveSelectedDay
import com.tlincompose.core.unableToSaveSelectedDays
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.presentation.BrandLogoPickerLauncher
import com.tlincompose.presentation.FileSaveLauncher
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.ProjectIconPickerLauncher
import com.tlincompose.presentation.TimesheetController
import com.tlincompose.presentation.accessibility.AccessibilitySettingsSection
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState
import com.tlincompose.presentation.accessibility.AccessibilityTextScaleUiState
import com.tlincompose.presentation.accessibility.PdfExportStyleUiState
import com.tlincompose.presentation.accessibility.ThemeModeUiState
import com.tlincompose.presentation.calendar.CalendarSection
import com.tlincompose.presentation.calendar.DayEditorDialog
import com.tlincompose.presentation.catalog.ActivityCatalogController
import com.tlincompose.presentation.catalog.ActivityCatalogSection
import com.tlincompose.presentation.catalog.ActivityDefinitionEditorDialog
import com.tlincompose.presentation.catalog.DeleteActivityDefinitionDialog
import com.tlincompose.presentation.components.AppActionButton
import com.tlincompose.presentation.components.AppButtonVariant
import com.tlincompose.presentation.export.ExportDialog
import com.tlincompose.presentation.header.HeaderSection
import com.tlincompose.presentation.layout.accessibilityLayoutSpec

@Composable
internal fun TimesheetScreen(
    controller: TimesheetController,
    activityCatalogController: ActivityCatalogController,
    accessibilityState: AccessibilitySettingsUiState,
    onThemeModeChanged: (ThemeModeUiState) -> Unit,
    onTextScaleChanged: (AccessibilityTextScaleUiState) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onComfortableSpacingChanged: (Boolean) -> Unit,
    onFocusModeChanged: (Boolean) -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
    onStandardWorkdayChanged: (Int) -> Unit,
    onExportUserFullNameChanged: (String) -> Unit,
    onPdfExportStyleChanged: (PdfExportStyleUiState) -> Unit,
    fileSaveLauncher: FileSaveLauncher,
    brandLogoPickerLauncher: BrandLogoPickerLauncher,
    projectIconPickerLauncher: ProjectIconPickerLauncher,
    onPickBrandingLogo: (ByteArray) -> Unit,
    onClearBrandingLogo: () -> Unit,
    showMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val layoutSpec = accessibilityLayoutSpec(accessibilityState)
    var isSettingsVisible by rememberSaveable { mutableStateOf(false) }
    var isActivityCatalogVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(accessibilityState.standardWorkdayMinutes) {
        controller.updateStandardWorkdayMinutes(accessibilityState.standardWorkdayMinutes)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = layoutSpec.screenPadding, vertical = layoutSpec.screenPadding),
        verticalArrangement = Arrangement.spacedBy(layoutSpec.sectionSpacing),
    ) {
        HeaderSection(
            month = controller.currentMonth,
            monthSummary = controller.monthSummary,
            intervalMessage = controller.intervalSelectionMessage,
            isSelectingRange = controller.rangeSelectionState.isSelecting,
            isExportEnabled = controller.isExportEnabled,
            accessibilityState = accessibilityState,
            brandingLogoBase64 = accessibilityState.brandingLogoBase64,
            isSettingsVisible = isSettingsVisible,
            layoutSpec = layoutSpec,
            onPreviousMonth = controller::loadPreviousMonth,
            onNextMonth = controller::loadNextMonth,
            onToggleRangeSelection = controller::toggleRangeSelection,
            onExport = controller::openExportDialog,
            onToggleSettings = {
                isSettingsVisible = !isSettingsVisible
            },
            onMonthSelected = { month -> controller.goToMonth(month) }
        )
        if (isSettingsVisible) {
            Box(modifier = Modifier.testTag("settings-panel")) {
                AccessibilitySettingsSection(
                    state = accessibilityState,
                    layoutSpec = layoutSpec,
                    brandLogoPickerLauncher = brandLogoPickerLauncher,
                    onThemeModeChanged = onThemeModeChanged,
                    onTextScaleChanged = onTextScaleChanged,
                    onHighContrastChanged = onHighContrastChanged,
                    onComfortableSpacingChanged = onComfortableSpacingChanged,
                    onFocusModeChanged = onFocusModeChanged,
                    onLanguageChanged = onLanguageChanged,
                    onStandardWorkdayChanged = onStandardWorkdayChanged,
                    onExportUserFullNameChanged = onExportUserFullNameChanged,
                    onPdfExportStyleChanged = onPdfExportStyleChanged,
                    onPickBrandingLogo = onPickBrandingLogo,
                    onClearBrandingLogo = onClearBrandingLogo,
                    onClose = {
                        isSettingsVisible = false
                    },
                )
            }
        }
        ActivityCatalogToggleButton(
            isVisible = isActivityCatalogVisible,
            buttonMinHeight = layoutSpec.buttonMinHeight,
            onClick = {
                isActivityCatalogVisible = !isActivityCatalogVisible
            },
        )
        if (isActivityCatalogVisible) {
            Box(modifier = Modifier.testTag("activity-catalog-panel")) {
                ActivityCatalogSection(
                    definitions = activityCatalogController.definitions,
                    layoutSpec = layoutSpec,
                    defaultWorkdayMinutes = accessibilityState.standardWorkdayMinutes,
                    onCreateDefinition = {
                        activityCatalogController.openCreateEditor(accessibilityState.standardWorkdayMinutes)
                    },
                    onEditDefinition = activityCatalogController::openEditEditor,
                    onDeleteDefinition = activityCatalogController::requestDelete,
                    onClose = {
                        isActivityCatalogVisible = false
                    },
                )
            }
        }
        CalendarSection(
            grid = controller.monthCells,
            accessibilityState = accessibilityState,
            layoutSpec = layoutSpec,
            onDaySelected = controller::onDayTapped,
            onDayDragStarted = controller::startDayDragSelection,
            onDayDragMoved = controller::updateDayDragSelection,
            onDayDragCompleted = controller::completeDayDragSelection,
            onDayDragCancelled = controller::cancelDayDragSelection,
            onActivityDragStarted = controller::startActivityDrag,
            onActivityDragMoved = controller::updateActivityDragTarget,
            onActivityDragCompleted = {
                controller.completeActivityDrag(
                    onPersistenceError = {
                        showMessage(strings.unableToCopyDraggedActivity)
                    },
                )
            },
            onActivityDragCancelled = controller::cancelActivityDrag,
        )
        Text(
            text = APP_VERSION,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }

    controller.editorState?.let { editorState ->
        DayEditorDialog(
            state = editorState,
            availableDefinitions = activityCatalogController.definitions,
            layoutSpec = layoutSpec,
            onDismiss = controller::dismissEditor,
            onAddRow = controller::addDraftRow,
            onRemoveRow = controller::removeDraftRow,
            onTypeChanged = controller::updateDraftType,
            onDefinitionSelected = controller::updateDraftSelection,
            onHoursChanged = controller::updateDraftHours,
            onSave = {
                controller.saveEditor(
                    language = accessibilityState.language,
                    onValidationError = {
                        showMessage(strings.checkDayFieldsBeforeSaving)
                    },
                    onPersistenceError = {
                        showMessage(
                            if (editorState.target.isRange) {
                                strings.unableToSaveSelectedDays
                            } else {
                                strings.unableToSaveSelectedDay
                            },
                        )
                    },
                )
            },
        )
    }

    activityCatalogController.editorState?.let { editorState ->
        ActivityDefinitionEditorDialog(
            state = editorState,
            layoutSpec = layoutSpec,
            projectIconPickerLauncher = projectIconPickerLauncher,
            onDismiss = activityCatalogController::dismissEditor,
            onTypeChanged = activityCatalogController::updateDraftType,
            onExtCodeChanged = activityCatalogController::updateDraftExtCode,
            onTitleChanged = activityCatalogController::updateDraftTitle,
            onDescriptionChanged = activityCatalogController::updateDraftDescription,
            onDurationChanged = activityCatalogController::updateDraftDuration,
            onProjectUrlChanged = activityCatalogController::updateDraftProjectUrl,
            onProjectIconPresetChanged = activityCatalogController::updateDraftProjectIconPreset,
            onPickProjectCustomIcon = { imageBytes ->
                activityCatalogController.updateDraftProjectCustomIcon(
                    imageBytes = imageBytes,
                    onIconTooLarge = {
                        showMessage(strings.chooseLighterImage(maxKilobytes = 256))
                    },
                )
            },
            onClearProjectIcon = activityCatalogController::clearDraftProjectIcon,
            onSave = {
                activityCatalogController.saveEditor(
                    language = accessibilityState.language,
                    onValidationError = {
                        showMessage(strings.checkEntityFieldsBeforeSaving)
                    },
                    onPersistenceError = {
                        showMessage(strings.unableToSaveEntity)
                    },
                    onSuccess = controller::refreshCurrentMonth,
                )
            },
        )
    }

    activityCatalogController.definitionPendingDelete?.let { definition ->
        DeleteActivityDefinitionDialog(
            definition = definition,
            onDismiss = activityCatalogController::dismissDeleteRequest,
            onConfirmDelete = {
                activityCatalogController.confirmDelete(
                    onFailure = {
                        showMessage(strings.unableToDeleteEntity)
                    },
                )
            },
        )
    }

    if (controller.isExportDialogVisible) {
        ExportDialog(
            title = controller.exportDialogTitle,
            description = controller.exportDialogDescription,
            layoutSpec = layoutSpec,
            onDismiss = controller::dismissExportDialog,
            onExport = { format, filter ->
                controller.exportDocument(
                    language = accessibilityState.language,
                    format = format,
                    filter = filter,
                    exportUserFullName = accessibilityState.exportUserFullName,
                    brandingLogoBase64 = accessibilityState.brandingLogoBase64,
                    pdfExportStyle = accessibilityState.pdfExportStyle.toDomain(),
                    onSuccess = { document ->
                        fileSaveLauncher.save(document)
                        controller.dismissExportDialog()
                    },
                    onFailure = {
                        showMessage(strings.unableToPrepareExport)
                    },
                )
            },
        )
    }
}

private fun PdfExportStyleUiState.toDomain(): com.tlincompose.domain.model.PdfExportStyle = when (this) {
    PdfExportStyleUiState.RETRO -> com.tlincompose.domain.model.PdfExportStyle.RETRO
    PdfExportStyleUiState.SIMPLE_TABLE -> com.tlincompose.domain.model.PdfExportStyle.SIMPLE_TABLE
    PdfExportStyleUiState.COMPACT_LIST -> com.tlincompose.domain.model.PdfExportStyle.COMPACT_LIST
    PdfExportStyleUiState.DETAIL_BLOCKS -> com.tlincompose.domain.model.PdfExportStyle.DETAIL_BLOCKS
}

@Composable
private fun ActivityCatalogToggleButton(
    isVisible: Boolean,
    buttonMinHeight: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val strings = LocalAppStrings.current
    AppActionButton(
        text = if (isVisible) {
            strings.hideActivityCatalog
        } else {
            strings.showActivityCatalog
        },
        onClick = onClick,
        variant = AppButtonVariant.SECONDARY,
        minHeight = buttonMinHeight,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity-catalog-toggle-button"),
    )
}
