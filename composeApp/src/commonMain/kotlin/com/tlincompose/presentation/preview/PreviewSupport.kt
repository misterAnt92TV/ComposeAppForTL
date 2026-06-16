package com.tlincompose.presentation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tlincompose.core.AppStrings
import com.tlincompose.core.appStrings
import com.tlincompose.core.activitySummaryLabel
import com.tlincompose.core.entryTypeLabel
import com.tlincompose.core.extEntityDisplayLabel
import com.tlincompose.core.holidayName
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DefaultWorkdayMinutes
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.HolidayKey
import com.tlincompose.domain.model.ProjectIconPreset
import com.tlincompose.presentation.LocalAppStrings
import com.tlincompose.presentation.TLInComposeTheme
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState
import com.tlincompose.presentation.accessibility.AccessibilityTextScaleUiState
import com.tlincompose.presentation.accessibility.PdfExportStyleUiState
import com.tlincompose.presentation.accessibility.ThemeModeUiState
import com.tlincompose.presentation.calendar.ActivityDraftUiState
import com.tlincompose.presentation.calendar.CalendarActivityUiModel
import com.tlincompose.presentation.calendar.DayEditTargetUiState
import com.tlincompose.presentation.calendar.DayEditorUiState
import com.tlincompose.presentation.calendar.MonthCellUiModel
import com.tlincompose.presentation.calendar.MonthSummaryUiState
import com.tlincompose.presentation.catalog.ActivityDefinitionEditorMode
import com.tlincompose.presentation.catalog.ActivityDefinitionEditorUiState
import com.tlincompose.presentation.layout.AccessibilityLayoutSpec
import com.tlincompose.presentation.layout.accessibilityLayoutSpec
import com.tlincompose.presentation.layout.appBackgroundBrush
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

internal fun previewAccessibilityState(
    themeMode: ThemeModeUiState = ThemeModeUiState.SYSTEM,
    language: AppLanguage = AppLanguage.ITALIAN,
    textScale: AccessibilityTextScaleUiState = AccessibilityTextScaleUiState.STANDARD,
    highContrast: Boolean = false,
    comfortableSpacing: Boolean = true,
    focusMode: Boolean = false,
    standardWorkdayMinutes: Int = DefaultWorkdayMinutes,
    brandingLogoBase64: String? = null,
    pdfExportStyle: PdfExportStyleUiState = PdfExportStyleUiState.RETRO,
): AccessibilitySettingsUiState = AccessibilitySettingsUiState(
    textScale = textScale,
    highContrast = highContrast,
    comfortableSpacing = comfortableSpacing,
    focusMode = focusMode,
    themeMode = themeMode,
    language = language,
    standardWorkdayMinutes = standardWorkdayMinutes,
    brandingLogoBase64 = brandingLogoBase64,
    pdfExportStyle = pdfExportStyle,
)

internal fun previewLayoutSpec(
    state: AccessibilitySettingsUiState = previewAccessibilityState(),
): AccessibilityLayoutSpec = accessibilityLayoutSpec(state)

@Composable
internal fun TLInComposePreviewSurface(
    state: AccessibilitySettingsUiState = previewAccessibilityState(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val strings = remember(state.language) { appStrings(state.language) }
    CompositionLocalProvider(LocalAppStrings provides strings) {
        TLInComposeTheme(accessibilitySettings = state) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(appBackgroundBrush(state))
                    .padding(16.dp),
            ) {
                content()
            }
        }
    }
}

internal val previewMonth: CalendarMonth = CalendarMonth(year = 2026, monthNumber = 5)

internal const val previewBrandingLogoBase64: String =
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO2Z0K8AAAAASUVORK5CYII="

internal val previewMonthSummary: MonthSummaryUiState = MonthSummaryUiState(
    totalLoggedMinutes = 6_720,
    targetCompletionMinutes = 10_080,
    completedCompletionMinutes = 6_720,
    remainingCompletionMinutes = 3_360,
    completionFraction = 0.67f,
    completionPercentage = 67,
)

