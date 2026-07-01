package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportActivityTypeFilter
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.TimesheetExporter
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportMonthRangeReportUseCaseTest {
    @Test
    fun loadsSelectedMonthsFromRepositoryAndDelegatesToExporter() = runTest {
        val requestedRange = MonthRange(
            startMonth = CalendarMonth(2026, 5),
            endMonth = CalendarMonth(2026, 7),
        )
        val repository = RecordingRepository(
            loadRangeResult = mapOf(
                LocalDate(2026, 7, 12) to DailyEntry(
                    LocalDate(2026, 7, 12),
                    listOf(Activity(type = EntryType.PROJECT, extCode = "EXT-0001", title = "Apollo", minutes = 480)),
                ),
                LocalDate(2026, 5, 10) to DailyEntry(
                    LocalDate(2026, 5, 10),
                    listOf(Activity(type = EntryType.PERMIT, extCode = "EXT-0002", title = "Permesso", minutes = 120)),
                ),
            ),
        )
        val exporter = RecordingExporter()

        val result = ExportMonthRangeReportUseCase(repository, exporter, FilterExportEntriesUseCase())(
            range = requestedRange,
            format = ExportFormat.CSV,
            language = AppLanguage.ITALIAN,
            filter = ExportActivityTypeFilter(setOf(EntryType.PERMIT)),
            exportUserFullName = "Mario Rossi",
            exportOfficeName = "Sede Milano",
            exportEmployeeId = "EMP-123",
            exportPersonId = "P-456",
        )

        assertEquals(DateRange(LocalDate(2026, 5, 1), LocalDate(2026, 7, 31)), repository.requestedRange)
        assertEquals(requestedRange, exporter.exportedRange)
        assertEquals(
            listOf(LocalDate(2026, 5, 10)),
            exporter.exportedEntries.map(DailyEntry::date),
        )
        assertEquals(listOf(EntryType.PERMIT), exporter.exportedEntries.single().activities.map(Activity::type))
        assertEquals("Mario Rossi", exporter.exportUserFullName)
        assertEquals("Sede Milano", exporter.exportOfficeName)
        assertEquals("EMP-123", exporter.exportEmployeeId)
        assertEquals("P-456", exporter.exportPersonId)
        assertEquals("months.csv", result.fileName)
    }

    private class RecordingRepository(
        private val loadRangeResult: Map<LocalDate, DailyEntry>,
    ) : TimesheetRepository {
        var requestedRange: DateRange? = null

        override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> = emptyMap()

        override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> {
            requestedRange = range
            return loadRangeResult
        }

        override suspend fun loadAll(): List<DailyEntry> = loadRangeResult.values.toList()

        override suspend fun saveEntry(entry: DailyEntry) = Unit

        override suspend fun deleteEntry(date: LocalDate) = Unit

        override suspend fun replaceAll(entries: List<DailyEntry>) = Unit

        override suspend fun syncActivitiesWithDefinition(
            previousExtCode: String,
            definition: ActivityDefinition,
        ): Int = 0
    }

    private class RecordingExporter : TimesheetExporter {
        var exportedRange: MonthRange? = null
        var exportedEntries: List<DailyEntry> = emptyList()
        var exportUserFullName: String? = null
        var exportOfficeName: String? = null
        var exportEmployeeId: String? = null
        var exportPersonId: String? = null

        override suspend fun exportMonth(
            month: CalendarMonth,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = error("exportMonth should not be called in this test")

        override suspend fun exportDateRange(
            range: DateRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument = error("exportDateRange should not be called in this test")

        override suspend fun exportMonthRange(
            range: MonthRange,
            entries: List<DailyEntry>,
            format: ExportFormat,
            language: AppLanguage,
            exportUserFullName: String?,
            exportOfficeName: String?,
            exportEmployeeId: String?,
            exportPersonId: String?,
            brandingLogoBase64: String?,
            pdfExportStyle: PdfExportStyle,
        ): ExportDocument {
            exportedRange = range
            exportedEntries = entries
            this.exportUserFullName = exportUserFullName
            this.exportOfficeName = exportOfficeName
            this.exportEmployeeId = exportEmployeeId
            this.exportPersonId = exportPersonId
            return ExportDocument(
                fileName = "months.csv",
                mimeType = format.mimeType,
                bytes = "csv".encodeToByteArray(),
            )
        }
    }
}
