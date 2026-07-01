package com.tlincompose.domain.repository

import com.tlincompose.domain.model.AppBackupData
import com.tlincompose.domain.model.AppBackupReadResult
import com.tlincompose.domain.model.ExportDocument

interface AppBackupRepository {
    suspend fun exportBackup(
        data: AppBackupData,
        fileName: String,
    ): ExportDocument

    suspend fun readBackup(bytes: ByteArray): AppBackupReadResult
}
