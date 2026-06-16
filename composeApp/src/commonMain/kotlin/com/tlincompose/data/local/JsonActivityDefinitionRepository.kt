package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.data.mapper.toDomain
import com.tlincompose.data.mapper.toEntity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class JsonActivityDefinitionRepository(
    private val storageDriver: StorageDriver,
    private val dispatcherProvider: DispatcherProvider,
    logger: Logger,
    private val fileName: String = "tlincompose-activity-definitions.json",
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    },
) : ActivityDefinitionRepository {
    private val log = logger.withTag("JsonActivityDefinitionRepository")

    override suspend fun loadAll(): List<ActivityDefinition> = withContext(dispatcherProvider.io) {
        val raw = storageDriver.read(fileName) ?: return@withContext emptyList()
        val decoded = runCatching {
            json.decodeFromString(ActivityDefinitionsStore.serializer(), raw)
        }.getOrElse {
            log.w(it) { "Impossibile leggere il file $fileName. Uso un catalogo vuoto." }
            return@withContext emptyList()
        }

        decoded.definitions
            .map(ActivityDefinitionEntity::toDomain)
            .sortedBy(ActivityDefinition::extCode)
    }

    override suspend fun upsert(definition: ActivityDefinition, previousExtCode: String?) = withContext(dispatcherProvider.io) {
        val definitions = loadDefinitionsMap()
        previousExtCode
            ?.takeIf { it != definition.extCode }
            ?.let(definitions::remove)
        definitions[definition.extCode] = definition
        persistDefinitions(definitions.values)
        log.i { "Entita ${definition.extCode} salvata con successo." }
    }

    override suspend fun delete(extCode: String) = withContext(dispatcherProvider.io) {
        val definitions = loadDefinitionsMap()
        definitions.remove(extCode)
        persistDefinitions(definitions.values)
        log.i { "Entita $extCode eliminata dal catalogo." }
    }

    private fun loadDefinitionsMap(): MutableMap<String, ActivityDefinition> {
        val raw = storageDriver.read(fileName) ?: return mutableMapOf()
        val decoded = runCatching {
            json.decodeFromString(ActivityDefinitionsStore.serializer(), raw)
        }.getOrElse {
            log.w(it) { "Impossibile leggere il file $fileName. Uso un catalogo vuoto." }
            return mutableMapOf()
        }

        return decoded.definitions
            .map(ActivityDefinitionEntity::toDomain)
            .associateByTo(mutableMapOf()) { it.extCode }
    }

    private fun persistDefinitions(definitions: Collection<ActivityDefinition>) {
        val store = ActivityDefinitionsStore(
            definitions = definitions
                .sortedBy(ActivityDefinition::extCode)
                .map(ActivityDefinition::toEntity),
        )
        storageDriver.write(
            fileName,
            json.encodeToString(ActivityDefinitionsStore.serializer(), store),
        )
    }
}
