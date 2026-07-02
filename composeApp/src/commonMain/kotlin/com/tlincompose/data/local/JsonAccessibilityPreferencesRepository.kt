package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.data.mapper.toDomain
import com.tlincompose.data.mapper.toStore
import com.tlincompose.domain.model.AccessibilityPreferences
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class JsonAccessibilityPreferencesRepository(
    private val storageDriver: StorageDriver,
    private val dispatcherProvider: DispatcherProvider,
    logger: Logger,
    private val fileName: String = "tlincompose-accessibility.json",
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    },
) : AccessibilityPreferencesRepository {
    private val log = logger.withTag("JsonAccessibilityPreferencesRepository")

    override suspend fun loadPreferences(): AccessibilityPreferences = withContext(dispatcherProvider.io) {
        val raw = storageDriver.read(fileName) ?: return@withContext AccessibilityPreferences()
        val decoded = runCatching {
            json.decodeFromString(AccessibilityPreferencesStore.serializer(), raw)
        }.getOrElse {
            log.w(it) { "Impossibile leggere il file $fileName. Uso le impostazioni di default." }
            return@withContext AccessibilityPreferences()
        }
        decoded.toDomain()
    }

    override suspend fun savePreferences(preferences: AccessibilityPreferences) = withContext(dispatcherProvider.io) {
        storageDriver.write(
            fileName,
            json.encodeToString(AccessibilityPreferencesStore.serializer(), preferences.toStore()),
        )
        log.d { "Impostazioni accessibilita salvate con successo." }
    }
}
