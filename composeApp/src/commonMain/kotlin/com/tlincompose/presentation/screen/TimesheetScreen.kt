package com.tlincompose.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tlincompose.core.AppVersion
import com.tlincompose.core.backupExportErrorMessage
import com.tlincompose.core.backupImportEmptyContentMessage
import com.tlincompose.core.backupImportInvalidJsonMessage
import com.tlincompose.core.backupImportPersistenceErrorMessage
import com.tlincompose.core.backupImportSuccessMessage
import com.tlincompose.core.backupImportUnsupportedVersionMessage
import com.tlincompose.core.appConfigurationExportError
import com.tlincompose.core.appConfigurationImportError
import com.tlincompose.core.appConfigurationSuccess
import com.tlincompose.core.checkDayFieldsBeforeSaving
import com.tlincompose.core.checkEntityFieldsBeforeSaving
import com.tlincompose.core.chooseLighterImage
import com.tlincompose.core.unableToCopyDraggedActivity
import com.tlincompose.core.unableToDeleteEntity
import com.tlincompose.core.unableToCoverIncompleteMonth
import com.tlincompose.core.unableToPrepareExport
import com.tlincompose.core.unableToSaveEntity
import com.tlincompose.core.unableToSaveSelectedDay
import com.tlincompose.core.unableToSaveSelectedDays
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.presentation.BrandLogoPickerLauncher
import com.tlincompose.presentation.FileSaveLauncher
import com.tlincompose.presentation.JsonFilePickerLauncher
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.ProjectIconPickerLauncher
import com.tlincompose.presentation.TimesheetController
import com.tlincompose.presentation.accessibility.AccessibilitySettingsSection
import com.tlincompose.presentation.accessibility.SettingsBackupController
import com.tlincompose.presentation.accessibility.SettingsBackupFailureReason
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
import com.tlincompose.presentation.configuration.AppConfigurationController
import com.tlincompose.presentation.configuration.AppConfigurationDialog
import com.tlincompose.presentation.export.ExportDialog
import com.tlincompose.presentation.header.HeaderSection
import com.tlincompose.presentation.layout.accessibilityLayoutSpec