internal fun previewActivityDefinitions(strings: AppStrings): List<ActivityDefinition> = listOf(
    ActivityDefinition(
        extCode = "TLI-204",
        type = EntryType.PROJECT,
        title = previewProjectTitle(strings),
        description = previewProjectDescription(strings),
        defaultMinutes = 480,
        createdDate = LocalDate(2026, 5, 2),
        updatedDate = LocalDate(2026, 5, 18),
        projectUrl = previewProjectUrl(strings),
        projectIconPreset = ProjectIconPreset.CODE,
    ),
    ActivityDefinition(
        extCode = "FER",
        type = EntryType.VACATION,
        title = strings.entryTypeLabel(EntryType.VACATION),
        description = previewVacationDescription(strings),
        defaultMinutes = 480,
        createdDate = LocalDate(2026, 1, 10),
        updatedDate = LocalDate(2026, 4, 12),
    ),
    ActivityDefinition(
        extCode = "PERM",
        type = EntryType.PERMIT,
        title = strings.entryTypeLabel(EntryType.PERMIT),
        description = previewPermitDescription(strings),
        defaultMinutes = 120,
        createdDate = LocalDate(2026, 3, 6),
        updatedDate = LocalDate(2026, 5, 8),
    ),
)

internal fun previewDayEditorState(strings: AppStrings): DayEditorUiState = DayEditorUiState(
    target = DayEditTargetUiState(
        sourceDate = LocalDate(2026, 5, 14),
    ),
    rows = listOf(
        ActivityDraftUiState(
            type = EntryType.PROJECT,
            extCode = "TLI-204",
            title = previewProjectTitle(strings),
            description = previewComponentsDescription(strings),
            projectUrl = previewProjectUrl(strings),
            hoursText = "6",
        ),
        ActivityDraftUiState(
            type = EntryType.PERMIT,
            extCode = "PERM",
            title = strings.entryTypeLabel(EntryType.PERMIT),
            description = previewMedicalVisitDescription(strings),
            hoursText = "2",
        ),
    ),
    errors = listOf(null, null),
)

internal fun previewActivityDefinitionEditorState(strings: AppStrings): ActivityDefinitionEditorUiState =
    ActivityDefinitionEditorUiState(
        mode = ActivityDefinitionEditorMode.EDIT,
        originalExtCode = "TLI-204",
        extCode = "TLI-204",
        type = EntryType.PROJECT,
        title = previewProjectTitle(strings),
        description = previewLightDarkDescription(strings),
        durationHoursText = "8",
        projectUrl = previewProjectUrl(strings),
        projectIconPreset = ProjectIconPreset.CODE,
    )

