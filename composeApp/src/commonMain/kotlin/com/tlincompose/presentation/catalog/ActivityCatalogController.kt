package com.tlincompose.presentation.catalog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.core.message
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.ActivityDefinitionDraftInput
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.BuiltInActivityDefinitions
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DefaultExtWorkMode
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.domain.usecase.ApplyDefaultExtActivityToMonthUseCase
import com.tlincompose.domain.usecase.DeleteActivityDefinitionUseCase
import com.tlincompose.domain.usecase.EnsureDefaultActivityDefinitionsUseCase
import com.tlincompose.domain.usecase.GenerateNextExtCodeUseCase
import com.tlincompose.domain.usecase.LoadActivityDefinitionsUseCase
import com.tlincompose.domain.usecase.SaveActivityDefinitionUseCase
import com.tlincompose.domain.usecase.SyncActivitiesWithDefinitionUseCase
import com.tlincompose.domain.usecase.ValidateActivityDefinitionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ActivityCatalogController(
    private val ensureDefaultActivityDefinitions: EnsureDefaultActivityDefinitionsUseCase,
    private val loadActivityDefinitions: LoadActivityDefinitionsUseCase,
    private val generateNextExtCode: GenerateNextExtCodeUseCase,
    private val validateActivityDefinition: ValidateActivityDefinitionUseCase,
    private val saveActivityDefinition: SaveActivityDefinitionUseCase,
    private val syncActivitiesWithDefinition: SyncActivitiesWithDefinitionUseCase,
    private val deleteActivityDefinition: DeleteActivityDefinitionUseCase,
    private val applyDefaultExtActivityToMonth: ApplyDefaultExtActivityToMonthUseCase,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
) {
    private companion object {
        const val MaxCustomProjectIconBytes: Int = 256 * 1024
    }

    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val log = logger.withTag("ActivityCatalogController")
    private var currentLanguage: AppLanguage = AppLanguage.ENGLISH

    var definitions by mutableStateOf<List<ActivityDefinition>>(emptyList())
        private set

    var editorState by mutableStateOf<ActivityDefinitionEditorUiState?>(null)
        private set

    var definitionPendingDelete by mutableStateOf<ActivityDefinition?>(null)
        private set

    var defaultExtWorkMode by mutableStateOf(DefaultExtWorkMode.OFFICE)
        private set

    var isApplyingDefaultExt by mutableStateOf(false)
        private set

    init {
        reloadDefinitions()
    }

    fun dispose() {
        scope.cancel()
    }

    fun refresh() {
        reloadDefinitions()
    }

    fun openCreateEditor(defaultMinutes: Int) {
        editorState = ActivityDefinitionEditorUiState(
            mode = ActivityDefinitionEditorMode.CREATE,
            extCode = generateNextExtCode(definitions),
            durationHoursText = com.tlincompose.core.formatHours(defaultMinutes),
        )
    }

    fun openEditEditor(definition: ActivityDefinition) {
        editorState = ActivityDefinitionEditorUiState(
            mode = ActivityDefinitionEditorMode.EDIT,
            originalExtCode = definition.extCode,
            extCode = definition.extCode,
            type = definition.type,
            title = definition.title,
            description = definition.description,
            durationHoursText = definition.defaultMinutes.div(60.0).toString().removeSuffix(".0"),
            projectUrl = definition.projectUrl.orEmpty(),
            projectIconPreset = definition.projectIconPreset,
            projectCustomIconBase64 = definition.projectCustomIconBase64,
        )
    }

    fun dismissEditor() {
        editorState = null
    }

    fun requestDelete(definition: ActivityDefinition) {
        if (BuiltInActivityDefinitions.isProtectedCode(definition.extCode)) return
        definitionPendingDelete = definition
    }

    fun dismissDeleteRequest() {
        definitionPendingDelete = null
    }

    fun updateDraftType(type: EntryType) {
        if (editorState?.isProtectedDefinition == true) return
        updateEditorState {
            if (type == EntryType.PROJECT) {
                copy(type = type)
            } else {
                copy(
                    type = type,
                    projectIconPreset = null,
                    projectCustomIconBase64 = null,
                )
            }
        }
    }

    fun updateDraftExtCode(extCode: String) {
        if (editorState?.isProtectedDefinition == true) return
        updateEditorState { copy(extCode = extCode, extCodeError = null) }
    }

    fun updateDraftTitle(title: String) {
        updateEditorState { copy(title = title, titleError = null) }
    }

    fun updateDraftDescription(description: String) {
        updateEditorState { copy(description = description) }
    }

    fun updateDraftDuration(durationHoursText: String) {
        updateEditorState { copy(durationHoursText = durationHoursText, durationError = null) }
    }

    fun updateDraftProjectUrl(projectUrl: String) {
        updateEditorState { copy(projectUrl = projectUrl, projectUrlError = null) }
    }

    fun updateDraftProjectIconPreset(projectIconPreset: ProjectIconPreset) {
        updateEditorState {
            copy(
                projectIconPreset = projectIconPreset,
                projectCustomIconBase64 = null,
            )
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun updateDraftProjectCustomIcon(
        imageBytes: ByteArray,
        onIconTooLarge: () -> Unit,
    ) {
        if (imageBytes.size > MaxCustomProjectIconBytes) {
            onIconTooLarge()
            return
        }
        updateEditorState {
            copy(
                projectIconPreset = null,
                projectCustomIconBase64 = Base64.Default.encode(imageBytes),
            )
        }
    }

    fun clearDraftProjectIcon() {
        updateEditorState {
            copy(
                projectIconPreset = null,
                projectCustomIconBase64 = null,
            )
        }
    }

    fun updateLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    fun updateDefaultExtWorkMode(workMode: DefaultExtWorkMode) {
        defaultExtWorkMode = workMode
    }

    fun applyDefaultExtToCurrentMonth(
        month: CalendarMonth,
        onSuccess: (Int) -> Unit,
        onFailure: () -> Unit,
    ) {
        if (isApplyingDefaultExt) return
        val defaultDefinition = definitions.firstOrNull { it.extCode == BuiltInActivityDefinitions.DefaultExtCode }
        isApplyingDefaultExt = true
        scope.launch {
            runCatching {
                applyDefaultExtActivityToMonth(
                    month = month,
                    workMode = defaultExtWorkMode,
                    definition = defaultDefinition,
                )
            }.onSuccess { appliedDays ->
                isApplyingDefaultExt = false
                onSuccess(appliedDays)
            }.onFailure {
                isApplyingDefaultExt = false
                log.e(it) { "Impossibile applicare l'attività EXT di default al mese ${month.fileStamp}." }
                onFailure()
            }
        }
    }

    fun saveEditor(
        language: AppLanguage,
        onValidationError: () -> Unit,
        onPersistenceError: () -> Unit,
        onSuccess: () -> Unit = {},
    ) {
        val currentState = editorState ?: return
        currentLanguage = language
        val validation = validateActivityDefinition(
            ActivityDefinitionDraftInput(
                extCode = currentState.extCode,
                type = currentState.type,
                title = currentState.title,
                description = currentState.description,
                durationHoursText = currentState.durationHoursText,
                projectUrl = currentState.projectUrl,
                projectIconPreset = currentState.projectIconPreset,
                projectCustomIconBase64 = currentState.projectCustomIconBase64,
            ),
            existingExtCodes = definitions.map(ActivityDefinition::extCode).toSet(),
            originalExtCode = currentState.originalExtCode,
        )

        if (validation.hasErrors) {
            editorState = currentState.copy(
                extCodeError = validation.extCodeError?.message(language),
                titleError = validation.titleError?.message(language),
                durationError = validation.durationError?.message(language),
                projectUrlError = validation.projectUrlError?.message(language),
            )
            onValidationError()
            return
        }

        val existingDefinition = definitions.firstOrNull {
            it.extCode == (currentState.originalExtCode ?: currentState.extCode)
        }
        val validatedDraft = validation.validatedDraft ?: return

        scope.launch {
            runCatching {
                val savedDefinition = saveActivityDefinition(validatedDraft, existingDefinition)
                existingDefinition?.let { previousDefinition ->
                    syncActivitiesWithDefinition(previousDefinition.extCode, savedDefinition)
                }
                savedDefinition
            }.onSuccess { definition ->
                definitions = definitions
                    .filterNot { it.extCode == definition.extCode || it.extCode == existingDefinition?.extCode }
                    .plus(definition)
                    .sortedForDisplay()
                editorState = null
                log.i { "Catalogo EXT aggiornato per ${definition.extCode}." }
                onSuccess()
            }.onFailure {
                log.e(it) { "Impossibile salvare l'entità ${currentState.extCode}." }
                onPersistenceError()
            }
        }
    }

    fun confirmDelete(
        onFailure: () -> Unit,
    ) {
        val pendingDefinition = definitionPendingDelete ?: return
        scope.launch {
            runCatching {
                deleteActivityDefinition(pendingDefinition.extCode)
            }.onSuccess {
                definitions = definitions
                    .filterNot { it.extCode == pendingDefinition.extCode }
                    .sortedForDisplay()
                definitionPendingDelete = null
            }.onFailure {
                log.e(it) { "Impossibile eliminare l'entità ${pendingDefinition.extCode}." }
                onFailure()
            }
        }
    }

    private fun reloadDefinitions() {
        scope.launch {
            val loadedDefinitions = runCatching {
                ensureDefaultActivityDefinitions()
                loadActivityDefinitions()
            }.onFailure {
                log.e(it) { "Impossibile caricare il catalogo EXT." }
            }.getOrDefault(emptyList())

            definitions = loadedDefinitions.sortedForDisplay()
        }
    }

    private fun updateEditorState(transform: ActivityDefinitionEditorUiState.() -> ActivityDefinitionEditorUiState) {
        val currentState = editorState ?: return
        editorState = transform(currentState)
    }

    private fun List<ActivityDefinition>.sortedForDisplay(): List<ActivityDefinition> =
        sortedWith(compareBy<ActivityDefinition>({ it.type.ordinal }, { it.title.lowercase() }, { it.extCode }))
}
