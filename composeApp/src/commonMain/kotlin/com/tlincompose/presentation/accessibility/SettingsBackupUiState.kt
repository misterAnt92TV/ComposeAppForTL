package com.tlincompose.presentation.accessibility

data class SettingsBackupUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val pendingImportFileName: String? = null,
) {
    val isBusy: Boolean
        get() = isExporting || isImporting

    val isImportConfirmationVisible: Boolean
        get() = pendingImportFileName != null
}

enum class SettingsBackupFailureReason {
    EXPORT_FAILED,
    INVALID_JSON,
    UNSUPPORTED_VERSION,
    EMPTY_CONTENT,
    PERSISTENCE_ERROR,
}