internal fun previewCalendarCells(strings: AppStrings): List<MonthCellUiModel> = buildList {
    val startDate = LocalDate(2026, 4, 27)
    repeat(35) { index ->
        val date = startDate.plus(DatePeriod(days = index))
        val summaries = when (date.day) {
            5 -> listOf(strings.activitySummaryLabel("TLI-204 • ${previewSprintPlanningLabel(strings)}", "8"))
            8 -> listOf(strings.activitySummaryLabel(strings.entryTypeLabel(EntryType.VACATION), "4"))
            12 -> listOf(
                strings.activitySummaryLabel("TLI-204 • ${previewUiReviewLabel(strings)}", "3"),
                strings.activitySummaryLabel("OPS-12 • ${previewReleaseSupportLabel(strings)}", "3"),
                strings.activitySummaryLabel(previewRetrospectiveLabel(strings), "2"),
            )
            14 -> listOf(
                strings.activitySummaryLabel("TLI-204 • ${previewComposePreviewLabel(strings)}", "6"),
                strings.activitySummaryLabel("PERM • ${strings.entryTypeLabel(EntryType.PERMIT)}", "2"),
            )
            18 -> listOf(strings.activitySummaryLabel("TLI-204 • ${previewExportTestLabel(strings)}", "7.5"))
            21 -> listOf(
                strings.activitySummaryLabel("TLI-204 • ${previewAccessibilityLabel(strings)}", "3"),
                strings.activitySummaryLabel("TLI-204 • ${previewFinalPolishLabel(strings)}", "3"),
                strings.activitySummaryLabel("OPS-21 • ${previewBugfixLabel(strings)}", "2.5"),
            )
            else -> emptyList()
        }
        val totalMinutes = when (date.day) {
            5 -> 480
            8 -> 240
            12 -> 480
            14 -> 480
            18 -> 450
            21 -> 510
            else -> 0
        }
        add(
            MonthCellUiModel(
                date = date,
                inCurrentMonth = date.month.ordinal + 1 == previewMonth.monthNumber,
                isWeekend = index % 7 >= 5,
                isToday = date == LocalDate(2026, 5, 14),
                holidayLabel = if (date == LocalDate(2026, 5, 1)) {
                    strings.holidayName(HolidayKey.LABOUR_DAY)
                } else {
                    null
                },
                activityItems = summaries.mapIndexed { activityIndex, summary ->
                    CalendarActivityUiModel(
                        activityIndex = activityIndex,
                        entryType = when {
                            summary.contains(strings.entryTypeLabel(EntryType.VACATION)) -> EntryType.VACATION
                            summary.contains(strings.entryTypeLabel(EntryType.PERMIT)) -> EntryType.PERMIT
                            else -> EntryType.PROJECT
                        },
                        summary = summary,
                    )
                },
                activityCount = summaries.size,
                totalMinutes = totalMinutes,
                isRangeStart = date == LocalDate(2026, 5, 12),
                isRangeEnd = date == LocalDate(2026, 5, 16),
                isInSelectedRange = date in LocalDate(2026, 5, 12)..LocalDate(2026, 5, 16),
                isActivityDragSource = date == LocalDate(2026, 5, 14),
                isActivityDropTarget = date == LocalDate(2026, 5, 15),
            ),
        )
    }
}

private fun previewProjectTitle(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Calendar refactor"
    AppLanguage.ITALIAN -> "Refactor calendario"
    AppLanguage.GERMAN -> "Kalender-Refactor"
    AppLanguage.FRENCH -> "Refactor du calendrier"
    AppLanguage.SPANISH -> "Refactor del calendario"
}

private fun previewProjectDescription(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Preview cleanup, accessibility, and layout polish for the main screen."
    AppLanguage.ITALIAN -> "Pulizia preview, accessibilita e layout della schermata principale."
    AppLanguage.GERMAN -> "Bereinigung von Preview, Barrierefreiheit und Layout der Hauptansicht."
    AppLanguage.FRENCH -> "Nettoyage des previews, accessibilite et affinamento du layout principal."
    AppLanguage.SPANISH -> "Limpieza de previews, accesibilidad y pulido del layout principal."
}

private fun previewVacationDescription(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Full day or partial vacation entry."
    AppLanguage.ITALIAN -> "Giornata o frazione dedicata alle ferie."
    AppLanguage.GERMAN -> "Ganzer oder teilweiser Urlaubseintrag."
    AppLanguage.FRENCH -> "Journee ou fraction consacree aux conges."
    AppLanguage.SPANISH -> "Jornada o fraccion dedicada a vacaciones."
}

private fun previewPermitDescription(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Hourly permit for personal needs."
    AppLanguage.ITALIAN -> "Permesso orario per esigenze personali."
    AppLanguage.GERMAN -> "Stundenweise Genehmigung fur private Bedurfnisse."
    AppLanguage.FRENCH -> "Autorisation horaire pour besoins personnels."
    AppLanguage.SPANISH -> "Permiso horario para necesidades personales."
}

private fun previewComponentsDescription(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Compose component previews"
    AppLanguage.ITALIAN -> "Preview componenti Compose"
    AppLanguage.GERMAN -> "Compose-Komponenten-Previews"
    AppLanguage.FRENCH -> "Previews des composants Compose"
    AppLanguage.SPANISH -> "Previews de componentes Compose"
}

