package com.tlincompose.presentation.configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.domain.model.AppConfigurationError
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.usecase.ExportAppConfigurationUseCase
import com.tlincompose.domain.usecase.ImportAppConfigurationUseCase
import com.tlincompose.presentation.JsonFileSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

data class AppConfigurationUiState(
    val isDialogVisible: Boolean = false,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val pendingFileName: String? = null,
) { val isBusy get() = isExporting || isImporting }

class AppConfigurationController(
    private val exportConfiguration: ExportAppConfigurationUseCase,
    private val importConfiguration: ImportAppConfigurationUseCase,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val log = logger.withTag("AppConfigurationController")
    private var pendingSelection: JsonFileSelection? = null
    var uiState by mutableStateOf(AppConfigurationUiState()); private set
    fun dispose() = scope.cancel()
    fun openDialog() { if (!uiState.isBusy) uiState = uiState.copy(isDialogVisible = true) }
    fun dismissDialog() { if (!uiState.isBusy) uiState = uiState.copy(isDialogVisible = false) }
    fun export(onSuccess: (ExportDocument) -> Unit, onFailure: () -> Unit) {
        if (uiState.isBusy) return
        uiState = uiState.copy(isExporting = true)
        scope.launch { runCatching { exportConfiguration() }.onSuccess { uiState = uiState.copy(isExporting = false); onSuccess(it) }.onFailure { log.e(it) { "Export configurazione fallito." }; uiState = uiState.copy(isExporting = false); onFailure() } }
    }
    fun prepareImport(selection: JsonFileSelection?) { if (!uiState.isBusy && selection != null) { pendingSelection = selection; uiState = uiState.copy(pendingFileName = selection.fileName) } }
    fun dismissImport() { if (!uiState.isImporting) { pendingSelection = null; uiState = uiState.copy(pendingFileName = null) } }
    fun confirmImport(onSuccess: () -> Unit, onFailure: (AppConfigurationError) -> Unit) {
        val selection = pendingSelection ?: return
        pendingSelection = null; uiState = uiState.copy(pendingFileName = null, isImporting = true)
        scope.launch { val error = importConfiguration(selection.bytes); uiState = uiState.copy(isImporting = false); if (error == null) onSuccess() else onFailure(error) }
    }
}
