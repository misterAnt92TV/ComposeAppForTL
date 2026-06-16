package com.tlincompose.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import com.tlincompose.core.MaxBrandingLogoBytes
import com.tlincompose.core.appStrings
import com.tlincompose.core.brandingLogoTooLargeMessage
import com.tlincompose.core.di.appModules
import com.tlincompose.core.invalidBrandingLogoMessage
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.presentation.accessibility.AccessibilitySettingsController
import com.tlincompose.presentation.catalog.ActivityCatalogController
import com.tlincompose.presentation.layout.appBackgroundBrush
import com.tlincompose.presentation.screen.TimesheetScreen
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    var platformLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val showMessage: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
    val platformServices = rememberPlatformServices(
        language = platformLanguage,
        onMessage = showMessage,
    )
    val modules = remember(platformServices.storageDriver) {
        appModules(platformServices.storageDriver)
    }

    KoinApplication(application = {
        modules(modules)
    }) {
        val controller = koinInject<TimesheetController>()
        val accessibilityController = koinInject<AccessibilitySettingsController>()
        val activityCatalogController = koinInject<ActivityCatalogController>()
        val appLogger = koinInject<Logger>()

        DisposableEffect(controller, accessibilityController, activityCatalogController) {
            onDispose {
                controller.dispose()
                accessibilityController.dispose()
                activityCatalogController.dispose()
            }
        }

        LaunchedEffect(appLogger) {
            appLogger.i { "Applicazione avviata con Koin e Kermit." }
        }

        val strings = remember(accessibilityController.uiState.language) {
            appStrings(accessibilityController.uiState.language)
        }

        LaunchedEffect(accessibilityController.uiState.language) {
            platformLanguage = accessibilityController.uiState.language
            controller.updateLanguage(accessibilityController.uiState.language)
            activityCatalogController.updateLanguage(accessibilityController.uiState.language)
        }

        CompositionLocalProvider(LocalAppStrings provides strings) {
            TLInComposeTheme(accessibilitySettings = accessibilityController.uiState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(appBackgroundBrush(accessibilityController.uiState)),
                ) {
                    Scaffold(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                    ) { innerPadding ->
                        TimesheetScreen(
                            controller = controller,
                            activityCatalogController = activityCatalogController,
                            accessibilityState = accessibilityController.uiState,
                            onThemeModeChanged = accessibilityController::updateThemeMode,
                            onTextScaleChanged = accessibilityController::updateTextScale,
                            onHighContrastChanged = accessibilityController::updateHighContrast,
                            onComfortableSpacingChanged = accessibilityController::updateComfortableSpacing,
                            onFocusModeChanged = accessibilityController::updateFocusMode,
                            onLanguageChanged = accessibilityController::updateLanguage,
                            onStandardWorkdayChanged = accessibilityController::updateStandardWorkdayMinutes,
                            onPdfExportStyleChanged = accessibilityController::updatePdfExportStyle,
                            fileSaveLauncher = platformServices.fileSaveLauncher,
                            brandLogoPickerLauncher = platformServices.brandLogoPickerLauncher,
                            projectIconPickerLauncher = platformServices.projectIconPickerLauncher,
                            onPickBrandingLogo = { imageBytes ->
                                accessibilityController.updateBrandingLogo(
                                    imageBytes = imageBytes,
                                    onLogoTooLarge = {
                                        showMessage(
                                            strings.brandingLogoTooLargeMessage(
                                                maxKilobytes = MaxBrandingLogoBytes / 1024,
                                            ),
                                        )
                                    },
                                    onInvalidLogo = {
                                        showMessage(strings.invalidBrandingLogoMessage)
                                    },
                                )
                            },
                            onClearBrandingLogo = accessibilityController::clearBrandingLogo,
                            showMessage = showMessage,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