private fun previewMedicalVisitDescription(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Medical appointment"
    AppLanguage.ITALIAN -> "Visita medica"
    AppLanguage.GERMAN -> "Arzttermin"
    AppLanguage.FRENCH -> "Visite medicale"
    AppLanguage.SPANISH -> "Visita medica"
}

private fun previewLightDarkDescription(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Component cleanup and light/dark preview coverage."
    AppLanguage.ITALIAN -> "Pulizia dei componenti e aggiunta preview light/dark."
    AppLanguage.GERMAN -> "Bereinigung der Komponenten und Abdeckung mit Light/Dark-Previews."
    AppLanguage.FRENCH -> "Nettoyage des composants et couverture des previews clair/sombre."
    AppLanguage.SPANISH -> "Limpieza de componentes y cobertura de previews claro/oscuro."
}

private fun previewProjectUrl(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "https://intranet.tli.local/calendar"
    AppLanguage.ITALIAN -> "https://intranet.tli.local/calendario"
    AppLanguage.GERMAN -> "https://intranet.tli.local/kalender"
    AppLanguage.FRENCH -> "https://intranet.tli.local/calendrier"
    AppLanguage.SPANISH -> "https://intranet.tli.local/calendario"
}

private fun previewSprintPlanningLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Sprint planning"
    AppLanguage.ITALIAN -> "Sprint planning"
    AppLanguage.GERMAN -> "Sprint-Planung"
    AppLanguage.FRENCH -> "Planification du sprint"
    AppLanguage.SPANISH -> "Planificacion del sprint"
}

private fun previewUiReviewLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "UI review"
    AppLanguage.ITALIAN -> "Revisione UI"
    AppLanguage.GERMAN -> "UI-Review"
    AppLanguage.FRENCH -> "Revue UI"
    AppLanguage.SPANISH -> "Revision UI"
}

private fun previewReleaseSupportLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Release support"
    AppLanguage.ITALIAN -> "Supporto release"
    AppLanguage.GERMAN -> "Release-Support"
    AppLanguage.FRENCH -> "Support de release"
    AppLanguage.SPANISH -> "Soporte release"
}

private fun previewRetrospectiveLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Team retrospective"
    AppLanguage.ITALIAN -> "Retro team"
    AppLanguage.GERMAN -> "Team-Retrospektive"
    AppLanguage.FRENCH -> "Retrospective equipe"
    AppLanguage.SPANISH -> "Retrospectiva del equipo"
}

private fun previewComposePreviewLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Compose previews"
    AppLanguage.ITALIAN -> "Preview Compose"
    AppLanguage.GERMAN -> "Compose-Previews"
    AppLanguage.FRENCH -> "Previews Compose"
    AppLanguage.SPANISH -> "Previews Compose"
}

private fun previewExportTestLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Export tests"
    AppLanguage.ITALIAN -> "Test export"
    AppLanguage.GERMAN -> "Export-Tests"
    AppLanguage.FRENCH -> "Tests export"
    AppLanguage.SPANISH -> "Pruebas de exportacion"
}

private fun previewAccessibilityLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Accessibility"
    AppLanguage.ITALIAN -> "Accessibilita"
    AppLanguage.GERMAN -> "Barrierefreiheit"
    AppLanguage.FRENCH -> "Accessibilite"
    AppLanguage.SPANISH -> "Accesibilidad"
}

private fun previewFinalPolishLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Final polish"
    AppLanguage.ITALIAN -> "Polish finale"
    AppLanguage.GERMAN -> "Feinschliff"
    AppLanguage.FRENCH -> "Finition finale"
    AppLanguage.SPANISH -> "Pulido final"
}

private fun previewBugfixLabel(strings: AppStrings): String = when (strings.language) {
    AppLanguage.ENGLISH -> "Bugfix"
    AppLanguage.ITALIAN -> "Bugfix"
    AppLanguage.GERMAN -> "Bugfix"
    AppLanguage.FRENCH -> "Correction"
    AppLanguage.SPANISH -> "Correccion"
}
