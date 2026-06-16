package com.tlincompose.core.di

import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.TestDispatcherProvider
import com.tlincompose.data.local.InMemoryStorageDriver
import com.tlincompose.domain.repository.ActivityDefinitionRepository
import com.tlincompose.domain.repository.AccessibilityPreferencesRepository
import com.tlincompose.domain.repository.TimesheetExporter
import com.tlincompose.domain.repository.TimesheetRepository
import com.tlincompose.presentation.accessibility.AccessibilitySettingsController
import com.tlincompose.presentation.catalog.ActivityCatalogController
import com.tlincompose.presentation.TimesheetController
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.StandardTestDispatcher
import org.koin.core.context.stopKoin
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AppModulesTest {
    @Test
    fun appModulesResolveCoreDependencies() {
        val koinApp = startKoin {
            allowOverride(true)
            modules(
                appModules(InMemoryStorageDriver()) +
                    module {
                        single<DispatcherProvider> {
                            TestDispatcherProvider(StandardTestDispatcher())
                        }
                    },
            )
        }

        try {
            val koin = koinApp.koin
            assertNotNull(koin.get<DispatcherProvider>())
            assertNotNull(koin.get<Logger>())
            assertNotNull(koin.get<TimesheetRepository>())
            assertNotNull(koin.get<ActivityDefinitionRepository>())
            assertNotNull(koin.get<AccessibilityPreferencesRepository>())
            assertNotNull(koin.get<TimesheetExporter>())
            assertNotNull(koin.get<TimesheetController>())
            assertNotNull(koin.get<AccessibilitySettingsController>())
            assertNotNull(koin.get<ActivityCatalogController>())
        } finally {
            stopKoin()
        }
    }
}
