package com.tlincompose.domain.usecase

import com.tlincompose.core.LocalDateComparator
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.ExportActivityTypeFilter
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.TimesheetExporter
import com.tlincompose.domain.repository.TimesheetRepository

class ExportMonthRangeReportUseCase(
    private val repository: TimesheetRepository,
    private val exporter: TimesheetExporter,
    private val filterExportEntries: FilterExportEntriesUseCase,
) {
    suspend operator fun invoke(
        range: MonthRange,
        format: ExportFormat,
        language: AppLanguage,
        filter: ExportActivityTypeFilter = ExportActivityTypeFilter(),
        exportUserFullName: String? = null,
        brandingLogoBase64: String? = null,
        pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
    ): ExportDocument {
        val entries = repository.loadRange(
            com.tlincompose.domain.model.DateRange(
                startDate = range.startDate,
                endDate = range.endDate,
            ),
        ).values.sortedWith(compareBy(LocalDateComparator) { it.date })

        return exporter.exportMonthRange(
            range = range,
            entries = filterExportEntries(entries, filter),
            format = format,
            language = language,
            exportUserFullName = exportUserFullName,
            brandingLogoBase64 = brandingLogoBase64,
            pdfExportStyle = pdfExportStyle,
        )
    }
}
