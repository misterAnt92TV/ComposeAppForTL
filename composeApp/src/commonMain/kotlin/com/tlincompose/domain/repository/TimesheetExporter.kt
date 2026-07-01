package com.tlincompose.domain.repository

import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.model.ExportDocument
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle

interface TimesheetExporter {
    suspend fun exportMonth(
        month: CalendarMonth,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        exportUserFullName: String? = null,
        exportOfficeName: String? = null,
        exportEmployeeId: String? = null,
        exportPersonId: String? = null,
        brandingLogoBase64: String? = null,
        pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
    ): ExportDocument

    suspend fun exportDateRange(
        range: DateRange,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        exportUserFullName: String? = null,
        exportOfficeName: String? = null,
        exportEmployeeId: String? = null,
        exportPersonId: String? = null,
        brandingLogoBase64: String? = null,
        pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
    ): ExportDocument

    suspend fun exportMonthRange(
        range: MonthRange,
        entries: List<DailyEntry>,
        format: ExportFormat,
        language: AppLanguage,
        exportUserFullName: String? = null,
        exportOfficeName: String? = null,
        exportEmployeeId: String? = null,
        exportPersonId: String? = null,
        brandingLogoBase64: String? = null,
        pdfExportStyle: PdfExportStyle = PdfExportStyle.RETRO,
    ): ExportDocument
}
