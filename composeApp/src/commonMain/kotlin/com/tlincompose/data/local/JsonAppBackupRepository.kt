package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.data.mapper.toDomain
import com.tlincompose.data.mapper.toEntity
import com.tlincompose.data.mapper.toStore
import com.tlincompose.domain.model.AppBackupData
import com.tlincompose.domain.model.AppBackupImportError
import com.tlincompose.domain.model.AppBackupReadResult
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.repository.AppBackupRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class JsonAppBackupRepository(
    private val dispatcherProvider: DispatcherProvider,
    logger: Logger,
    private val json: Json = Json {
        prettyPrint = false
        encodeDefaults = false
        explicitNulls = false
        ignoreUnknownKeys = true
    },
) : AppBackupRepository {
    private val log = logger.withTag("JsonAppBackupRepository")

    override suspend fun exportBackup(
        data: AppBackupData,
        fileName: String,
    ): ExportDocument = withContext(dispatcherProvider.io) {
        val store = AppBackupStore(
            schemaVersion = AppBackupStore.CURRENT_SCHEMA_VERSION,
            preferences = data.preferences.toStore(),
            activityDefinitions = data.activityDefinitions.map { it.toEntity() },
            timesheetEntries = data.timesheetEntries.map { it.toEntity() },
        )
        ExportDocument(
            fileName = fileName,
            mimeType = "application/json",
            bytes = json.encodeToString(AppBackupStore.serializer(), store).encodeToByteArray(),
        )
    }

    override suspend fun readBackup(bytes: ByteArray): AppBackupReadResult = withContext(dispatcherProvider.io) {
        val rawContent = bytes.decodeToString().trim()
        if (rawContent.isBlank()) {
            return@withContext AppBackupReadResult.Failure(AppBackupImportError.EmptyContent)
        }

        val decoded = runCatching {
            json.decodeFromString(AppBackupStore.serializer(), rawContent)
        }.getOrElse { error ->
            if (error is SerializationException) {
                return@withContext AppBackupReadResult.Failure(AppBackupImportError.InvalidJson)
            }
            log.e(error) { "Errore inatteso durante la lettura del backup JSON." }
            return@withContext AppBackupReadResult.Failure(AppBackupImportError.InvalidJson)
        }

        if (decoded.schemaVersion != AppBackupStore.CURRENT_SCHEMA_VERSION) {
            return@withContext AppBackupReadResult.Failure(AppBackupImportError.UnsupportedVersion)
        }

        AppBackupReadResult.Success(
            AppBackupData(
                preferences = decoded.preferences.toDomain(),
                activityDefinitions = decoded.activityDefinitions.map(ActivityDefinitionEntity::toDomain),
                timesheetEntries = decoded.timesheetEntries.map(DailyEntryEntity::toDomain),
            ),
        )
    }
}
