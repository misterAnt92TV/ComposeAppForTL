package com.tlincompose.domain.usecase

import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.ExportActivityTypeFilter
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.TimesheetExporter

class ExportMonthReportUseCase(
    private val exporter: TimesheetExporter,
    private val filterExportEntries: FilterExportEntriesUseCase,
) {
    suspend operator fun invoke(
        month: CalendarMonth,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        filter: ExportActivityTypeFilter = ExportActivityTypeFilter(),
        exportUserFullName: String? = null,
        exportOfficeName: String? = null,
        exportEmployeeId: String? = null,
        exportPersonId: String? = null,
        brandingLogoBase64: String? = null,
        pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
    ): ExportDocument = exporter.exportMonth(
        month = month,
        entries = filterExportEntries(entries, filter),
        format = format,
        language = language,
        exportUserFullName = exportUserFullName,
        exportOfficeName = exportOfficeName,
        exportEmployeeId = exportEmployeeId,
        exportPersonId = exportPersonId,
        brandingLogoBase64 = brandingLogoBase64,
        pdfExportStyle = pdfExportStyle,
    )
}
