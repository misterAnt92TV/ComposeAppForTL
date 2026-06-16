@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.core.di

import co.touchlab.kermit.Logger
import com.tlincompose.core.DefaultDispatcherProvider
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.data.export.DefaultMonthExporter
import com.tlincompose.data.local.JsonAccessibilityPreferencesRepository
import com.tlincompose.data.local.JsonActivityDefinitionRepository
import com.tlincompose.data.local.JsonTimesheetRepository
import com.tlincompose.data.local.StorageDriver
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.TimesheetExporter
import com.tlincompose.domain.repository.TimesheetRepository
import com.tlincompose.domain.usecase.AddActivityToDayUseCase
import com.tlincompose.domain.usecase.BuildCalendarMonthGridUseCase
import com.tlincompose.domain.usecase.CalculateMonthWorkSummaryUseCase
import com.tlincompose.domain.usecase.CreateDateRangeUseCase
import com.tlincompose.domain.usecase.CreateMonthRangeUseCase
import com.tlincompose.domain.usecase.DeleteActivityDefinitionUseCase
import com.tlincompose.domain.usecase.ExportDateRangeReportUseCase
import com.tlincompose.domain.usecase.ExportMonthRangeReportUseCase
import com.tlincompose.domain.usecase.ExportMonthReportUseCase
import com.tlincompose.domain.usecase.FilterExportEntriesUseCase
import com.tlincompose.domain.usecase.GenerateNextExtCodeUseCase
import com.tlincompose.domain.usecase.LoadAccessibilityPreferencesUseCase
import com.tlincompose.domain.usecase.LoadActivityDefinitionsUseCase
import com.tlincompose.domain.usecase.LoadMonthEntriesUseCase
import com.tlincompose.domain.usecase.SaveAccessibilityPreferencesUseCase
import com.tlincompose.domain.usecase.SaveActivityDefinitionUseCase
import com.tlincompose.domain.usecase.SaveDailyEntryUseCase
import com.tlincompose.domain.usecase.SaveDateRangeEntriesUseCase
import com.tlincompose.domain.usecase.SyncActivitiesWithDefinitionUseCase
import com.tlincompose.domain.usecase.ValidateActivityDefinitionUseCase
import com.tlincompose.domain.usecase.ValidateDailyEntryUseCase
import com.tlincompose.presentation.TimesheetController
import com.tlincompose.presentation.accessibility.AccessibilitySettingsController
import com.tlincompose.presentation.catalog.ActivityCatalogController
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModules(storageDriver: StorageDriver): List<Module> = listOf(
    platformModule(storageDriver),
    coreModule,
    dataModule,
    domainModule,
    presentationModule,
)

private fun platformModule(storageDriver: StorageDriver): Module = module {
    single<StorageDriver> { storageDriver }
}

private val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider }
    single<Logger> { Logger.withTag("TLInCompose") }
}

private val dataModule = module {
    single<TimesheetRepository> {
        JsonTimesheetRepository(
            storageDriver = get(),
            dispatcherProvider = get(),
            logger = get(),
        )
    }
    single<ActivityDefinitionRepository> {
        JsonActivityDefinitionRepository(
            storageDriver = get(),
            dispatcherProvider = get(),
            logger = get(),
        )
    }
    single<AccessibilityPreferencesRepository> {
        JsonAccessibilityPreferencesRepository(
            storageDriver = get(),
            dispatcherProvider = get(),
            logger = get(),
        )
    }
    single<TimesheetExporter> {
        DefaultMonthExporter(
            dispatcherProvider = get(),
            logger = get(),
        )
    }
}

private val domainModule = module {
    single { BuildCalendarMonthGridUseCase() }
    single { CalculateMonthWorkSummaryUseCase() }
    single { CreateDateRangeUseCase() }
    single { CreateMonthRangeUseCase() }
    single { FilterExportEntriesUseCase() }
    single { DeleteActivityDefinitionUseCase(get()) }
    single { GenerateNextExtCodeUseCase() }
    single { LoadActivityDefinitionsUseCase(get()) }
    single { LoadAccessibilityPreferencesUseCase(get()) }
    single { LoadMonthEntriesUseCase(get()) }
    single { SaveActivityDefinitionUseCase(get()) }
    single { SaveAccessibilityPreferencesUseCase(get()) }
    single { ValidateActivityDefinitionUseCase() }
    single { ValidateDailyEntryUseCase() }
    single { SaveDailyEntryUseCase(get()) }
    single { AddActivityToDayUseCase(get(), get()) }
    single { SaveDateRangeEntriesUseCase(get()) }
    single { SyncActivitiesWithDefinitionUseCase(get()) }
    single { ExportMonthReportUseCase(get(), get()) }
    single { ExportDateRangeReportUseCase(get(), get(), get()) }
    single { ExportMonthRangeReportUseCase(get(), get(), get()) }
}

private val presentationModule = module {
    factory {
        TimesheetController(
            loadMonthEntries = get(),
            buildCalendarMonthGrid = get(),
            calculateMonthWorkSummary = get(),
            validateDailyEntry = get(),
            addActivityToDay = get(),
            saveDateRangeEntries = get(),
            exportMonthReport = get(),
            exportMonthRangeReport = get(),
            createDateRange = get(),
            createMonthRange = get(),
            dispatcherProvider = get(),
            logger = get(),
        )
    }
    factory {
        AccessibilitySettingsController(
            loadAccessibilityPreferences = get(),
            saveAccessibilityPreferences = get(),
            dispatcherProvider = get(),
            logger = get(),
        )
    }
    factory {
        ActivityCatalogController(
            loadActivityDefinitions = get(),
            generateNextExtCode = get(),
            validateActivityDefinition = get(),
            saveActivityDefinition = get(),
            syncActivitiesWithDefinition = get(),
            deleteActivityDefinition = get(),
            dispatcherProvider = get(),
            logger = get(),
        )
    }
}
