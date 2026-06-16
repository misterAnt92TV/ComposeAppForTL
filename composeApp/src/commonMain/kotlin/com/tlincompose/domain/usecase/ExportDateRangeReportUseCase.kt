package com.tlincompose.domain.usecase

import com.tlincompose.core.LocalDateComparator
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.ExportActivityTypeFilter
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.repository.TimesheetExporter
import com.tlincompose.domain.repository.TimesheetRepository

class ExportDateRangeReportUseCase(
    private val repository: TimesheetRepository,
    private val exporter: TimesheetExporter,
    private val filterExportEntries: FilterExportEntriesUseCase,
) {
    suspend operator fun invoke(
        range: DateRange,
        format: ExportFormat,
        language: AppLanguage,
        filter: ExportActivityTypeFilter = ExportActivityTypeFilter(),
        brandingLogoBase64: String? = null,
        pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
    ): ExportDocument {
        val entries = repository.loadRange(range)
            .values
            .sortedWith(compareBy(LocalDateComparator) { it.date })

        return exporter.exportDateRange(
            range = range,
            entries = filterExportEntries(entries, filter),
            format = format,
            language = language,
            brandingLogoBase64 = brandingLogoBase64,
            pdfExportStyle = pdfExportStyle,
        )
    }
}
