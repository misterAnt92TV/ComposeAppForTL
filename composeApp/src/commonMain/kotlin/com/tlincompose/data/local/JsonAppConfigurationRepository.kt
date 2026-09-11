package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.data.mapper.toDomain
import com.tlincompose.data.mapper.toStore
import com.tlincompose.domain.model.AppConfigurationData
import com.tlincompose.domain.model.AppConfigurationError
import com.tlincompose.domain.model.AppConfigurationReadResult
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.repository.AppConfigurationRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class JsonAppConfigurationRepository(
    private val dispatcherProvider: DispatcherProvider,
    logger: Logger,
    private val json: Json = Json { encodeDefaults = true; ignoreUnknownKeys = true },
) : AppConfigurationRepository {
    private val log = logger.withTag("JsonAppConfigurationRepository")

    override suspend fun exportConfiguration(data: AppConfigurationData, fileName: String): ExportDocument =
        withContext(dispatcherProvider.io) {
            ExportDocument(fileName, "application/json", json.encodeToString(AppConfigurationStore.serializer(), data.toStore()).encodeToByteArray())
        }

    override suspend fun readConfiguration(bytes: ByteArray): AppConfigurationReadResult = withContext(dispatcherProvider.io) {
        val content = bytes.decodeToString().trim()
        if (content.isBlank()) return@withContext AppConfigurationReadResult.Failure(AppConfigurationError.EmptyContent)
        val store = runCatching { json.decodeFromString(AppConfigurationStore.serializer(), content) }.getOrElse {
            if (it !is SerializationException) log.e(it) { "Errore lettura configurazione JSON." }
            return@withContext AppConfigurationReadResult.Failure(AppConfigurationError.InvalidJson)
        }
        if (store.schemaVersion != AppConfigurationStore.CURRENT_SCHEMA_VERSION) {
            return@withContext AppConfigurationReadResult.Failure(AppConfigurationError.UnsupportedVersion)
        }
        AppConfigurationReadResult.Success(store.toDomain())
    }
}
