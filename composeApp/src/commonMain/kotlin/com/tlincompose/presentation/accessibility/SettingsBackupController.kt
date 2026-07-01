package com.tlincompose.presentation.accessibility

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.domain.model.AppBackupImportError
import com.tlincompose.domain.model.AppBackupImportResult
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.usecase.ExportAppBackupUseCase
import com.tlincompose.domain.usecase.ImportAppBackupUseCase
import com.tlincompose.presentation.JsonFileSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SettingsBackupController(
    private val exportAppBackup: ExportAppBackupUseCase,
    private val importAppBackup: ImportAppBackupUseCase,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val log = logger.withTag("SettingsBackupController")
    private var pendingImportSelection: JsonFileSelection? = null

    var uiState by mutableStateOf(SettingsBackupUiState())
        private set

    fun dispose() {
        scope.cancel()
    }

    fun exportBackup(
        onSuccess: (ExportDocument) -> Unit,
        onFailure: (SettingsBackupFailureReason) -> Unit,
    ) {
        if (uiState.isBusy) return

        uiState = uiState.copy(isExporting = true)
        scope.launch {
            runCatching {
                exportAppBackup()
            }.onSuccess { document ->
                uiState = uiState.copy(isExporting = false)
                onSuccess(document)
            }.onFailure { error ->
                log.e(error) { "Impossibile preparare il backup JSON." }
                uiState = uiState.copy(isExporting = false)
                onFailure(SettingsBackupFailureReason.EXPORT_FAILED)
            }
        }
    }

    fun prepareImport(selection: JsonFileSelection?) {
        if (uiState.isBusy || selection == null) return

        pendingImportSelection = selection
        uiState = uiState.copy(pendingImportFileName = selection.fileName)
    }

    fun dismissImportConfirmation() {
        if (uiState.isImporting) return

        pendingImportSelection = null
        uiState = uiState.copy(pendingImportFileName = null)
    }

    fun confirmImport(
        onSuccess: () -> Unit,
        onFailure: (SettingsBackupFailureReason) -> Unit,
    ) {
        if (uiState.isBusy) return

        val selection = pendingImportSelection ?: return
        pendingImportSelection = null
        uiState = uiState.copy(
            pendingImportFileName = null,
            isImporting = true,
        )

        scope.launch {
            when (val result = importAppBackup(selection.bytes)) {
                AppBackupImportResult.Success -> {
                    log.i { "Backup JSON importato correttamente da ${selection.fileName}." }
                    uiState = uiState.copy(isImporting = false)
                    onSuccess()
                }

                is AppBackupImportResult.Failure -> {
                    log.w { "Import backup fallito per ${selection.fileName}: ${result.error}." }
                    uiState = uiState.copy(isImporting = false)
                    onFailure(result.error.toFailureReason())
                }
            }
        }
    }

    private fun AppBackupImportError.toFailureReason(): SettingsBackupFailureReason = when (this) {
        AppBackupImportError.EmptyContent -> SettingsBackupFailureReason.EMPTY_CONTENT
        AppBackupImportError.InvalidJson -> SettingsBackupFailureReason.INVALID_JSON
        AppBackupImportError.PersistenceFailure -> SettingsBackupFailureReason.PERSISTENCE_ERROR
        AppBackupImportError.UnsupportedVersion -> SettingsBackupFailureReason.UNSUPPORTED_VERSION
    }
}