@Composable
internal fun TimesheetScreen(
    controller: TimesheetController,
    activityCatalogController: ActivityCatalogController,
    settingsBackupController: SettingsBackupController,
    appConfigurationController: AppConfigurationController,
    accessibilityState: AccessibilitySettingsUiState,
    onThemeModeChanged: (ThemeModeUiState) -> Unit,
    onTextScaleChanged: (AccessibilityTextScaleUiState) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onComfortableSpacingChanged: (Boolean) -> Unit,
    onFocusModeChanged: (Boolean) -> Unit,
    onReduceMotionChanged: (Boolean) -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
    onStandardWorkdayChanged: (Int) -> Unit,
    onExportUserFullNameChanged: (String) -> Unit,
    onExportOfficeNameChanged: (String) -> Unit,
    onExportEmployeeIdChanged: (String) -> Unit,
    onExportPersonIdChanged: (String) -> Unit,
    onPdfExportStyleChanged: (PdfExportStyleUiState) -> Unit,
    fileSaveLauncher: FileSaveLauncher,
    brandLogoPickerLauncher: BrandLogoPickerLauncher,
    projectIconPickerLauncher: ProjectIconPickerLauncher,
    jsonFilePickerLauncher: JsonFilePickerLauncher,
    onPickBrandingLogo: (ByteArray) -> Unit,
    onClearBrandingLogo: () -> Unit,
    onRefreshAfterBackupImport: () -> Unit,
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

    LaunchedEffect(activityCatalogController.definitions) {
        controller.updateAvailableDefinitions(activityCatalogController.definitions)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = layoutSpec.screenPadding, vertical = layoutSpec.screenPadding),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = layoutSpec.contentMaxWidth),
                verticalArrangement = Arrangement.spacedBy(layoutSpec.sectionSpacing),
            ) {
                HeaderSection(
                    monthSummary = controller.monthSummary,
                    intervalMessage = controller.intervalSelectionMessage,
                    isSelectingRange = controller.rangeSelectionState.isSelecting,
                    isExportEnabled = controller.isExportEnabled,
                    accessibilityState = accessibilityState,
                    brandingLogoBase64 = accessibilityState.brandingLogoBase64,
                    isSettingsVisible = isSettingsVisible,
                    layoutSpec = layoutSpec,
                    onToggleRangeSelection = controller::toggleRangeSelection,
                    onExport = controller::openExportDialog,
                    onToggleSettings = {
                        isSettingsVisible = !isSettingsVisible
                    },
                    isActivityCatalogVisible = isActivityCatalogVisible,
                    onToggleActivityCatalog = {
                        isActivityCatalogVisible = !isActivityCatalogVisible
                    },
                )
                AnimatedVisibility(
                    visible = isSettingsVisible,
                    enter = if (accessibilityState.reduceMotion) EnterTransition.None else fadeIn() + expandVertically(),
                    exit = if (accessibilityState.reduceMotion) ExitTransition.None else fadeOut() + shrinkVertically(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings-panel"),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        AccessibilitySettingsSection(
                            state = accessibilityState,
                            layoutSpec = layoutSpec,
                            brandLogoPickerLauncher = brandLogoPickerLauncher,
                            onThemeModeChanged = onThemeModeChanged,
                            onTextScaleChanged = onTextScaleChanged,
                            onHighContrastChanged = onHighContrastChanged,
                            onComfortableSpacingChanged = onComfortableSpacingChanged,
                            onFocusModeChanged = onFocusModeChanged,
                            onReduceMotionChanged = onReduceMotionChanged,
                            onLanguageChanged = onLanguageChanged,
                            onStandardWorkdayChanged = onStandardWorkdayChanged,
                            onExportUserFullNameChanged = onExportUserFullNameChanged,
                            onExportOfficeNameChanged = onExportOfficeNameChanged,
                            onExportEmployeeIdChanged = onExportEmployeeIdChanged,
                            onExportPersonIdChanged = onExportPersonIdChanged,
                            onPdfExportStyleChanged = onPdfExportStyleChanged,
                            onPickBrandingLogo = onPickBrandingLogo,
                            onClearBrandingLogo = onClearBrandingLogo,
                            settingsBackupState = settingsBackupController.uiState,
                            jsonFilePickerLauncher = jsonFilePickerLauncher,
                            onExportBackup = {
                                settingsBackupController.exportBackup(
                                    onSuccess = fileSaveLauncher::save,
                                    onFailure = {
                                        showMessage(strings.messageFor(it))
                                    },
                                )
                            },
                            onImportBackupSelected = settingsBackupController::prepareImport,
                            onConfirmImport = {
                                settingsBackupController.confirmImport(
                                    onSuccess = {
                                        onRefreshAfterBackupImport()
                                        showMessage(strings.backupImportSuccessMessage)
                                    },
                                    onFailure = {
                                        showMessage(strings.messageFor(it))
                                    },
                                )
                            },
                            onDismissImportConfirmation = settingsBackupController::dismissImportConfirmation,
                            onClose = {
                                isSettingsVisible = false
                            },
                            modifier = Modifier.widthIn(max = layoutSpec.panelMaxWidth),
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isActivityCatalogVisible,
                    enter = if (accessibilityState.reduceMotion) EnterTransition.None else fadeIn() + expandVertically(),
                    exit = if (accessibilityState.reduceMotion) ExitTransition.None else fadeOut() + shrinkVertically(),
                ) {
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
                    month = controller.currentMonth,
                    grid = controller.monthCells,
                    accessibilityState = accessibilityState,
                    layoutSpec = layoutSpec,
                    onPreviousMonth = controller::loadPreviousMonth,
                    onNextMonth = controller::loadNextMonth,
                    onMonthSelected = controller::goToMonth,
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
                    text = AppVersion.DISPLAY_NAME,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
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
            onWorkLocationChanged = controller::updateDraftWorkLocation,
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
            onCoverIncompleteMonth = {
                controller.coverIncompleteMonth(
                    language = accessibilityState.language,
                    onValidationError = {
                        showMessage(strings.checkDayFieldsBeforeSaving)
                    },
                    onPersistenceError = {
                        showMessage(strings.unableToCoverIncompleteMonth)
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
                    exportOfficeName = accessibilityState.exportOfficeName,
                    exportEmployeeId = accessibilityState.exportEmployeeId,
                    exportPersonId = accessibilityState.exportPersonId,
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
    if (appConfigurationController.uiState.isDialogVisible) {
        AppConfigurationDialog(
            state = appConfigurationController.uiState,
            layoutSpec = layoutSpec,
            jsonFilePickerLauncher = jsonFilePickerLauncher,
            onExport = {
                appConfigurationController.export(
                    onSuccess = { document ->
                        fileSaveLauncher.save(document)
                        appConfigurationController.dismissDialog()
                    },
                    onFailure = { showMessage(strings.appConfigurationExportError) },
                )
            },
            onImportSelected = appConfigurationController::prepareImport,
            onConfirmImport = { appConfigurationController.confirmImport({ onRefreshAfterBackupImport(); showMessage(strings.appConfigurationSuccess) }, { showMessage(strings.appConfigurationImportError) }) },
            onDismissImport = appConfigurationController::dismissImport,
            onDismiss = appConfigurationController::dismissDialog,
        )
    }
}

private fun com.tlincompose.core.AppStrings.messageFor(reason: SettingsBackupFailureReason): String = when (reason) {
    SettingsBackupFailureReason.EMPTY_CONTENT -> backupImportEmptyContentMessage
    SettingsBackupFailureReason.EXPORT_FAILED -> backupExportErrorMessage
    SettingsBackupFailureReason.INVALID_JSON -> backupImportInvalidJsonMessage
    SettingsBackupFailureReason.PERSISTENCE_ERROR -> backupImportPersistenceErrorMessage
    SettingsBackupFailureReason.UNSUPPORTED_VERSION -> backupImportUnsupportedVersionMessage
}

private fun PdfExportStyleUiState.toDomain(): com.tlincompose.domain.model.PdfExportStyle = when (this) {
    PdfExportStyleUiState.RETRO -> com.tlincompose.domain.model.PdfExportStyle.RETRO
    PdfExportStyleUiState.SIMPLE_TABLE -> com.tlincompose.domain.model.PdfExportStyle.SIMPLE_TABLE
    PdfExportStyleUiState.COMPACT_LIST -> com.tlincompose.domain.model.PdfExportStyle.COMPACT_LIST
    PdfExportStyleUiState.DETAIL_BLOCKS -> com.tlincompose.domain.model.PdfExportStyle.DETAIL_BLOCKS
}
