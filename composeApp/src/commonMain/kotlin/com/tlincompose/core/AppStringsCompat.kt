@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.core

import com.tlincompose.domain.model.ActivityDefinitionFieldError
import com.tlincompose.domain.model.Activity
import com.tlincompose.domain.model.ActivityWorkLocation
import com.tlincompose.domain.model.AppLanguage
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntryValidationError
import com.tlincompose.domain.model.EntryType
import com.tlincompose.domain.model.ExportFormat
import com.tlincompose.domain.model.Holiday
import com.tlincompose.domain.model.HolidayKey
import com.tlincompose.domain.model.MonthRange
import com.tlincompose.domain.model.PdfExportStyle
import com.tlincompose.domain.model.ProjectIconPreset
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
fun CalendarMonth.displayLabel(language: AppLanguage): String = appStrings(language).monthLabel(monthNumber, year)

fun MonthRange.displayLabel(language: AppLanguage): String =
    if (startMonth == endMonth) {
        startMonth.displayLabel(language)
    } else {
        "${startMonth.displayLabel(language)} - ${endMonth.displayLabel(language)}"
    }

fun EntryType.displayName(language: AppLanguage): String = appStrings(language).entryTypeLabel(this)

fun ActivityWorkLocation.displayName(language: AppLanguage): String = appStrings(language).activityWorkLocationLabel(this)

fun Activity.displayLabel(language: AppLanguage): String = buildString {
    extCode?.takeIf(String::isNotBlank)?.let {
        append(it)
        append(" • ")
    }
    append(title.ifBlank { type.displayName(language) })
}

fun Activity.exportLabel(language: AppLanguage): String = displayLabel(language)

fun Holiday.label(language: AppLanguage): String = appStrings(language).holidayName(key)

fun ExportFormat.label(language: AppLanguage): String = appStrings(language).exportFormatLabel(this)

fun ProjectIconPreset.displayLabel(language: AppLanguage): String = appStrings(language).projectIconPresetLabel(this)

fun DailyEntryValidationError.message(language: AppLanguage): String = appStrings(language).dailyEntryValidationMessage(this)

fun ActivityDefinitionFieldError.message(language: AppLanguage): String =
    appStrings(language).activityDefinitionFieldErrorMessage(this)

fun AppStrings.monthLabel(monthNumber: Int, year: Int): String = "${monthNames[monthNumber - 1]} $year"

fun AppStrings.compactHours(hoursText: String): String = when (language) {
    AppLanguage.ENGLISH -> "$hoursText h"
    AppLanguage.ITALIAN -> "${hoursText}h"
    AppLanguage.GERMAN -> "$hoursText h"
    AppLanguage.FRENCH -> "${hoursText} h"
    AppLanguage.SPANISH -> "${hoursText} h"
}

fun AppStrings.percentageValue(value: Int): String = "$value%"

fun AppStrings.extEntityDisplayLabel(extCode: String, title: String): String = "$extCode • $title"

fun AppStrings.activitySummaryLabel(valueLabel: String, hoursText: String): String =
    "$valueLabel • ${compactHours(hoursText)}"

fun AppStrings.labeledValue(label: String, value: String): String = when (language) {
    AppLanguage.FRENCH -> "$label : $value"
    else -> "$label: $value"
}

val AppStrings.monthNames: List<String>
    get() = when (language) {
        AppLanguage.ENGLISH -> listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )
        AppLanguage.ITALIAN -> listOf(
            "Gennaio",
            "Febbraio",
            "Märzo",
            "Aprile",
            "Maggio",
            "Giugno",
            "Luglio",
            "Agosto",
            "Settembre",
            "Ottobre",
            "Novembre",
            "Dicembre",
        )
        AppLanguage.GERMAN -> listOf(
            "Januar",
            "Februar",
            "März",
            "April",
            "Mai",
            "Juni",
            "Juli",
            "August",
            "September",
            "Oktober",
            "November",
            "Dezember",
        )
        AppLanguage.FRENCH -> listOf(
            "Janvier",
            "Février",
            "Mars",
            "Avril",
            "Mai",
            "Juin",
            "Juillet",
            "Août",
            "Septembre",
            "Octobre",
            "Novembre",
            "Decembre",
        )
        AppLanguage.SPANISH -> listOf(
            "Enero",
            "Febrero",
            "Märzo",
            "Abril",
            "Mayo",
            "Junio",
            "Julio",
            "Agosto",
            "Septiembre",
            "Octubre",
            "Noviembre",
            "Diciembre",
        )
    }

val AppStrings.weekdayShortLabelsMondayFirst: List<String>
    get() = when (language) {
        AppLanguage.ENGLISH -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        AppLanguage.ITALIAN -> listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
        AppLanguage.GERMAN -> listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
        AppLanguage.FRENCH -> listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
        AppLanguage.SPANISH -> listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sab", "Dom")
    }

val AppStrings.weekdayLongLabelsMondayFirst: List<String>
    get() = when (language) {
        AppLanguage.ENGLISH -> listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        AppLanguage.ITALIAN -> listOf("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica")
        AppLanguage.GERMAN -> listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag")
        AppLanguage.FRENCH -> listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche")
        AppLanguage.SPANISH -> listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    }

fun AppStrings.entryTypeLabel(type: EntryType): String = when (language) {
    AppLanguage.ENGLISH -> when (type) {
        EntryType.PROJECT -> "Project"
        EntryType.COURSE -> "Courses"
        EntryType.VACATION -> "Vacation"
        EntryType.PERMIT -> "Permit"
    }
    AppLanguage.ITALIAN -> when (type) {
        EntryType.PROJECT -> "Progetto"
        EntryType.COURSE -> "Corsi"
        EntryType.VACATION -> "Ferie"
        EntryType.PERMIT -> "Permesso"
    }
    AppLanguage.GERMAN -> when (type) {
        EntryType.PROJECT -> "Projekt"
        EntryType.COURSE -> "Kurse"
        EntryType.VACATION -> "Urlaub"
        EntryType.PERMIT -> "Genehmigung"
    }
    AppLanguage.FRENCH -> when (type) {
        EntryType.PROJECT -> "Projet"
        EntryType.COURSE -> "Cours"
        EntryType.VACATION -> "Congés"
        EntryType.PERMIT -> "Autorisation"
    }
    AppLanguage.SPANISH -> when (type) {
        EntryType.PROJECT -> "Proyecto"
        EntryType.COURSE -> "Cursos"
        EntryType.VACATION -> "Vacaciones"
        EntryType.PERMIT -> "Permiso"
    }
}

fun AppStrings.activityWorkLocationLabel(location: ActivityWorkLocation): String = when (language) {
    AppLanguage.ENGLISH -> when (location) {
        ActivityWorkLocation.SMART_WORKING -> "Smart working"
        ActivityWorkLocation.OFFICE -> "Office"
        ActivityWorkLocation.CLIENT_SITE -> "Client site"
    }
    AppLanguage.ITALIAN -> when (location) {
        ActivityWorkLocation.SMART_WORKING -> "Smart working"
        ActivityWorkLocation.OFFICE -> "In sede"
        ActivityWorkLocation.CLIENT_SITE -> "Dal cliente"
    }
    AppLanguage.GERMAN -> when (location) {
        ActivityWorkLocation.SMART_WORKING -> "Homeoffice"
        ActivityWorkLocation.OFFICE -> "Im Büro"
        ActivityWorkLocation.CLIENT_SITE -> "Beim Kunden"
    }
    AppLanguage.FRENCH -> when (location) {
        ActivityWorkLocation.SMART_WORKING -> "Télétravail"
        ActivityWorkLocation.OFFICE -> "Au bureau"
        ActivityWorkLocation.CLIENT_SITE -> "Chez le client"
    }
    AppLanguage.SPANISH -> when (location) {
        ActivityWorkLocation.SMART_WORKING -> "Teletrabajo"
        ActivityWorkLocation.OFFICE -> "En sede"
        ActivityWorkLocation.CLIENT_SITE -> "En cliente"
    }
}

fun AppStrings.exportFormatLabel(format: ExportFormat): String = when (format) {
    ExportFormat.CSV -> "CSV"
    ExportFormat.XLSX -> "Excel"
    ExportFormat.PDF -> "PDF"
}

fun AppStrings.projectIconPresetLabel(preset: ProjectIconPreset): String = when (language) {
    AppLanguage.ENGLISH -> when (preset) {
        ProjectIconPreset.WORK -> "Work"
        ProjectIconPreset.CODE -> "Code"
        ProjectIconPreset.PALETTE -> "Design"
        ProjectIconPreset.BUILD -> "Build"
        ProjectIconPreset.BUG_REPORT -> "Bug"
        ProjectIconPreset.FOLDER -> "Archive"
        ProjectIconPreset.SETTINGS -> "Ops"
        ProjectIconPreset.SCHOOL -> "Study"
    }
    AppLanguage.ITALIAN -> when (preset) {
        ProjectIconPreset.WORK -> "Lavoro"
        ProjectIconPreset.CODE -> "Codice"
        ProjectIconPreset.PALETTE -> "Design"
        ProjectIconPreset.BUILD -> "Build"
        ProjectIconPreset.BUG_REPORT -> "Bug"
        ProjectIconPreset.FOLDER -> "Archivio"
        ProjectIconPreset.SETTINGS -> "Ops"
        ProjectIconPreset.SCHOOL -> "Studio"
    }
    AppLanguage.GERMAN -> when (preset) {
        ProjectIconPreset.WORK -> "Arbeit"
        ProjectIconPreset.CODE -> "Code"
        ProjectIconPreset.PALETTE -> "Design"
        ProjectIconPreset.BUILD -> "Build"
        ProjectIconPreset.BUG_REPORT -> "Bug"
        ProjectIconPreset.FOLDER -> "Archiv"
        ProjectIconPreset.SETTINGS -> "Ops"
        ProjectIconPreset.SCHOOL -> "Lernen"
    }
    AppLanguage.FRENCH -> when (preset) {
        ProjectIconPreset.WORK -> "Travail"
        ProjectIconPreset.CODE -> "Code"
        ProjectIconPreset.PALETTE -> "Design"
        ProjectIconPreset.BUILD -> "Build"
        ProjectIconPreset.BUG_REPORT -> "Bug"
        ProjectIconPreset.FOLDER -> "Archive"
        ProjectIconPreset.SETTINGS -> "Ops"
        ProjectIconPreset.SCHOOL -> "Étude"
    }
    AppLanguage.SPANISH -> when (preset) {
        ProjectIconPreset.WORK -> "Trabajo"
        ProjectIconPreset.CODE -> "Código"
        ProjectIconPreset.PALETTE -> "Diseño"
        ProjectIconPreset.BUILD -> "Build"
        ProjectIconPreset.BUG_REPORT -> "Bug"
        ProjectIconPreset.FOLDER -> "Archivo"
        ProjectIconPreset.SETTINGS -> "Ops"
        ProjectIconPreset.SCHOOL -> "Estudio"
    }
}

fun AppStrings.holidayName(key: HolidayKey): String = when (language) {
    AppLanguage.ENGLISH -> when (key) {
        HolidayKey.NEW_YEAR -> "New Year's Day"
        HolidayKey.EPIPHANY -> "Epiphany"
        HolidayKey.LIBERATION_DAY -> "Liberation Day"
        HolidayKey.LABOUR_DAY -> "Labour Day"
        HolidayKey.REPUBLIC_DAY -> "Republic Day"
        HolidayKey.FERRAGOSTO -> "Ferragosto"
        HolidayKey.ALL_SAINTS -> "All Saints"
        HolidayKey.IMMACULATE_CONCEPTION -> "Immaculate Conception"
        HolidayKey.CHRISTMAS -> "Christmás"
        HolidayKey.SAINT_STEPHENS_DAY -> "Saint Stephen's Day"
        HolidayKey.EASTER -> "Easter"
        HolidayKey.EASTER_MONDAY -> "Easter Monday"
    }
    AppLanguage.ITALIAN -> when (key) {
        HolidayKey.NEW_YEAR -> "Capodanno"
        HolidayKey.EPIPHANY -> "Epifania"
        HolidayKey.LIBERATION_DAY -> "Liberazione"
        HolidayKey.LABOUR_DAY -> "Festa del Lavoro"
        HolidayKey.REPUBLIC_DAY -> "Festa della Repubblica"
        HolidayKey.FERRAGOSTO -> "Ferragosto"
        HolidayKey.ALL_SAINTS -> "Ognissanti"
        HolidayKey.IMMACULATE_CONCEPTION -> "Immacolata"
        HolidayKey.CHRISTMAS -> "Natale"
        HolidayKey.SAINT_STEPHENS_DAY -> "Santo Stefano"
        HolidayKey.EASTER -> "Pasqua"
        HolidayKey.EASTER_MONDAY -> "Pasquetta"
    }
    AppLanguage.GERMAN -> when (key) {
        HolidayKey.NEW_YEAR -> "Neujahr"
        HolidayKey.EPIPHANY -> "Heilige Drei Konige"
        HolidayKey.LIBERATION_DAY -> "Tag der Befreiung"
        HolidayKey.LABOUR_DAY -> "Tag der Arbeit"
        HolidayKey.REPUBLIC_DAY -> "Tag der Republik"
        HolidayKey.FERRAGOSTO -> "Ferragosto"
        HolidayKey.ALL_SAINTS -> "Allerheiligen"
        HolidayKey.IMMACULATE_CONCEPTION -> "Maria Empfangnis"
        HolidayKey.CHRISTMAS -> "Weihnachten"
        HolidayKey.SAINT_STEPHENS_DAY -> "Stefanitag"
        HolidayKey.EASTER -> "Ostern"
        HolidayKey.EASTER_MONDAY -> "Ostermontag"
    }
    AppLanguage.FRENCH -> when (key) {
        HolidayKey.NEW_YEAR -> "Jour de l'An"
        HolidayKey.EPIPHANY -> "Épiphanie"
        HolidayKey.LIBERATION_DAY -> "Fête de la Libération"
        HolidayKey.LABOUR_DAY -> "Fête du Travail"
        HolidayKey.REPUBLIC_DAY -> "Fête de la République"
        HolidayKey.FERRAGOSTO -> "Ferragosto"
        HolidayKey.ALL_SAINTS -> "Toussaint"
        HolidayKey.IMMACULATE_CONCEPTION -> "Immaculée Conception"
        HolidayKey.CHRISTMAS -> "Noël"
        HolidayKey.SAINT_STEPHENS_DAY -> "Saint-Étienne"
        HolidayKey.EASTER -> "Pâques"
        HolidayKey.EASTER_MONDAY -> "Lundi de Pâques"
    }
    AppLanguage.SPANISH -> when (key) {
        HolidayKey.NEW_YEAR -> "Año Nuevo"
        HolidayKey.EPIPHANY -> "Epifanía"
        HolidayKey.LIBERATION_DAY -> "Día de la Liberación"
        HolidayKey.LABOUR_DAY -> "Día del Trabajo"
        HolidayKey.REPUBLIC_DAY -> "Día de la República"
        HolidayKey.FERRAGOSTO -> "Ferragosto"
        HolidayKey.ALL_SAINTS -> "Todos los Santos"
        HolidayKey.IMMACULATE_CONCEPTION -> "Inmaculada Concepción"
        HolidayKey.CHRISTMAS -> "Navidad"
        HolidayKey.SAINT_STEPHENS_DAY -> "San Esteban"
        HolidayKey.EASTER -> "Pascua"
        HolidayKey.EASTER_MONDAY -> "Lunes de Pascua"
    }
}

fun AppStrings.dailyEntryValidationMessage(error: DailyEntryValidationError): String = when (language) {
    AppLanguage.ENGLISH -> when (error) {
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Select a valid EXT entity."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Hours must be greater than zero."
    }
    AppLanguage.ITALIAN -> when (error) {
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Seleziona un'entità EXT valida."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Le ore devono essere maggiori di zero."
    }
    AppLanguage.GERMAN -> when (error) {
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Wähle eine gültige EXT-Entität aus."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Die Stunden mussen größer als null sein."
    }
    AppLanguage.FRENCH -> when (error) {
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Sélectionne une entité EXT valide."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Les heures doivent être supérieures à zéro."
    }
    AppLanguage.SPANISH -> when (error) {
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Selecciona una entidad EXT valida."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Las horas deben ser mayores que cero."
    }
}

fun AppStrings.activityDefinitionFieldErrorMessage(error: ActivityDefinitionFieldError): String = when (language) {
    AppLanguage.ENGLISH -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Enter an EXT code."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "This EXT code is already used in the catalog."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Enter a clear title for the entity."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "Duration must be greater than zero."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "Project URL must start with http:// or https://."
    }
    AppLanguage.ITALIAN -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Inserisci un codice EXT."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Questo codice EXT è già usato nel catalogo."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Inserisci un titolo chiaro per l'entità."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "La durata deve essere maggiore di zero."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "L'URL progetto deve iniziare con http:// oppure https://."
    }
    AppLanguage.GERMAN -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Gib einen EXT-Code ein."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Dieser EXT-Code wird im Katalog bereits verwendet."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Gib einen klaren Titel für die Entität ein."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "Die Dauer muss größer als null sein."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "Die Projekt-URL muss mit http:// oder https:// beginnen."
    }
    AppLanguage.FRENCH -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Saisis un code EXT."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Ce code EXT est déjà utilisé dans le catalogue."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Saisis un titre clair pour l'entité."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "La durée doit être supérieure à zéro."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "L'URL du projet doit commencer par http:// ou https://."
    }
    AppLanguage.SPANISH -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Introduce un código EXT."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Este código EXT ya se usa en el catálogo."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Introduce un título claro para la entidad."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "La duración debe ser mayor que cero."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "La URL del proyecto debe empezar por http:// o https://."
    }
}

fun AppStrings.languageLabel(option: AppLanguage): String = when (option) {
    AppLanguage.ENGLISH -> "English"
    AppLanguage.ITALIAN -> "Italiano"
    AppLanguage.GERMAN -> "Deutsch"
    AppLanguage.FRENCH -> "Français"
    AppLanguage.SPANISH -> "Español"
}

val AppStrings.calendarTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Operational calendar"
        AppLanguage.ITALIAN -> "Calendario operativo"
        AppLanguage.GERMAN -> "Betriebskalender"
        AppLanguage.FRENCH -> "Calendrier operationnel"
        AppLanguage.SPANISH -> "Calendario operativo"
    }

val AppStrings.calendarDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Tap a day to add activities, long-press a day to fill a range, or drag a visible activity onto another day to copy it."
        AppLanguage.ITALIAN -> "Tocca un giorno per aggiungere attività, tieni premuto un giorno per compilare un intervallo, oppure trascina un'attività visibile su un altro giorno per copiarla."
        AppLanguage.GERMAN -> "Tippe auf einen Tag, um Aktivitäten hinzuzufügen, halte einen Tag länger gedrückt für einen Bereich, oder ziehe eine sichtbare Aktivität auf einen anderen Tag, um sie zu kopieren."
        AppLanguage.FRENCH -> "Touchez un jour pour ajouter des activités, maintenez un jour pour remplir une plage, ou faites glisser une activité visible vers un autre jour pour la copier."
        AppLanguage.SPANISH -> "Toca un día para agregar actividades, mantén pulsado un día para compilar un intervalo, o arrastra una actividad visible a otro día para copiarla."
    }

val AppStrings.unableToCopyDraggedActivity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to copy the dragged activity."
        AppLanguage.ITALIAN -> "Impossibile copiare l'attività trascinata."
        AppLanguage.GERMAN -> "Die gezogene Aktivität konnte nicht kopiert werden."
        AppLanguage.FRENCH -> "Impossible de copier l'activité glissée."
        AppLanguage.SPANISH -> "No se puede copiar la actividad arrastrada."
    }

val AppStrings.appTagline: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "A clearer calendar for projects, vacation, and permits, with export for one month or multiple selected months."
        AppLanguage.ITALIAN -> "Un calendario più chiaro per progetti, ferie e permessi, con export per un mese o per più mesi selezionati."
        AppLanguage.GERMAN -> "Ein klarerer Kalender für Projekte, Urlaub und Genehmigungen mit Export für einen oder mehrere ausgewählte Monate."
        AppLanguage.FRENCH -> "Un calendrier plus clair pour les projets, les congés et les autorisations, avec export sur un ou plusieurs mois sélectionnés."
        AppLanguage.SPANISH -> "Un calendario más claro para proyectos, vacaciones y permisos, con exportación de uno o varios meses seleccionados."
    }

val AppStrings.weekStartsMonday: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Week starts on Monday"
        AppLanguage.ITALIAN -> "Settimana da lunedì"
        AppLanguage.GERMAN -> "Woche beginnt am Montag"
        AppLanguage.FRENCH -> "Semaine à partir du lundi"
        AppLanguage.SPANISH -> "La semana empieza el lunes"
    }

fun AppStrings.standardWorkday(hoursText: String): String = when (language) {
    AppLanguage.ENGLISH -> "Standard workday $hoursText h"
    AppLanguage.ITALIAN -> "Giornata standard ${hoursText}h"
    AppLanguage.GERMAN -> "Standardtag $hoursText h"
    AppLanguage.FRENCH -> "Journée standard ${hoursText} h"
    AppLanguage.SPANISH -> "Jornada estándar ${hoursText} h"
}

val AppStrings.monthHoursTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Month hours"
        AppLanguage.ITALIAN -> "Ore del mese"
        AppLanguage.GERMAN -> "Monatsstunden"
        AppLanguage.FRENCH -> "Heures du mois"
        AppLanguage.SPANISH -> "Horas del mes"
    }

fun AppStrings.monthCompletionProgress(completedHours: String, targetHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "Workday completion: ${completedHours}h of ${targetHours}h"
    AppLanguage.ITALIAN -> "Completamento giornate lavorative: ${completedHours}h su ${targetHours}h"
    AppLanguage.GERMAN -> "Fortschritt Arbeitstage: ${completedHours}h von ${targetHours}h"
    AppLanguage.FRENCH -> "Progression jours travaillés : ${completedHours}h sur ${targetHours}h"
    AppLanguage.SPANISH -> "Avance días laborables: ${completedHours}h de ${targetHours}h"
}

fun AppStrings.monthCompletionRemaining(remainingHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "${remainingHours}h still to log on workdays"
    AppLanguage.ITALIAN -> "Restano ${remainingHours}h da segnare sui giorni lavorativi"
    AppLanguage.GERMAN -> "Es fehlen noch ${remainingHours}h an Arbeitstagen"
    AppLanguage.FRENCH -> "Il reste ${remainingHours}h à renseigner sur les jours travaillés"
    AppLanguage.SPANISH -> "Quedan ${remainingHours}h por registrar en los días laborables"
}

fun AppStrings.monthCompletionComplete(standardHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "All workdays are covered up to the standard ${standardHours}h"
    AppLanguage.ITALIAN -> "Tutte le giornate lavorative sono coperte fino alle ${standardHours}h standard"
    AppLanguage.GERMAN -> "Alle Arbeitstage sind bis zum Standard von ${standardHours}h abgedeckt"
    AppLanguage.FRENCH -> "Tous les jours travaillés sont couverts jusqu'aux ${standardHours}h standard"
    AppLanguage.SPANISH -> "Todos los días laborables estan cubiertos hasta las ${standardHours}h estándar"
}

val AppStrings.workdayHoursTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Standard workday"
        AppLanguage.ITALIAN -> "Giornata standard"
        AppLanguage.GERMAN -> "Standardarbeitstag"
        AppLanguage.FRENCH -> "Journée standard"
        AppLanguage.SPANISH -> "Jornada estándar"
    }

val AppStrings.workdayHoursDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Use 30-minute steps to adapt the daily target used in summaries and new defaults."
        AppLanguage.ITALIAN -> "Usa passi da 30 minuti per adattare il target giornaliero usato nei riepiloghi e nei nuovi predefiniti."
        AppLanguage.GERMAN -> "Verwende 30-Minuten-Schritte, um das Tagesziel für Zusammenfassungen und neue Vorgaben anzupassen."
        AppLanguage.FRENCH -> "Utilise des pas de 30 minutes pour ajuster la cible journalière des résumés et des nouvelles valeurs par défaut."
        AppLanguage.SPANISH -> "Usa pasos de 30 minutos para adaptar el objetivo diario usado en los resúmenes y en los nuevos valores predeterminados."
    }

val AppStrings.decreaseWorkdayHours: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Reduce by 30 minutes"
        AppLanguage.ITALIAN -> "Riduci di 30 minuti"
        AppLanguage.GERMAN -> "Um 30 Minuten reduzieren"
        AppLanguage.FRENCH -> "Réduire de 30 minutes"
        AppLanguage.SPANISH -> "Reducir 30 minutos"
    }

val AppStrings.increaseWorkdayHours: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Increase by 30 minutes"
        AppLanguage.ITALIAN -> "Aumenta di 30 minuti"
        AppLanguage.GERMAN -> "Um 30 Minuten erhöhen"
        AppLanguage.FRENCH -> "Augmenter de 30 minutes"
        AppLanguage.SPANISH -> "Aumentar 30 minutos"
    }

val AppStrings.decreaseWorkdayButtonLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "- 0.5h"
        AppLanguage.ITALIAN -> "- 0,5h"
        AppLanguage.GERMAN -> "- 0,5h"
        AppLanguage.FRENCH -> "- 0,5h"
        AppLanguage.SPANISH -> "- 0,5h"
    }

val AppStrings.increaseWorkdayButtonLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "+ 0.5h"
        AppLanguage.ITALIAN -> "+ 0,5h"
        AppLanguage.GERMAN -> "+ 0,5h"
        AppLanguage.FRENCH -> "+ 0,5h"
        AppLanguage.SPANISH -> "+ 0,5h"
    }

val AppStrings.weekendNormallyFree: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Weekends usually free"
        AppLanguage.ITALIAN -> "Weekend di norma libero"
        AppLanguage.GERMAN -> "Wochenende normalerweise frei"
        AppLanguage.FRENCH -> "Week-end normalement libre"
        AppLanguage.SPANISH -> "Fin de semana normalmente libre"
    }

val AppStrings.exportFormatsInfo: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "CSV, Excel, and PDF export"
        AppLanguage.ITALIAN -> "Export CSV, Excel e PDF"
        AppLanguage.GERMAN -> "CSV-, Excel- und PDF-Export"
        AppLanguage.FRENCH -> "Export CSV, Excel et PDF"
        AppLanguage.SPANISH -> "Exportacion CSV, Excel y PDF"
    }

val AppStrings.highContrastActive: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "High contrast enabled"
        AppLanguage.ITALIAN -> "Contrasto elevato attivo"
        AppLanguage.GERMAN -> "Höher Kontrast aktiv"
        AppLanguage.FRENCH -> "Contraste élevé actif"
        AppLanguage.SPANISH -> "Contraste alto activo"
    }

val AppStrings.previousMonth: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Previous month"
        AppLanguage.ITALIAN -> "Mese precedente"
        AppLanguage.GERMAN -> "Vorheriger Monat"
        AppLanguage.FRENCH -> "Mois precedent"
        AppLanguage.SPANISH -> "Mes anterior"
    }

val AppStrings.nextMonth: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Next month"
        AppLanguage.ITALIAN -> "Mese successivo"
        AppLanguage.GERMAN -> "Nachster Monat"
        AppLanguage.FRENCH -> "Mois suivant"
        AppLanguage.SPANISH -> "Mes siguiente"
    }

val AppStrings.monthYearPickerTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Select month and year"
        AppLanguage.ITALIAN -> "Seleziona mese e anno"
        AppLanguage.GERMAN -> "Monat und Jahr auswählen"
        AppLanguage.FRENCH -> "Sélectionner mois et année"
        AppLanguage.SPANISH -> "Selecciona mes y año"
    }

val AppStrings.monthFieldLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Month"
        AppLanguage.ITALIAN -> "Mese"
        AppLanguage.GERMAN -> "Monat"
        AppLanguage.FRENCH -> "Mois"
        AppLanguage.SPANISH -> "Mes"
    }

val AppStrings.yearFieldLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Year"
        AppLanguage.ITALIAN -> "Anno"
        AppLanguage.GERMAN -> "Jahr"
        AppLanguage.FRENCH -> "Année"
        AppLanguage.SPANISH -> "Año"
    }

val AppStrings.cancelRangeSelection: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Cancel month selection"
        AppLanguage.ITALIAN -> "Annulla selezione mesi"
        AppLanguage.GERMAN -> "Monatsauswahl abbrechen"
        AppLanguage.FRENCH -> "Annuler la sélection des mois"
        AppLanguage.SPANISH -> "Cancelar selección de meses"
    }

val AppStrings.selectExportRange: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Select export months"
        AppLanguage.ITALIAN -> "Seleziona mesi export"
        AppLanguage.GERMAN -> "Exportmonate auswählen"
        AppLanguage.FRENCH -> "Sélectionner les mois d'export"
        AppLanguage.SPANISH -> "Seleccionar meses de exportación"
    }

val AppStrings.exportSelectedRange: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export selected months"
        AppLanguage.ITALIAN -> "Esporta mesi selezionati"
        AppLanguage.GERMAN -> "Ausgewählte Monate exportieren"
        AppLanguage.FRENCH -> "Exporter les mois sélectionnés"
        AppLanguage.SPANISH -> "Exportar meses seleccionados"
    }

val AppStrings.exportVisibleMonth: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export visible month"
        AppLanguage.ITALIAN -> "Esporta mese visibile"
        AppLanguage.GERMAN -> "Sichtbaren Monat exportieren"
        AppLanguage.FRENCH -> "Exporter le mois visible"
        AppLanguage.SPANISH -> "Exportar mes visible"
    }

fun AppStrings.selectedMonthsReadyMessage(rangeLabel: String): String = when (language) {
    AppLanguage.ENGLISH -> "Selected months: $rangeLabel. Now choose the export format."
    AppLanguage.ITALIAN -> "Mesi selezionati: $rangeLabel. Ora scegli il formato di export."
    AppLanguage.GERMAN -> "Ausgewählte Monate: $rangeLabel. Wähle jetzt das Exportformat."
    AppLanguage.FRENCH -> "Mois sélectionnés : $rangeLabel. Choisis maintenant le format d'export."
    AppLanguage.SPANISH -> "Meses seleccionados: $rangeLabel. Ahora elige el formato de exportación."
}

val AppStrings.selectFirstMonthInstruction: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "1/2 Select the first month."
        AppLanguage.ITALIAN -> "1/2 Seleziona il primo mese."
        AppLanguage.GERMAN -> "1/2 Wähle den ersten Monat."
        AppLanguage.FRENCH -> "1/2 Sélectionne le premier mois."
        AppLanguage.SPANISH -> "1/2 Selecciona el primer mes."
    }

val AppStrings.selectLastMonthInstruction: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "2/2 Select the last month."
        AppLanguage.ITALIAN -> "2/2 Seleziona l'ultimo mese."
        AppLanguage.GERMAN -> "2/2 Wähle den letzten Monat."
        AppLanguage.FRENCH -> "2/2 Sélectionne le dernier mois."
        AppLanguage.SPANISH -> "2/2 Selecciona el último mes."
    }

fun AppStrings.exportPeriodTitle(periodLabel: String): String = when (language) {
    AppLanguage.ENGLISH -> "Export $periodLabel"
    AppLanguage.ITALIAN -> "Esporta $periodLabel"
    AppLanguage.GERMAN -> "Export $periodLabel"
    AppLanguage.FRENCH -> "Exporter $periodLabel"
    AppLanguage.SPANISH -> "Exportar $periodLabel"
}

val AppStrings.exportSelectedMonthsDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The file will include the selected months, with a clearer summary and grouped consecutive days."
        AppLanguage.ITALIAN -> "Il file conterrà i mesi selezionati, con un riepilogo più leggibile e i giorni consecutivi raggruppati."
        AppLanguage.GERMAN -> "Die Datei enthält die ausgewählten Monate mit einer klareren Zusammenfassung und gruppierten aufeinanderfolgenden Tagen."
        AppLanguage.FRENCH -> "Le fichier contiendra les mois sélectionnés avec un résumé plus lisible et les jours consécutifs regroupés."
        AppLanguage.SPANISH -> "El archivo incluirá los meses seleccionados, con un resumen más legible y los días consecutivos agrupados."
    }

val AppStrings.exportVisibleMonthDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The file will include the visible month, with a clearer summary and grouped consecutive days."
        AppLanguage.ITALIAN -> "Il file conterrà il mese visibile, con un riepilogo più leggibile e i giorni consecutivi raggruppati."
        AppLanguage.GERMAN -> "Die Datei enthält den sichtbaren Monat mit einer klareren Zusammenfassung und gruppierten aufeinanderfolgenden Tagen."
        AppLanguage.FRENCH -> "Le fichier contiendra le mois visible avec un résumé plus lisible et les jours consécutifs regroupés."
        AppLanguage.SPANISH -> "El archivo incluirá el mes visible, con un resumen más legible y los días consecutivos agrupados."
    }

val AppStrings.exportTypeFilterTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export filter"
        AppLanguage.ITALIAN -> "Filtro export"
        AppLanguage.GERMAN -> "Exportfilter"
        AppLanguage.FRENCH -> "Filtre d'export"
        AppLanguage.SPANISH -> "Filtro de exportación"
    }

val AppStrings.exportTypeFilterDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Leave all types active to export everything, or choose only project, vacation, and permit combinations."
        AppLanguage.ITALIAN -> "Lascia tutti i tipi attivi per esportare tutto, oppure scegli solo le combinazioni di progetto, ferie e permesso."
        AppLanguage.GERMAN -> "Lass alle Typen aktiv, um alles zu exportieren, oder wähle nur Kombinationen aus Projekt, Urlaub und Genehmigung."
        AppLanguage.FRENCH -> "Laisse tous les types actifs pour tout exporter, ou choisis seulement les combinaisons de projet, congés et autorisation."
        AppLanguage.SPANISH -> "Deja todos los tipos activos para exportarlo todo, o elige solo las combinaciones de proyecto, vacaciones y permiso."
    }

val AppStrings.exportTypeFilterRequiredMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Select at least one type to enable export."
        AppLanguage.ITALIAN -> "Seleziona almeno un tipo per abilitare l'export."
        AppLanguage.GERMAN -> "Wähle mindestens einen Typ aus, um den Export zu aktivieren."
        AppLanguage.FRENCH -> "Sélectionne au moins un type pour activer l'export."
        AppLanguage.SPANISH -> "Selecciona al menos un tipo para habilitar la exportación."
    }

val AppStrings.activityWorkLocationFieldLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Work location"
        AppLanguage.ITALIAN -> "Svolta in"
        AppLanguage.GERMAN -> "Arbeitsort"
        AppLanguage.FRENCH -> "Lieu de travail"
        AppLanguage.SPANISH -> "Lugar de trabajo"
    }

val AppStrings.settingsTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.ITALIAN -> "Impostazioni"
        AppLanguage.GERMAN -> "Einstellungen"
        AppLanguage.FRENCH -> "Paramètres"
        AppLanguage.SPANISH -> "Configuración"
    }

val AppStrings.settingsDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Adjust readability, contrast, language, and layout without changing timesheet data."
        AppLanguage.ITALIAN -> "Regola leggibilità, contrasto, lingua e densità del calendario senza modificare i dati del timesheet."
        AppLanguage.GERMAN -> "Passe Lesbarkeit, Kontrast, Sprache und Layout an, ohne die Timesheet-Daten zu ändern."
        AppLanguage.FRENCH -> "Ajuste la lisibilité, le contraste, la langue et la mise en page sans modifier les données du timesheet."
        AppLanguage.SPANISH -> "Ajusta legibilidad, contraste, idioma y distribución sin modificar los datos del timesheet."
    }

val AppStrings.closeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Close"
        AppLanguage.ITALIAN -> "Chiudi"
        AppLanguage.GERMAN -> "Schließen"
        AppLanguage.FRENCH -> "Fermer"
        AppLanguage.SPANISH -> "Cerrar"
    }

val AppStrings.openSettingsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Open settings"
        AppLanguage.ITALIAN -> "Apri impostazioni"
        AppLanguage.GERMAN -> "Einstellungen öffnen"
        AppLanguage.FRENCH -> "Ouvrir les paramètres"
        AppLanguage.SPANISH -> "Abrir configuración"
    }

val AppStrings.closeSettingsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Close settings"
        AppLanguage.ITALIAN -> "Chiudi impostazioni"
        AppLanguage.GERMAN -> "Einstellungen schließen"
        AppLanguage.FRENCH -> "Fermer les paramètres"
        AppLanguage.SPANISH -> "Cerrar configuración"
    }

val AppStrings.themeTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Theme"
        AppLanguage.ITALIAN -> "Tema"
        AppLanguage.GERMAN -> "Thema"
        AppLanguage.FRENCH -> "Theme"
        AppLanguage.SPANISH -> "Tema"
    }

val AppStrings.textSizeTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Text size"
        AppLanguage.ITALIAN -> "Dimensione testo"
        AppLanguage.GERMAN -> "Textgröße"
        AppLanguage.FRENCH -> "Taille du texte"
        AppLanguage.SPANISH -> "Tamaño del texto"
    }

val AppStrings.languageTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Language"
        AppLanguage.ITALIAN -> "Lingua"
        AppLanguage.GERMAN -> "Sprache"
        AppLanguage.FRENCH -> "Langue"
        AppLanguage.SPANISH -> "Idioma"
    }

val AppStrings.languageDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose the app language. English is the default language."
        AppLanguage.ITALIAN -> "Scegli la lingua dell'app. L'inglese è la lingua predefinita."
        AppLanguage.GERMAN -> "Wähle die App-Sprache. Englisch ist die Standardsprache."
        AppLanguage.FRENCH -> "Choisis la langue de l'application. L'anglais est la langue par défaut."
        AppLanguage.SPANISH -> "Elige el idioma de la aplicación. El inglés es el idioma predeterminado."
    }

val AppStrings.exportUserFullNameTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export user"
        AppLanguage.ITALIAN -> "Nome utente export"
        AppLanguage.GERMAN -> "Exportbenutzer"
        AppLanguage.FRENCH -> "Utilisateur export"
        AppLanguage.SPANISH -> "Usuario de exportación"
    }

val AppStrings.exportUserFullNameDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Add the full name to exported file names and report metadata."
        AppLanguage.ITALIAN -> "Aggiungi nome e cognome al nome file esportato e ai metadati del report."
        AppLanguage.GERMAN -> "Fügt Vor- und Nachnamen zu exportierten Dateinamen und Bericht-Metadaten hinzu."
        AppLanguage.FRENCH -> "Ajoute le nom complet au fichier exporté et aux métadonnées du rapport."
        AppLanguage.SPANISH -> "Agrega nombre y apellidos al archivo exportado y a los metadatos del informe."
    }

val AppStrings.exportUserFullNameLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Full name"
        AppLanguage.ITALIAN -> "Nome e cognome"
        AppLanguage.GERMAN -> "Vor- und Nachname"
        AppLanguage.FRENCH -> "Nom et prénom"
        AppLanguage.SPANISH -> "Nombre y apellidos"
    }

val AppStrings.exportUserFullNamePlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Mario Rossi"
        AppLanguage.ITALIAN -> "Mario Rossi"
        AppLanguage.GERMAN -> "Mario Rossi"
        AppLanguage.FRENCH -> "Mario Rossi"
        AppLanguage.SPANISH -> "Mario Rossi"
    }

val AppStrings.exportMetadataTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export metadata"
        AppLanguage.ITALIAN -> "Metadati export"
        AppLanguage.GERMAN -> "Export-Metadaten"
        AppLanguage.FRENCH -> "Métadonnées d'export"
        AppLanguage.SPANISH -> "Metadatos de exportación"
    }

val AppStrings.exportMetadataDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Add optional details shown only inside exported files."
        AppLanguage.ITALIAN -> "Aggiungi dati opzionali mostrati solo all'interno dei file esportati."
        AppLanguage.GERMAN -> "Füge optionale Angaben hinzu, die nur in exportierten Dateien angezeigt werden."
        AppLanguage.FRENCH -> "Ajoute des informations facultatives affichées uniquement dans les fichiers exportés."
        AppLanguage.SPANISH -> "Agrega datos opcionales que se muestran solo dentro de los archivos exportados."
    }

val AppStrings.backupJsonTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "JSON backup"
        AppLanguage.ITALIAN -> "Backup JSON"
        AppLanguage.GERMAN -> "JSON-Backup"
        AppLanguage.FRENCH -> "Sauvegarde JSON"
        AppLanguage.SPANISH -> "Copia JSON"
    }

val AppStrings.backupJsonDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export a lightweight JSON backup with settings, activity catalog, and all saved timesheet days."
        AppLanguage.ITALIAN -> "Esporta un backup JSON leggero con impostazioni, catalogo attività e tutte le giornate salvate del timesheet."
        AppLanguage.GERMAN -> "Exportiere ein leichtes JSON-Backup mit Einstellungen, Aktivitätenkatalog und allen gespeicherten Timesheet-Tagen."
        AppLanguage.FRENCH -> "Exporte une sauvegarde JSON légère avec paramètres, catalogue d'activités et toutes les journées enregistrées du timesheet."
        AppLanguage.SPANISH -> "Exporta una copia JSON ligera con configuración, catálogo de actividades y todos los días guardados del timesheet."
    }

val AppStrings.backupJsonWarning: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Import replaces all local settings, activities, and saved days."
        AppLanguage.ITALIAN -> "L'import sostituisce tutte le impostazioni locali, le attività e le giornate salvate."
        AppLanguage.GERMAN -> "Der Import ersetzt alle lokalen Einstellungen, Aktivitäten und gespeicherten Tage."
        AppLanguage.FRENCH -> "L'import remplace tous les paramètres locaux, les activités et les journées enregistrées."
        AppLanguage.SPANISH -> "La importación sustituye toda la configuración local, las actividades y los días guardados."
    }

val AppStrings.backupJsonExportLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export JSON backup"
        AppLanguage.ITALIAN -> "Esporta backup JSON"
        AppLanguage.GERMAN -> "JSON-Backup exportieren"
        AppLanguage.FRENCH -> "Exporter la sauvegarde JSON"
        AppLanguage.SPANISH -> "Exportar copia JSON"
    }

val AppStrings.backupJsonImportLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Import JSON backup"
        AppLanguage.ITALIAN -> "Importa backup JSON"
        AppLanguage.GERMAN -> "JSON-Backup importieren"
        AppLanguage.FRENCH -> "Importer la sauvegarde JSON"
        AppLanguage.SPANISH -> "Importar copia JSON"
    }

val AppStrings.backupJsonExportingLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Exporting backup..."
        AppLanguage.ITALIAN -> "Esportazione backup..."
        AppLanguage.GERMAN -> "Backup wird exportiert..."
        AppLanguage.FRENCH -> "Export de la sauvegarde..."
        AppLanguage.SPANISH -> "Exportando copia..."
    }

val AppStrings.backupImportingLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Importing backup..."
        AppLanguage.ITALIAN -> "Importazione backup..."
        AppLanguage.GERMAN -> "Backup wird importiert..."
        AppLanguage.FRENCH -> "Import de la sauvegarde..."
        AppLanguage.SPANISH -> "Importando copia..."
    }

val AppStrings.backupImportConfirmTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Replace local data?"
        AppLanguage.ITALIAN -> "Sostituire i dati locali?"
        AppLanguage.GERMAN -> "Lokale Daten ersetzen?"
        AppLanguage.FRENCH -> "Remplacer les données locales ?"
        AppLanguage.SPANISH -> "¿Sustituir los datos locales?"
    }

fun AppStrings.backupImportConfirmBody(fileName: String): String = when (language) {
    AppLanguage.ENGLISH -> "Importing $fileName will replace all local settings, activities, and saved timesheet days."
    AppLanguage.ITALIAN -> "Importando $fileName verranno sostituite tutte le impostazioni locali, le attività e le giornate salvate del timesheet."
    AppLanguage.GERMAN -> "Beim Import von $fileName werden alle lokalen Einstellungen, Aktivitäten und gespeicherten Timesheet-Tage ersetzt."
    AppLanguage.FRENCH -> "L'import de $fileName remplacera tous les paramètres locaux, les activités et les journées enregistrées du timesheet."
    AppLanguage.SPANISH -> "Al importar $fileName se sustituirán toda la configuración local, las actividades y los días guardados del timesheet."
}

val AppStrings.backupImportReplaceLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Import and replace"
        AppLanguage.ITALIAN -> "Importa e sostituisci"
        AppLanguage.GERMAN -> "Importieren und ersetzen"
        AppLanguage.FRENCH -> "Importer et remplacer"
        AppLanguage.SPANISH -> "Importar y sustituir"
    }

val AppStrings.backupImportSuccessMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Backup imported successfully."
        AppLanguage.ITALIAN -> "Backup importato correttamente."
        AppLanguage.GERMAN -> "Backup erfolgreich importiert."
        AppLanguage.FRENCH -> "Sauvegarde importée avec succès."
        AppLanguage.SPANISH -> "Copia importada correctamente."
    }

val AppStrings.backupExportErrorMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to prepare the JSON backup."
        AppLanguage.ITALIAN -> "Impossibile preparare il backup JSON."
        AppLanguage.GERMAN -> "Das JSON-Backup konnte nicht vorbereitet werden."
        AppLanguage.FRENCH -> "Impossible de préparer la sauvegarde JSON."
        AppLanguage.SPANISH -> "No se pudo preparar la copia JSON."
    }

val AppStrings.backupImportInvalidJsonMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The selected file is not a valid TLInCompose backup."
        AppLanguage.ITALIAN -> "Il file selezionato non è un backup TLInCompose valido."
        AppLanguage.GERMAN -> "Die ausgewählte Datei ist kein gültiges TLInCompose-Backup."
        AppLanguage.FRENCH -> "Le fichier sélectionné n'est pas une sauvegarde TLInCompose valide."
        AppLanguage.SPANISH -> "El archivo seleccionado no es una copia válida de TLInCompose."
    }

val AppStrings.backupImportUnsupportedVersionMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The selected backup version is not supported."
        AppLanguage.ITALIAN -> "La versione del backup selezionato non è supportata."
        AppLanguage.GERMAN -> "Die ausgewählte Backup-Version wird nicht unterstützt."
        AppLanguage.FRENCH -> "La version de la sauvegarde sélectionnée n'est pas prise en charge."
        AppLanguage.SPANISH -> "La versión de la copia seleccionada no es compatible."
    }

val AppStrings.backupImportEmptyContentMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The selected backup file is empty."
        AppLanguage.ITALIAN -> "Il file di backup selezionato è vuoto."
        AppLanguage.GERMAN -> "Die ausgewählte Backup-Datei ist leer."
        AppLanguage.FRENCH -> "Le fichier de sauvegarde sélectionné est vide."
        AppLanguage.SPANISH -> "El archivo de copia seleccionado está vacío."
    }

val AppStrings.backupImportPersistenceErrorMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to apply the selected backup to local data."
        AppLanguage.ITALIAN -> "Impossibile applicare il backup selezionato ai dati locali."
        AppLanguage.GERMAN -> "Das ausgewählte Backup konnte nicht auf die lokalen Daten angewendet werden."
        AppLanguage.FRENCH -> "Impossible d'appliquer la sauvegarde sélectionnée aux données locales."
        AppLanguage.SPANISH -> "No se pudo aplicar la copia seleccionada a los datos locales."
    }

val AppStrings.exportOfficeNameLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Office name"
        AppLanguage.ITALIAN -> "Nome sede"
        AppLanguage.GERMAN -> "Standortname"
        AppLanguage.FRENCH -> "Nom du site"
        AppLanguage.SPANISH -> "Nombre de la sede"
    }

val AppStrings.exportWorkLocationLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Work location"
        AppLanguage.ITALIAN -> "Svolta in"
        AppLanguage.GERMAN -> "Arbeitsort"
        AppLanguage.FRENCH -> "Lieu de travail"
        AppLanguage.SPANISH -> "Lugar de trabajo"
    }

val AppStrings.exportOfficeNamePlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Milan HQ"
        AppLanguage.ITALIAN -> "Sede Milano"
        AppLanguage.GERMAN -> "Standort Mailand"
        AppLanguage.FRENCH -> "Site de Milan"
        AppLanguage.SPANISH -> "Sede de Milán"
    }

val AppStrings.exportEmployeeIdLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Employee ID"
        AppLanguage.ITALIAN -> "Dipendente ID"
        AppLanguage.GERMAN -> "Mitarbeiter-ID"
        AppLanguage.FRENCH -> "ID employé"
        AppLanguage.SPANISH -> "ID empleado"
    }

val AppStrings.exportEmployeeIdPlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "EMP-12345"
        AppLanguage.ITALIAN -> "EMP-12345"
        AppLanguage.GERMAN -> "EMP-12345"
        AppLanguage.FRENCH -> "EMP-12345"
        AppLanguage.SPANISH -> "EMP-12345"
    }

val AppStrings.exportPersonIdLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Person ID"
        AppLanguage.ITALIAN -> "Person ID"
        AppLanguage.GERMAN -> "Personen-ID"
        AppLanguage.FRENCH -> "ID personne"
        AppLanguage.SPANISH -> "ID persona"
    }

val AppStrings.exportPersonIdPlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "P-67890"
        AppLanguage.ITALIAN -> "P-67890"
        AppLanguage.GERMAN -> "P-67890"
        AppLanguage.FRENCH -> "P-67890"
        AppLanguage.SPANISH -> "P-67890"
    }

val AppStrings.pdfExportStyleTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "PDF export style"
        AppLanguage.ITALIAN -> "Stile export PDF"
        AppLanguage.GERMAN -> "PDF-Exportstil"
        AppLanguage.FRENCH -> "Style d'export PDF"
        AppLanguage.SPANISH -> "Estilo de exportación PDF"
    }

val AppStrings.pdfExportStyleDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose how PDF exports present the summary and activity rows."
        AppLanguage.ITALIAN -> "Scegli come presentare riepilogo e righe attività negli export PDF."
        AppLanguage.GERMAN -> "Wähle, wie Zusammenfassung und Aktivitätszeilen im PDF dargestellt werden."
        AppLanguage.FRENCH -> "Choisis comment présenter le résumé et les lignes d'activité dans les exports PDF."
        AppLanguage.SPANISH -> "Elige cómo presentar el resumen y las filas de actividad en las exportaciones PDF."
    }

fun AppStrings.pdfExportStyleLabel(style: PdfExportStyle): String = when (language) {
    AppLanguage.ENGLISH -> when (style) {
        PdfExportStyle.RETRO -> "Classic table"
        PdfExportStyle.SIMPLE_TABLE -> "Simple table"
        PdfExportStyle.COMPACT_LIST -> "Compact list"
        PdfExportStyle.DETAIL_BLOCKS -> "Detail blocks"
    }
    AppLanguage.ITALIAN -> when (style) {
        PdfExportStyle.RETRO -> "Tabella classica"
        PdfExportStyle.SIMPLE_TABLE -> "Tabella semplice"
        PdfExportStyle.COMPACT_LIST -> "Elenco compatto"
        PdfExportStyle.DETAIL_BLOCKS -> "Blocchi dettaglio"
    }
    AppLanguage.GERMAN -> when (style) {
        PdfExportStyle.RETRO -> "Klassische Tabelle"
        PdfExportStyle.SIMPLE_TABLE -> "Einfache Tabelle"
        PdfExportStyle.COMPACT_LIST -> "Kompakte Liste"
        PdfExportStyle.DETAIL_BLOCKS -> "Detailblöcke"
    }
    AppLanguage.FRENCH -> when (style) {
        PdfExportStyle.RETRO -> "Tableau classique"
        PdfExportStyle.SIMPLE_TABLE -> "Table simple"
        PdfExportStyle.COMPACT_LIST -> "Liste compacte"
        PdfExportStyle.DETAIL_BLOCKS -> "Blocs détail"
    }
    AppLanguage.SPANISH -> when (style) {
        PdfExportStyle.RETRO -> "Tabla clásica"
        PdfExportStyle.SIMPLE_TABLE -> "Tabla simple"
        PdfExportStyle.COMPACT_LIST -> "Lista compacta"
        PdfExportStyle.DETAIL_BLOCKS -> "Bloques detalle"
    }
}

fun AppStrings.pdfExportStyleOptionDescription(style: PdfExportStyle): String = when (language) {
    AppLanguage.ENGLISH -> when (style) {
        PdfExportStyle.RETRO -> "Uses a formal table with aligned columns and a dedicated line for each activity period."
        PdfExportStyle.SIMPLE_TABLE -> "Uses a lighter aligned table with clean spacing and separate period details."
        PdfExportStyle.COMPACT_LIST -> "Presents each activity as a compact list item with the key values on a few lines."
        PdfExportStyle.DETAIL_BLOCKS -> "Separates each activity into stacked detail blocks for easier reading."
    }
    AppLanguage.ITALIAN -> when (style) {
        PdfExportStyle.RETRO -> "Usa una tabella formale con colonne allineate e una riga dedicata ai periodi di ogni attività."
        PdfExportStyle.SIMPLE_TABLE -> "Usa una tabella più leggera con spaziatura pulita e dettagli dei periodi separati."
        PdfExportStyle.COMPACT_LIST -> "Presenta ogni attività come elemento compatto con i valori chiave su poche righe."
        PdfExportStyle.DETAIL_BLOCKS -> "Separa ogni attività in blocchi di dettaglio impilati, più facili da leggere."
    }
    AppLanguage.GERMAN -> when (style) {
        PdfExportStyle.RETRO -> "Verwendet eine formale Tabelle mit ausgerichteten Spalten und einer eigenen Zeile für jeden Zeitraum."
        PdfExportStyle.SIMPLE_TABLE -> "Verwendet eine leichtere ausgerichtete Tabelle mit sauberem Abstand und separaten Zeitraumdetails."
        PdfExportStyle.COMPACT_LIST -> "Zeigt jede Aktivität als kompakten Listeneintrag mit den wichtigsten Werten."
        PdfExportStyle.DETAIL_BLOCKS -> "Trennt jede Aktivität in gestapelte Detailblöcke für leichteres Lesen."
    }
    AppLanguage.FRENCH -> when (style) {
        PdfExportStyle.RETRO -> "Utilise un tableau formel avec des colonnes alignées et une ligne dédiée à chaque période."
        PdfExportStyle.SIMPLE_TABLE -> "Utilise un tableau plus léger, bien espacé, avec les périodes sur une ligne séparée."
        PdfExportStyle.COMPACT_LIST -> "Affiche chaque activité comme une ligne compacte avec les valeurs principales."
        PdfExportStyle.DETAIL_BLOCKS -> "Sépare chaque activité en blocs de détail empilés plus faciles à lire."
    }
    AppLanguage.SPANISH -> when (style) {
        PdfExportStyle.RETRO -> "Usa una tabla formal con columnas alineadas y una línea dedicada a cada periodo de actividad."
        PdfExportStyle.SIMPLE_TABLE -> "Usa una tabla más ligera, con espaciado limpio y los periodos en una línea separada."
        PdfExportStyle.COMPACT_LIST -> "Presenta cada actividad cómo un elemento compacto con los valores clave en pocas líneas."
        PdfExportStyle.DETAIL_BLOCKS -> "Separa cada actividad en bloques de detalle apilados para facilitar la lectura."
    }
}

val AppStrings.brandingTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Branding"
        AppLanguage.ITALIAN -> "Brandizzazione"
        AppLanguage.GERMAN -> "Branding"
        AppLanguage.FRENCH -> "Branding"
        AppLanguage.SPANISH -> "Branding"
    }

val AppStrings.brandingDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Upload one shared logo to show it in the app header and in PDF or Excel exports."
        AppLanguage.ITALIAN -> "Carica un logo condiviso da mostrare nell'header dell'app e negli export PDF o Excel."
        AppLanguage.GERMAN -> "Lade ein gemeinsames Logo hoch, um es im App-Header und in PDF- oder Excel-Exporten anzuzeigen."
        AppLanguage.FRENCH -> "Téléverse un logo partagé pour l'afficher dans l'en-tête de l'application et dans les exports PDF ou Excel."
        AppLanguage.SPANISH -> "Carga un logotipo compartido para mostrarlo en el encabezado de la app y en las exportaciones PDF o Excel."
    }

val AppStrings.brandingLogoPlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No logo"
        AppLanguage.ITALIAN -> "Nessun logo"
        AppLanguage.GERMAN -> "Kein Logo"
        AppLanguage.FRENCH -> "Aucun logo"
        AppLanguage.SPANISH -> "Sin logo"
    }

val AppStrings.uploadBrandingLogoLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Upload logo"
        AppLanguage.ITALIAN -> "Carica logo"
        AppLanguage.GERMAN -> "Logo hochladen"
        AppLanguage.FRENCH -> "Téléverser le logo"
        AppLanguage.SPANISH -> "Cargar logo"
    }

val AppStrings.replaceBrandingLogoLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Replace logo"
        AppLanguage.ITALIAN -> "Sostituisci logo"
        AppLanguage.GERMAN -> "Logo ersetzen"
        AppLanguage.FRENCH -> "Remplacer le logo"
        AppLanguage.SPANISH -> "Reemplazar logo"
    }

val AppStrings.removeBrandingLogoLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Remove logo"
        AppLanguage.ITALIAN -> "Rimuovi logo"
        AppLanguage.GERMAN -> "Logo entfernen"
        AppLanguage.FRENCH -> "Supprimer le logo"
        AppLanguage.SPANISH -> "Quitar logo"
    }

val AppStrings.invalidBrandingLogoMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The selected file is not a valid PNG, JPEG or SVG logo."
        AppLanguage.ITALIAN -> "Il file selezionato non è un logo PNG, JPEG o SVG valido."
        AppLanguage.GERMAN -> "Die ausgewählte Datei ist kein gültiges PNG-, JPEG- oder SVG-Logo."
        AppLanguage.FRENCH -> "Le fichier sélectionné n'est pas un logo PNG, JPEG ou SVG valide."
        AppLanguage.SPANISH -> "El archivo seleccionado no es un logo PNG, JPEG o SVG válido."
    }

fun AppStrings.brandingLogoTooLargeMessage(maxKilobytes: Int): String = when (language) {
    AppLanguage.ENGLISH -> "The selected logo is too large. Stay within ${maxKilobytes} KB after optimization."
    AppLanguage.ITALIAN -> "Il logo selezionato è troppo pesante. Resta entro ${maxKilobytes} KB dopo l'ottimizzazione."
    AppLanguage.GERMAN -> "Das ausgewählte Logo ist zu groß. Bleibe nach der Optimierung innerhalb von ${maxKilobytes} KB."
    AppLanguage.FRENCH -> "Le logo sélectionné est trop volumineux. Reste sous ${maxKilobytes} KB après l'optimisation."
    AppLanguage.SPANISH -> "El logo seleccionado es demasiado pesado. Mantenlo dentro de ${maxKilobytes} KB tras la optimización."
}

fun AppStrings.brandingLogoContentDescription(context: String): String = when (language) {
    AppLanguage.ENGLISH -> "Brand logo for $context"
    AppLanguage.ITALIAN -> "Logo brand per $context"
    AppLanguage.GERMAN -> "Brand-Logo für $context"
    AppLanguage.FRENCH -> "Logo de marque pour $context"
    AppLanguage.SPANISH -> "Logo de marca para $context"
}

val AppStrings.privacyTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Privacy policy"
        AppLanguage.ITALIAN -> "Privacy policy"
        AppLanguage.GERMAN -> "Datenschutz"
        AppLanguage.FRENCH -> "Politique de confidentialite"
        AppLanguage.SPANISH -> "Politica de privacidad"
    }

val AppStrings.privacyDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "This app does not collect personal data."
        AppLanguage.ITALIAN -> "Questa app non raccoglie dati personali."
        AppLanguage.GERMAN -> "Diese App sammelt keine personenbezogenen Daten."
        AppLanguage.FRENCH -> "Cette application ne collecte aucune donnée personnelle."
        AppLanguage.SPANISH -> "Esta app no recopila datos personales."
    }

val AppStrings.privacyBody: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No account, analytics, tracking, or cloud upload is required. Data stays on your device unless you explicitly export a file."
        AppLanguage.ITALIAN -> "Non sono richiesti account, analytics, tracciamenti o upload cloud. I dati restano sul dispositivo finché non esporti esplicitamente un file."
        AppLanguage.GERMAN -> "Es sind kein Konto, keine Analysen, kein Tracking und kein Cloud-Upload erforderlich. Die Daten bleiben auf deinem Gerät, bis du bewusst eine Datei exportierst."
        AppLanguage.FRENCH -> "Aucun compte, analytics, suivi ou envoi cloud n'est requis. Les données restent sur ton appareil jusqu'à un export explicite."
        AppLanguage.SPANISH -> "No se requiere cuenta, analítica, seguimiento ni carga en la nube. Los datos permanecen en tu dispositivo hasta que exportes un archivo de forma explícita."
    }

val AppStrings.thirdPartyLibrariesTitle: String
    get() = this[StringKey.ThirdPartyLibrariesTitle]

val AppStrings.thirdPartyLibrariesDescription: String
    get() = this[StringKey.ThirdPartyLibrariesDescription]

fun AppStrings.thirdPartyLibraryContentDescription(name: String): String =
    this[StringKey.ThirdPartyLibraryContentDescription(name)]

val AppStrings.thirdPartyLibraryOpenSite: String
    get() = this[StringKey.ThirdPartyLibraryOpenSite]

val AppStrings.developerSectionTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Developer"
        AppLanguage.ITALIAN -> "Sviluppatore"
        AppLanguage.GERMAN -> "Entwickler"
        AppLanguage.FRENCH -> "Developpeur"
        AppLanguage.SPANISH -> "Desarrollador"
    }

val AppStrings.developerSectionDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Contact details and availability for suggestions or future improvements."
        AppLanguage.ITALIAN -> "Dettagli di contatto e disponibilita per suggerimenti o futuri miglioramenti."
        AppLanguage.GERMAN -> "Kontaktdaten und Verfugbarkeit fur Vorschlage oder zukunftige Verbesserungen."
        AppLanguage.FRENCH -> "Coordonnees et disponibilite pour des suggestions ou de futures ameliorations."
        AppLanguage.SPANISH -> "Datos de contacto y disponibilidad para sugerencias o futuras mejoras."
    }

val AppStrings.developerGithubLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "GitHub"
        AppLanguage.ITALIAN -> "GitHub"
        AppLanguage.GERMAN -> "GitHub"
        AppLanguage.FRENCH -> "GitHub"
        AppLanguage.SPANISH -> "GitHub"
    }

val AppStrings.developerEmailLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Email"
        AppLanguage.ITALIAN -> "Email"
        AppLanguage.GERMAN -> "E-Mail"
        AppLanguage.FRENCH -> "E-mail"
        AppLanguage.SPANISH -> "Correo"
    }

val AppStrings.developerAvailabilityMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "I am open to suggestions and new developments to keep improving the app."
        AppLanguage.ITALIAN -> "Sono aperto a suggerimenti e a nuovi sviluppi per migliorare l'app."
        AppLanguage.GERMAN -> "Ich bin offen fur Vorschlage und neue Entwicklungen, um die App weiter zu verbessern."
        AppLanguage.FRENCH -> "Je suis ouvert aux suggestions et a de nouveaux developpements pour ameliorer l'application."
        AppLanguage.SPANISH -> "Estoy abierto a sugerencias y a nuevos desarrollos para mejorar la aplicacion."
    }

val AppStrings.developerOpenLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Open"
        AppLanguage.ITALIAN -> "Apri"
        AppLanguage.GERMAN -> "Offnen"
        AppLanguage.FRENCH -> "Ouvrir"
        AppLanguage.SPANISH -> "Abrir"
    }

val AppStrings.developerCopyLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Copy"
        AppLanguage.ITALIAN -> "Copia"
        AppLanguage.GERMAN -> "Kopieren"
        AppLanguage.FRENCH -> "Copier"
        AppLanguage.SPANISH -> "Copiar"
    }

val AppStrings.highContrastTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "High contrast"
        AppLanguage.ITALIAN -> "Alto contrasto"
        AppLanguage.GERMAN -> "Höher Kontrast"
        AppLanguage.FRENCH -> "Contraste élevé"
        AppLanguage.SPANISH -> "Alto contraste"
    }

val AppStrings.highContrastDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Creates a clearer separation between text, buttons, selections, and calendar cells."
        AppLanguage.ITALIAN -> "Rende più netta la separazione tra testo, pulsanti, selezioni e celle del calendario."
        AppLanguage.GERMAN -> "Sorgt für klarere Abgrenzung zwischen Text, Schaltflächen, Auswahl und Kalenderzellen."
        AppLanguage.FRENCH -> "Renforce la séparation entre le texte, les boutons, les sélections et les cellules du calendrier."
        AppLanguage.SPANISH -> "Hace más clara la separación entre texto, botones, selecciones y celdas del calendario."
    }

val AppStrings.comfortableLayoutTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Comfortable layout"
        AppLanguage.ITALIAN -> "Layout confortevole"
        AppLanguage.GERMAN -> "Komfortables Layout"
        AppLanguage.FRENCH -> "Mise en page confortable"
        AppLanguage.SPANISH -> "Diseño cómodo"
    }

val AppStrings.comfortableLayoutDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Increases spacing, button height, and minimum cell size for a more relaxed reading experience."
        AppLanguage.ITALIAN -> "Aumenta spazi, altezza dei pulsanti e dimensione minima delle celle per una lettura più rilassata."
        AppLanguage.GERMAN -> "Erhöht Abstände, Button-Höhe und minimale Zellgröße für entspannteres Lesen."
        AppLanguage.FRENCH -> "Augmente les espacements, la hauteur des boutons et la taille minimale des cellules pour une lecture plus confortable."
        AppLanguage.SPANISH -> "Aumenta espacios, altura de botones y tamaño mínimo de celdas para una lectura más cómoda."
    }

val AppStrings.focusModeTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Focus mode"
        AppLanguage.ITALIAN -> "Modalità concentrata"
        AppLanguage.GERMAN -> "Fokusmodus"
        AppLanguage.FRENCH -> "Mode concentration"
        AppLanguage.SPANISH -> "Modo concentracion"
    }

val AppStrings.focusModeDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Reduces secondary copy and hides extra details when you want a cleaner screen."
        AppLanguage.ITALIAN -> "Riduce il testo secondario e mostra meno dettagli superflui quando vuoi una schermata più pulita."
        AppLanguage.GERMAN -> "Reduziert Sekundartext und blendet überflussige Details aus für einen ruhigeren Bildschirm."
        AppLanguage.FRENCH -> "Reduit le texte secondaire et affiche moins de details superflus pour un ecran plus propre."
        AppLanguage.SPANISH -> "Reduce el texto secundario y muestra menos detalles superfluos cuando quieres una pantalla más limpia."
    }

val AppStrings.reduceMotionTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Reduce motion"
        AppLanguage.ITALIAN -> "Riduci animazioni"
        AppLanguage.GERMAN -> "Bewegungen reduzieren"
        AppLanguage.FRENCH -> "Réduire les animations"
        AppLanguage.SPANISH -> "Reducir animaciones"
    }

val AppStrings.reduceMotionDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Uses minimal transitions and avoids unnecessary movement."
        AppLanguage.ITALIAN -> "Usa transizioni minime ed evita movimenti non necessari."
        AppLanguage.GERMAN -> "Verwendet minimale Übergänge und vermeidet unnötige Bewegungen."
        AppLanguage.FRENCH -> "Utilise des transitions minimales et évite les mouvements inutiles."
        AppLanguage.SPANISH -> "Usa transiciones mínimas y evita movimientos innecesarios."
    }

val AppStrings.appConfigurationTitle: String get() = when (language) {
    AppLanguage.ENGLISH -> "App configuration"
    AppLanguage.ITALIAN -> "Configurazione app"
    AppLanguage.GERMAN -> "App-Konfiguration"
    AppLanguage.FRENCH -> "Configuration de l’application"
    AppLanguage.SPANISH -> "Configuración de la aplicación"
}
val AppStrings.appConfigurationDescription: String get() = when (language) {
    AppLanguage.ENGLISH -> "Export or import app settings and activity definitions in JSON."
    AppLanguage.ITALIAN -> "Esporta o importa le impostazioni e le attività dell’app in formato JSON."
    AppLanguage.GERMAN -> "App-Einstellungen und Aktivitätsdefinitionen im JSON-Format exportieren oder importieren."
    AppLanguage.FRENCH -> "Exportez ou importez les réglages et les activités de l’application au format JSON."
    AppLanguage.SPANISH -> "Exporta o importa la configuración y las actividades de la aplicación en formato JSON."
}
val AppStrings.appConfigurationWarning: String get() = when (language) {
    AppLanguage.ENGLISH -> "Timesheet entries are not included and will not be changed."
    AppLanguage.ITALIAN -> "Le registrazioni del timesheet non sono incluse e non verranno modificate."
    AppLanguage.GERMAN -> "Timesheet-Einträge sind nicht enthalten und werden nicht geändert."
    AppLanguage.FRENCH -> "Les entrées du timesheet ne sont pas incluses et ne seront pas modifiées."
    AppLanguage.SPANISH -> "Los registros del timesheet no están incluidos y no se modificarán."
}
val AppStrings.appConfigurationExport: String get() = when (language) { AppLanguage.ENGLISH -> "Export"; AppLanguage.ITALIAN -> "Esporta"; AppLanguage.GERMAN -> "Exportieren"; AppLanguage.FRENCH -> "Exporter"; AppLanguage.SPANISH -> "Exportar" }
val AppStrings.appConfigurationImport: String get() = when (language) { AppLanguage.ENGLISH -> "Import"; AppLanguage.ITALIAN -> "Importa"; AppLanguage.GERMAN -> "Importieren"; AppLanguage.FRENCH -> "Importer"; AppLanguage.SPANISH -> "Importar" }
val AppStrings.appConfigurationSuccess: String get() = when (language) { AppLanguage.ENGLISH -> "Configuration imported successfully."; AppLanguage.ITALIAN -> "Configurazione importata correttamente."; AppLanguage.GERMAN -> "Konfiguration erfolgreich importiert."; AppLanguage.FRENCH -> "Configuration importée avec succès."; AppLanguage.SPANISH -> "Configuración importada correctamente." }
val AppStrings.appConfigurationImportError: String get() = when (language) { AppLanguage.ENGLISH -> "Unable to import configuration."; AppLanguage.ITALIAN -> "Impossibile importare la configurazione."; AppLanguage.GERMAN -> "Konfiguration konnte nicht importiert werden."; AppLanguage.FRENCH -> "Impossible d’importer la configuration."; AppLanguage.SPANISH -> "No se puede importar la configuración." }
val AppStrings.appConfigurationExportError: String get() = when (language) { AppLanguage.ENGLISH -> "Unable to export configuration."; AppLanguage.ITALIAN -> "Impossibile esportare la configurazione."; AppLanguage.GERMAN -> "Konfiguration konnte nicht exportiert werden."; AppLanguage.FRENCH -> "Impossible d’exporter la configuration."; AppLanguage.SPANISH -> "No se puede exportar la configuración." }

val AppStrings.activityCatalogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "EXT activity catalog"
        AppLanguage.ITALIAN -> "Catalogo attività EXT"
        AppLanguage.GERMAN -> "EXT-Aktivitätenkatalog"
        AppLanguage.FRENCH -> "Catalogue d'activités EXT"
        AppLanguage.SPANISH -> "Catálogo de actividades EXT"
    }

fun AppStrings.activityCatalogDescription(defaultHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "Manage reusable entities for project, vacation, and permit. The default duration starts at ${defaultHours}h and weekends are usually considered non-working."
    AppLanguage.ITALIAN -> "Gestisci le entità riutilizzabili per progetto, ferie e permesso. La durata standard parte da ${defaultHours}h e il weekend è considerato normalmente non lavorativo."
    AppLanguage.GERMAN -> "Verwalte wiederverwendbare Einträge für Projekt, Urlaub und Genehmigung. Die Standarddauer startet bei ${defaultHours}h und Wochenenden gelten normalerweise als arbeitsfrei."
    AppLanguage.FRENCH -> "Gère les entités réutilisables pour projet, congés et autorisation. La durée standard commence à ${defaultHours}h et le week-end est généralement non travaillé."
    AppLanguage.SPANISH -> "Gestiona entidades reutilizables para proyecto, vacaciones y permiso. La duración estándar parte de ${defaultHours}h y el fin de semana suele considerarse no laborable."
}

val AppStrings.newExtEntity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "New EXT entity"
        AppLanguage.ITALIAN -> "Nuova entità EXT"
        AppLanguage.GERMAN -> "Neue EXT-Entität"
        AppLanguage.FRENCH -> "Nouvelle entité EXT"
        AppLanguage.SPANISH -> "Nueva entidad EXT"
    }

val AppStrings.showActivityCatalog: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Show EXT catalog"
        AppLanguage.ITALIAN -> "Mostra catalogo EXT"
        AppLanguage.GERMAN -> "EXT-Katalog anzeigen"
        AppLanguage.FRENCH -> "Afficher le catalogue EXT"
        AppLanguage.SPANISH -> "Mostrar catálogo EXT"
    }

val AppStrings.hideActivityCatalog: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Hide EXT catalog"
        AppLanguage.ITALIAN -> "Nascondi catalogo EXT"
        AppLanguage.GERMAN -> "EXT-Katalog ausblenden"
        AppLanguage.FRENCH -> "Masquer le catalogue EXT"
        AppLanguage.SPANISH -> "Ocultar catálogo EXT"
    }

val AppStrings.noSavedEntities: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No entities have been saved yet. Create the first EXT code to populate the catalog."
        AppLanguage.ITALIAN -> "Non ci sono ancora entità salvate. Crea il primo codice EXT per popolare il catalogo."
        AppLanguage.GERMAN -> "Es sind noch keine Entitäten gespeichert. Erstelle den ersten EXT-Code, um den Katalog zu populieren."
        AppLanguage.FRENCH -> "Aucune entité n'est encore enregistrée. Crée le premier code EXT pour alimenter le catalogue."
        AppLanguage.SPANISH -> "Todavía no hay entidades guardadas. Crea el primer código EXT para poblar el catálogo."
    }

fun AppStrings.baseDuration(hours: String): String = when (language) {
    AppLanguage.ENGLISH -> "Base duration ${hours}h"
    AppLanguage.ITALIAN -> "Durata base ${hours}h"
    AppLanguage.GERMAN -> "Basisdauer ${hours}h"
    AppLanguage.FRENCH -> "Durée de base ${hours}h"
    AppLanguage.SPANISH -> "Duración base ${hours}h"
}

fun AppStrings.createdOn(date: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Created ${formatDate(date)}"
    AppLanguage.ITALIAN -> "Creata ${formatDate(date)}"
    AppLanguage.GERMAN -> "Erstellt ${formatDate(date)}"
    AppLanguage.FRENCH -> "Créée ${formatDate(date)}"
    AppLanguage.SPANISH -> "Creada ${formatDate(date)}"
}

fun AppStrings.updatedOn(date: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Updated ${formatDate(date)}"
    AppLanguage.ITALIAN -> "Aggiornata ${formatDate(date)}"
    AppLanguage.GERMAN -> "Aktualisiert ${formatDate(date)}"
    AppLanguage.FRENCH -> "Mise à jour ${formatDate(date)}"
    AppLanguage.SPANISH -> "Actualizada ${formatDate(date)}"
}

val AppStrings.editLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Edit"
        AppLanguage.ITALIAN -> "Modifica"
        AppLanguage.GERMAN -> "Bearbeiten"
        AppLanguage.FRENCH -> "Modifier"
        AppLanguage.SPANISH -> "Editar"
    }

val AppStrings.deleteLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Delete"
        AppLanguage.ITALIAN -> "Elimina"
        AppLanguage.GERMAN -> "Löschen"
        AppLanguage.FRENCH -> "Supprimer"
        AppLanguage.SPANISH -> "Eliminar"
    }

fun AppStrings.projectIconContentDescription(title: String): String = when (language) {
    AppLanguage.ENGLISH -> "Project icon $title"
    AppLanguage.ITALIAN -> "Icona progetto $title"
    AppLanguage.GERMAN -> "Projekticon $title"
    AppLanguage.FRENCH -> "Icône du projet $title"
    AppLanguage.SPANISH -> "Icono del proyecto $title"
}

fun AppStrings.projectPresetContentDescription(label: String): String = when (language) {
    AppLanguage.ENGLISH -> "Project icon $label"
    AppLanguage.ITALIAN -> "Icona progetto $label"
    AppLanguage.GERMAN -> "Projekticon $label"
    AppLanguage.FRENCH -> "Icône du projet $label"
    AppLanguage.SPANISH -> "Icono del proyecto $label"
}

fun AppStrings.activityTypeIconContentDescription(label: String): String = when (language) {
    AppLanguage.ENGLISH -> "Activity icon $label"
    AppLanguage.ITALIAN -> "Icona attività $label"
    AppLanguage.GERMAN -> "Aktivitätssymbol $label"
    AppLanguage.FRENCH -> "Icône activité $label"
    AppLanguage.SPANISH -> "Icono de actividad $label"
}

val AppStrings.rangeLabelBoth: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "From/To"
        AppLanguage.ITALIAN -> "Da/A"
        AppLanguage.GERMAN -> "Von/Bis"
        AppLanguage.FRENCH -> "De/A"
        AppLanguage.SPANISH -> "De/A"
    }

val AppStrings.rangeLabelStart: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "From"
        AppLanguage.ITALIAN -> "Da"
        AppLanguage.GERMAN -> "Von"
        AppLanguage.FRENCH -> "De"
        AppLanguage.SPANISH -> "De"
    }

val AppStrings.rangeLabelEnd: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "To"
        AppLanguage.ITALIAN -> "A"
        AppLanguage.GERMAN -> "Bis"
        AppLanguage.FRENCH -> "A"
        AppLanguage.SPANISH -> "A"
    }

fun AppStrings.openDayDetail(date: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Open day details for ${formatDate(date)}"
    AppLanguage.ITALIAN -> "Apri il dettaglio del giorno ${formatDate(date)}"
    AppLanguage.GERMAN -> "Tagesdetails für ${formatDate(date)} öffnen"
    AppLanguage.FRENCH -> "Ouvrir le detail du jour ${formatDate(date)}"
    AppLanguage.SPANISH -> "Abrir el detalle del día ${formatDate(date)}"
}

val AppStrings.todayLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Today"
        AppLanguage.ITALIAN -> "Oggi"
        AppLanguage.GERMAN -> "Heute"
        AppLanguage.FRENCH -> "Aujourd'hui"
        AppLanguage.SPANISH -> "Hoy"
    }

fun AppStrings.moreActivities(count: Int): String = when (language) {
    AppLanguage.ENGLISH -> "+$count more"
    AppLanguage.ITALIAN -> "+$count altre"
    AppLanguage.GERMAN -> "+$count weitere"
    AppLanguage.FRENCH -> "+$count autres"
    AppLanguage.SPANISH -> "+$count más"
}

fun AppStrings.dayDialogTitle(date: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Day ${formatDate(date)}"
    AppLanguage.ITALIAN -> "Giorno ${formatDate(date)}"
    AppLanguage.GERMAN -> "Tag ${formatDate(date)}"
    AppLanguage.FRENCH -> "Jour ${formatDate(date)}"
    AppLanguage.SPANISH -> "Día ${formatDate(date)}"
}

fun AppStrings.dayRangeDialogTitle(startDate: LocalDate, endDate: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Days ${formatDateRange(startDate, endDate)}"
    AppLanguage.ITALIAN -> "Giorni ${formatDateRange(startDate, endDate)}"
    AppLanguage.GERMAN -> "Tage ${formatDateRange(startDate, endDate)}"
    AppLanguage.FRENCH -> "Jours ${formatDateRange(startDate, endDate)}"
    AppLanguage.SPANISH -> "Días ${formatDateRange(startDate, endDate)}"
}

val AppStrings.dayDialogDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Enter one or more activities and review the hours before saving."
        AppLanguage.ITALIAN -> "Inserisci una o più attività e verifica le ore prima del salvataggio."
        AppLanguage.GERMAN -> "Füge eine oder mehrere Aktivitäten hinzu und prüfe die Stunden vor dem Speichern."
        AppLanguage.FRENCH -> "Saisis une ou plusieurs activités et vérifie les heures avant l'enregistrement."
        AppLanguage.SPANISH -> "Introduce una o más actividades y revisa las horas antes de guardar."
    }

fun AppStrings.dayRangeDialogDescription(dayCount: Int): String = when (language) {
    AppLanguage.ENGLISH -> "The same activities will be saved on $dayCount selected days."
    AppLanguage.ITALIAN -> "Le stesse attività verranno salvate su $dayCount giorni selezionati."
    AppLanguage.GERMAN -> "Dieselben Aktivitäten werden für $dayCount ausgewählte Tage gespeichert."
    AppLanguage.FRENCH -> "Les mêmes activités seront enregistrées sur $dayCount jours sélectionnés."
    AppLanguage.SPANISH -> "Las mismas actividades se guardarán en $dayCount días seleccionados."
}

val AppStrings.weekendDayDialogNote: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Saturday and Sunday are usually non-working days. Add activities only when needed."
        AppLanguage.ITALIAN -> "Sabato e domenica sono considerati normalmente non lavorativi: registra attività solo quando necessario."
        AppLanguage.GERMAN -> "Samstag und Sonntag sind normalerweise arbeitsfrei. Erfasse Aktivitäten nur bei Bedarf."
        AppLanguage.FRENCH -> "Le samedi et le dimanche sont généralement non travaillés : saisis des activités uniquement si nécessaire."
        AppLanguage.SPANISH -> "Sábado y domingo suelen ser no laborables: registra actividades solo cuando sea necesario."
    }

val AppStrings.rangeWeekendDayDialogNote: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The selected range includes Saturday or Sunday. Review the hours before applying the same activities everywhere."
        AppLanguage.ITALIAN -> "L'intervallo selezionato include sabato o domenica. Controlla bene le ore prima di applicare le stesse attività a tutti i giorni."
        AppLanguage.GERMAN -> "Der ausgewählte Bereich enthält Samstag oder Sonntag. Prüfe die Stunden, bevor dieselben Aktivitäten überall angewendet werden."
        AppLanguage.FRENCH -> "La plage sélectionnée inclut un samedi ou un dimanche. Vérifie bien les heures avant d'appliquer les mêmes activités à tous les jours."
        AppLanguage.SPANISH -> "El rango seleccionado incluye sábado o domingo. Revisa bien las horas antes de aplicar las mismas actividades a todos los días."
    }

fun AppStrings.activityRowLabel(index: Int): String = when (language) {
    AppLanguage.ENGLISH -> "Activity ${index + 1}"
    AppLanguage.ITALIAN -> "Attività ${index + 1}"
    AppLanguage.GERMAN -> "Aktivität ${index + 1}"
    AppLanguage.FRENCH -> "Activité ${index + 1}"
    AppLanguage.SPANISH -> "Actividad ${index + 1}"
}

val AppStrings.removeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Remove"
        AppLanguage.ITALIAN -> "Rimuovi"
        AppLanguage.GERMAN -> "Entfernen"
        AppLanguage.FRENCH -> "Retirer"
        AppLanguage.SPANISH -> "Quitar"
    }

val AppStrings.selectExtEntity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Select EXT entity"
        AppLanguage.ITALIAN -> "Seleziona entità EXT"
        AppLanguage.GERMAN -> "EXT-Entität auswählen"
        AppLanguage.FRENCH -> "Sélectionner l'entité EXT"
        AppLanguage.SPANISH -> "Selecciona entidad EXT"
    }

fun AppStrings.noEntityAvailableForType(type: EntryType): String = when (language) {
    AppLanguage.ENGLISH -> "No ${entryTypeLabel(type).lowercase()} entity is available in the catalog. Create one in the dedicated section."
    AppLanguage.ITALIAN -> "Nessuna entità ${entryTypeLabel(type).lowercase()} disponibile nel catalogo. Creane una nella sezione dedicata."
    AppLanguage.GERMAN -> "Keine ${entryTypeLabel(type).lowercase()}-Entität im Katalog verfügbar. Erstelle eine im entsprechenden Bereich."
    AppLanguage.FRENCH -> "Aucune entité ${entryTypeLabel(type).lowercase()} disponible dans le catalogue. Crée-en une dans la section dédiée."
    AppLanguage.SPANISH -> "No hay ninguna entidad ${entryTypeLabel(type).lowercase()} disponible en el catálogo. Crea una en la sección dedicada."
}

val AppStrings.hoursOrFractionsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Hours or fractions"
        AppLanguage.ITALIAN -> "Ore o frazioni"
        AppLanguage.GERMAN -> "Stunden oder Bruchteile"
        AppLanguage.FRENCH -> "Heures ou fractions"
        AppLanguage.SPANISH -> "Horas o fracciones"
    }

val AppStrings.workLocationLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Work location"
        AppLanguage.ITALIAN -> "Luogo di lavoro"
        AppLanguage.GERMAN -> "Arbeitsort"
        AppLanguage.FRENCH -> "Lieu de travail"
        AppLanguage.SPANISH -> "Lugar de trabajo"
    }

val AppStrings.hoursPlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "E.g. 4 or 7.5"
        AppLanguage.ITALIAN -> "Es. 4 oppure 7,5"
        AppLanguage.GERMAN -> "Z. B. 4 oder 7,5"
        AppLanguage.FRENCH -> "Ex. 4 ou 7,5"
        AppLanguage.SPANISH -> "Ej. 4 o 7,5"
    }

val AppStrings.addActivityLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Add activity"
        AppLanguage.ITALIAN -> "Aggiungi attività"
        AppLanguage.GERMAN -> "Aktivität hinzufügen"
        AppLanguage.FRENCH -> "Ajouter une activité"
        AppLanguage.SPANISH -> "Agregar actividad"
    }

val AppStrings.cancelLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Cancel"
        AppLanguage.ITALIAN -> "Annulla"
        AppLanguage.GERMAN -> "Abbrechen"
        AppLanguage.FRENCH -> "Annuler"
        AppLanguage.SPANISH -> "Cancelar"
    }

val AppStrings.saveLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Save"
        AppLanguage.ITALIAN -> "Salva"
        AppLanguage.GERMAN -> "Speichern"
        AppLanguage.FRENCH -> "Enregistrer"
        AppLanguage.SPANISH -> "Guardar"
    }

fun AppStrings.saveSelectedDaysLabel(dayCount: Int): String = when (language) {
    AppLanguage.ENGLISH -> "Save on $dayCount days"
    AppLanguage.ITALIAN -> "Salva su $dayCount giorni"
    AppLanguage.GERMAN -> "Für $dayCount Tage speichern"
    AppLanguage.FRENCH -> "Enregistrer sur $dayCount jours"
    AppLanguage.SPANISH -> "Guardar en $dayCount días"
}

val AppStrings.coverIncompleteMonthLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Cover incomplete month"
        AppLanguage.ITALIAN -> "Copri mese incompleto"
        AppLanguage.GERMAN -> "Unvollständigen Monat abdecken"
        AppLanguage.FRENCH -> "Couvrir le mois incomplet"
        AppLanguage.SPANISH -> "Cubrir mes incompleto"
    }

val AppStrings.coverIncompleteMonthDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Applies this activity only to workdays that are still below the daily standard."
        AppLanguage.ITALIAN -> "Applica questa attività solo ai giorni lavorativi ancora sotto la giornata standard."
        AppLanguage.GERMAN -> "Wendet diese Aktivität nur auf Arbeitstage an, die noch unter dem Standardtag liegen."
        AppLanguage.FRENCH -> "Applique cette activité uniquement aux jours travaillés encore en dessous de la journée standard."
        AppLanguage.SPANISH -> "Aplica esta actividad solo a los días laborables que aún están por debajo de la jornada estándar."
    }

fun AppStrings.chooseEntityForType(type: EntryType): String = when (language) {
    AppLanguage.ENGLISH -> "Choose ${entryTypeLabel(type).lowercase()} entity"
    AppLanguage.ITALIAN -> "Scegli entità ${entryTypeLabel(type).lowercase()}"
    AppLanguage.GERMAN -> "${entryTypeLabel(type)}-Entität auswählen"
    AppLanguage.FRENCH -> "Choisir une entité ${entryTypeLabel(type).lowercase()}"
    AppLanguage.SPANISH -> "Elegir entidad ${entryTypeLabel(type).lowercase()}"
}

val AppStrings.noEntitiesForType: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No entities are available for this type. Create one in the EXT catalog and try again."
        AppLanguage.ITALIAN -> "Non ci sono entità disponibili per questo tipo. Creane una nel catalogo EXT e poi riprova."
        AppLanguage.GERMAN -> "Keine Entitäten für diesen Typ verfügbar. Erstelle eine im EXT-Katalog und versuche es erneut."
        AppLanguage.FRENCH -> "Aucune entité disponible pour ce type. Crée-en une dans le catalogue EXT puis réessaie."
        AppLanguage.SPANISH -> "No hay entidades disponibles para este tipo. Crea una en el catálogo EXT y vuelve a intentarlo."
    }

fun AppStrings.editEntityTitle(isEditing: Boolean): String = when (language) {
    AppLanguage.ENGLISH -> if (isEditing) "Edit EXT entity" else "New EXT entity"
    AppLanguage.ITALIAN -> if (isEditing) "Modifica entità EXT" else "Nuova entità EXT"
    AppLanguage.GERMAN -> if (isEditing) "EXT-Entität bearbeiten" else "Neue EXT-Entität"
    AppLanguage.FRENCH -> if (isEditing) "Modifier l'entité EXT" else "Nouvelle entité EXT"
    AppLanguage.SPANISH -> if (isEditing) "Editar entidad EXT" else "Nueva entidad EXT"
}

fun AppStrings.uniqueCode(code: String): String = when (language) {
    AppLanguage.ENGLISH -> "Unique code $code"
    AppLanguage.ITALIAN -> "Codice univoco $code"
    AppLanguage.GERMAN -> "Eindeutiger Code $code"
    AppLanguage.FRENCH -> "Code unique $code"
    AppLanguage.SPANISH -> "Código único $code"
}

val AppStrings.extCodeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "EXT code"
        AppLanguage.ITALIAN -> "Codice EXT"
        AppLanguage.GERMAN -> "EXT-Code"
        AppLanguage.FRENCH -> "Code EXT"
        AppLanguage.SPANISH -> "Código EXT"
    }

val AppStrings.protectedExtCodeHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "This built-in code is protected and cannot be changed."
        AppLanguage.ITALIAN -> "Questo codice predefinito è protetto e non può essere modificato."
        AppLanguage.GERMAN -> "Dieser vordefinierte Code ist geschützt und kann nicht geändert werden."
        AppLanguage.FRENCH -> "Ce code prédéfini est protégé et ne peut pas être modifié."
        AppLanguage.SPANISH -> "Este código predefinido está protegido y no se puede modificar."
    }

val AppStrings.protectedEntryTypeHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The type of this built-in activity is fixed."
        AppLanguage.ITALIAN -> "Il tipo di questa attività predefinita è fisso."
        AppLanguage.GERMAN -> "Der Typ dieser Standardaktivität ist festgelegt."
        AppLanguage.FRENCH -> "Le type de cette activité prédéfinie est fixe."
        AppLanguage.SPANISH -> "El tipo de esta actividad predefinida es fijo."
    }

val AppStrings.titleFieldLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Title"
        AppLanguage.ITALIAN -> "Titolo"
        AppLanguage.GERMAN -> "Titel"
        AppLanguage.FRENCH -> "Titre"
        AppLanguage.SPANISH -> "Título"
    }

val AppStrings.titlePlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "E.g. Client Alpha or Summer vacation"
        AppLanguage.ITALIAN -> "Es. Cliente Alfa oppure Ferie estive"
        AppLanguage.GERMAN -> "Z. B. Kunde Alpha oder Sommerurlaub"
        AppLanguage.FRENCH -> "Ex. Client Alpha ou congés d'été"
        AppLanguage.SPANISH -> "Ej. Cliente Alfa o vacaciones de verano"
    }

val AppStrings.descriptionFieldLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Description"
        AppLanguage.ITALIAN -> "Descrizione"
        AppLanguage.GERMAN -> "Beschreibung"
        AppLanguage.FRENCH -> "Description"
        AppLanguage.SPANISH -> "Descripción"
    }

val AppStrings.descriptionPlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Helpful details to recognize the activity"
        AppLanguage.ITALIAN -> "Dettagli utili per capire l'attività"
        AppLanguage.GERMAN -> "Hilfreiche Details, um die Aktivität zu erkennen"
        AppLanguage.FRENCH -> "Details utiles pour comprendre l'activité"
        AppLanguage.SPANISH -> "Detalles útiles para reconocer la actividad"
    }

val AppStrings.defaultDurationHoursLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Default duration (hours)"
        AppLanguage.ITALIAN -> "Durata predefinita (ore)"
        AppLanguage.GERMAN -> "Standarddauer (Stunden)"
        AppLanguage.FRENCH -> "Durée par défaut (heures)"
        AppLanguage.SPANISH -> "Duración predeterminada (horas)"
    }

val AppStrings.projectIconTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Project icon"
        AppLanguage.ITALIAN -> "Icona progetto"
        AppLanguage.GERMAN -> "Projekticon"
        AppLanguage.FRENCH -> "Icône du projet"
        AppLanguage.SPANISH -> "Icono del proyecto"
    }

val AppStrings.projectIconDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose a Material preset or upload a PNG, JPG, or WebP file from the device."
        AppLanguage.ITALIAN -> "Scegli un preset Material oppure carica un file PNG, JPG o WebP dal dispositivo."
        AppLanguage.GERMAN -> "Wähle ein Material-Preset oder lade eine PNG-, JPG- oder WebP-Datei vom Gerät hoch."
        AppLanguage.FRENCH -> "Choisis un preset Material ou charge un fichier PNG, JPG ou WebP depuis l'appareil."
        AppLanguage.SPANISH -> "Elige un preset Material o carga un archivo PNG, JPG o WebP desde el dispositivo."
    }

val AppStrings.customIconActive: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Custom icon enabled"
        AppLanguage.ITALIAN -> "Icona personalizzata attiva"
        AppLanguage.GERMAN -> "Benutzerdefiniertes Icon aktiv"
        AppLanguage.FRENCH -> "Icône personnalisée active"
        AppLanguage.SPANISH -> "Icono personalizado activo"
    }

fun AppStrings.presetIconActive(preset: ProjectIconPreset): String = when (language) {
    AppLanguage.ENGLISH -> "Preset ${projectIconPresetLabel(preset)}"
    AppLanguage.ITALIAN -> "Preset ${projectIconPresetLabel(preset)}"
    AppLanguage.GERMAN -> "Preset ${projectIconPresetLabel(preset)}"
    AppLanguage.FRENCH -> "Preset ${projectIconPresetLabel(preset)}"
    AppLanguage.SPANISH -> "Preset ${projectIconPresetLabel(preset)}"
}

val AppStrings.noIconSelected: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No icon selected"
        AppLanguage.ITALIAN -> "Nessuna icona selezionata"
        AppLanguage.GERMAN -> "Kein Icon ausgewählt"
        AppLanguage.FRENCH -> "Aucune icône sélectionnée"
        AppLanguage.SPANISH -> "Ningún icono seleccionado"
    }

val AppStrings.customIconHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The image will be stored together with the project entity."
        AppLanguage.ITALIAN -> "L'immagine verrà salvata insieme all'entità progetto."
        AppLanguage.GERMAN -> "Das Bild wird zusammen mit der Projekt-Entität gespeichert."
        AppLanguage.FRENCH -> "L'image sera enregistrée avec l'entité projet."
        AppLanguage.SPANISH -> "La imagen se guardará junto con la entidad del proyecto."
    }

val AppStrings.presetIconHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "You can replace it anytime with another preset or a file."
        AppLanguage.ITALIAN -> "Puoi sostituirla in qualsiasi momento con un altro preset o con un file."
        AppLanguage.GERMAN -> "Du kannst es jederzeit durch ein anderes Preset oder eine Datei ersetzen."
        AppLanguage.FRENCH -> "Tu peux le remplacer à tout moment par un autre preset ou un fichier."
        AppLanguage.SPANISH -> "Puedes reemplazarlo en cualquier momento por otro preset o por un archivo."
    }

val AppStrings.noIconHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Add a visual symbol to recognize the project faster."
        AppLanguage.ITALIAN -> "Aggiungi un simbolo visivo per riconoscere il progetto più rapidamente."
        AppLanguage.GERMAN -> "Füge ein visuelles Symbol hinzu, um das Projekt schneller zu erkennen."
        AppLanguage.FRENCH -> "Ajoute un symbole visuel pour reconnaître plus vite le projet."
        AppLanguage.SPANISH -> "Agrega un símbolo visual para reconocer el proyecto más rápido."
    }

val AppStrings.uploadFromFile: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Upload from file"
        AppLanguage.ITALIAN -> "Carica da file"
        AppLanguage.GERMAN -> "Aus Datei laden"
        AppLanguage.FRENCH -> "Charger depuis un fichier"
        AppLanguage.SPANISH -> "Cargar desde archivo"
    }

val AppStrings.removeIconLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Remove icon"
        AppLanguage.ITALIAN -> "Rimuovi icona"
        AppLanguage.GERMAN -> "Icon entfernen"
        AppLanguage.FRENCH -> "Retirer l'icône"
        AppLanguage.SPANISH -> "Quitar icono"
    }

val AppStrings.projectUrlLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Project URL"
        AppLanguage.ITALIAN -> "URL progetto"
        AppLanguage.GERMAN -> "Projekt-URL"
        AppLanguage.FRENCH -> "URL du projet"
        AppLanguage.SPANISH -> "URL del proyecto"
    }

val AppStrings.projectUrlPlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "https://example.com/client-alpha"
        AppLanguage.ITALIAN -> "https://esempio.it/progetto-alfa"
        AppLanguage.GERMAN -> "https://beispiel.de/projekt-alpha"
        AppLanguage.FRENCH -> "https://exemple.fr/projet-alpha"
        AppLanguage.SPANISH -> "https://ejemplo.es/proyecto-alfa"
    }

fun AppStrings.saveEntityLabel(isEditing: Boolean): String = when (language) {
    AppLanguage.ENGLISH -> if (isEditing) "Save changes" else "Create entity"
    AppLanguage.ITALIAN -> if (isEditing) "Salva modifiche" else "Crea entità"
    AppLanguage.GERMAN -> if (isEditing) "Änderungen speichern" else "Entität erstellen"
    AppLanguage.FRENCH -> if (isEditing) "Enregistrer les modifications" else "Créer l'entité"
    AppLanguage.SPANISH -> if (isEditing) "Guardar cambios" else "Crear entidad"
}

fun AppStrings.deleteExtCode(code: String): String = when (language) {
    AppLanguage.ENGLISH -> "Delete $code"
    AppLanguage.ITALIAN -> "Elimina $code"
    AppLanguage.GERMAN -> "$code löschen"
    AppLanguage.FRENCH -> "Supprimer $code"
    AppLanguage.SPANISH -> "Eliminar $code"
}

fun AppStrings.deleteEntityDescription(title: String): String = when (language) {
    AppLanguage.ENGLISH -> "The entity \"$title\" will be removed from the catalog. Existing calendar entries will keep their stored snapshot."
    AppLanguage.ITALIAN -> "L'entità \"$title\" verrà rimossa dal catalogo. Le registrazioni già salvate nel calendario manterranno comunque il loro snapshot."
    AppLanguage.GERMAN -> "Die Entität \"$title\" wird aus dem Katalog entfernt. Bereits gespeicherte Kalendereinträge behalten ihren Snapshot."
    AppLanguage.FRENCH -> "L'entité \"$title\" sera retirée du catalogue. Les enregistrements déjà sauvegardés conserveront leur instantané."
    AppLanguage.SPANISH -> "La entidad \"$title\" se eliminará del catálogo. Los registros ya guardados en el calendario conservarán su snapshot."
}

fun AppStrings.exportDialogTitle(title: String): String = title

fun AppStrings.dayCellDescription(cellDate: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Day ${formatDate(cellDate)}"
    AppLanguage.ITALIAN -> "Giorno ${formatDate(cellDate)}"
    AppLanguage.GERMAN -> "Tag ${formatDate(cellDate)}"
    AppLanguage.FRENCH -> "Jour ${formatDate(cellDate)}"
    AppLanguage.SPANISH -> "Día ${formatDate(cellDate)}"
}

val AppStrings.currentDayPhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "today"
        AppLanguage.ITALIAN -> "oggi"
        AppLanguage.GERMAN -> "heute"
        AppLanguage.FRENCH -> "aujourd'hui"
        AppLanguage.SPANISH -> "hoy"
    }

val AppStrings.outsideCurrentMonthPhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "outside the current month"
        AppLanguage.ITALIAN -> "fuori dal mese corrente"
        AppLanguage.GERMAN -> "ausserhalb des aktuellen Monats"
        AppLanguage.FRENCH -> "hors du mois en cours"
        AppLanguage.SPANISH -> "fuera del mes actual"
    }

fun AppStrings.totalHoursPhrase(hours: String): String = when (language) {
    AppLanguage.ENGLISH -> "$hours total hours"
    AppLanguage.ITALIAN -> "$hours ore totali"
    AppLanguage.GERMAN -> "$hours Gesamtstunden"
    AppLanguage.FRENCH -> "$hours heures au total"
    AppLanguage.SPANISH -> "$hours horas totales"
}

val AppStrings.dailyHoursExceededLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Daily hour overrun"
        AppLanguage.ITALIAN -> "Sforamento ore giornaliere"
        AppLanguage.GERMAN -> "Uberschreitung der Tagesstunden"
        AppLanguage.FRENCH -> "Depassement des heures quotidiennes"
        AppLanguage.SPANISH -> "Exceso de horas díarias"
    }

fun AppStrings.dailyHoursExceededMessage(totalHours: String, limitHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "Logged $totalHours h, above the configured daily limit of $limitHours h."
    AppLanguage.ITALIAN -> "Registrate ${totalHours}h, oltre il limite giornaliero impostato di ${limitHours}h."
    AppLanguage.GERMAN -> "${totalHours}h erfasst, über dem eingestellten Tageslimit von ${limitHours}h."
    AppLanguage.FRENCH -> "${totalHours}h enregistrées, au-dessus de la limite quotidienne configurée de ${limitHours}h."
    AppLanguage.SPANISH -> "${totalHours}h registradas, por encima del límite diario configurado de ${limitHours}h."
}

fun AppStrings.activityCountPhrase(count: Int): String = when (language) {
    AppLanguage.ENGLISH -> "$count activities"
    AppLanguage.ITALIAN -> "$count attività"
    AppLanguage.GERMAN -> "$count Aktivitäten"
    AppLanguage.FRENCH -> "$count activités"
    AppLanguage.SPANISH -> "$count actividades"
}

val AppStrings.rangeStartAndEndPhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "range start and end"
        AppLanguage.ITALIAN -> "inizio e fine intervallo"
        AppLanguage.GERMAN -> "Start und Ende des Bereichs"
        AppLanguage.FRENCH -> "début et fin de la plage"
        AppLanguage.SPANISH -> "inicio y fin del rango"
    }

val AppStrings.rangeStartPhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "range start"
        AppLanguage.ITALIAN -> "inizio intervallo"
        AppLanguage.GERMAN -> "Start des Bereichs"
        AppLanguage.FRENCH -> "début de la plage"
        AppLanguage.SPANISH -> "inicio del rango"
    }

val AppStrings.rangeEndPhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "range end"
        AppLanguage.ITALIAN -> "fine intervallo"
        AppLanguage.GERMAN -> "Ende des Bereichs"
        AppLanguage.FRENCH -> "fin de la plage"
        AppLanguage.SPANISH -> "fin del rango"
    }

val AppStrings.insideSelectedRangePhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "inside the selected range"
        AppLanguage.ITALIAN -> "dentro l'intervallo selezionato"
        AppLanguage.GERMAN -> "innerhalb des ausgewählten Bereichs"
        AppLanguage.FRENCH -> "dans la plage sélectionnée"
        AppLanguage.SPANISH -> "dentro del rango seleccionado"
    }

val AppStrings.checkDayFieldsBeforeSaving: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Review the highlighted fields before saving."
        AppLanguage.ITALIAN -> "Controlla i campi evidenziati prima di salvare."
        AppLanguage.GERMAN -> "prüfe die markierten Felder vor dem Speichern."
        AppLanguage.FRENCH -> "Vérifie les champs mis en évidence avant d'enregistrer."
        AppLanguage.SPANISH -> "Revisa los campos resaltados antes de guardar."
    }

val AppStrings.unableToSaveSelectedDay: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to save the selected day."
        AppLanguage.ITALIAN -> "Impossibile salvare il giorno selezionato."
        AppLanguage.GERMAN -> "Der ausgewählte Tag konnte nicht gespeichert werden."
        AppLanguage.FRENCH -> "Impossible d'enregistrer le jour sélectionné."
        AppLanguage.SPANISH -> "No se pudo guardar el día seleccionado."
    }

val AppStrings.unableToSaveSelectedDays: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to save the selected days."
        AppLanguage.ITALIAN -> "Impossibile salvare i giorni selezionati."
        AppLanguage.GERMAN -> "Die ausgewählten Tage konnten nicht gespeichert werden."
        AppLanguage.FRENCH -> "Impossible d'enregistrer les jours sélectionnés."
        AppLanguage.SPANISH -> "No se pudieron guardar los días seleccionados."
    }

val AppStrings.unableToCoverIncompleteMonth: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to cover the incomplete month."
        AppLanguage.ITALIAN -> "Impossibile coprire il mese incompleto."
        AppLanguage.GERMAN -> "Der unvollständige Monat konnte nicht abgedeckt werden."
        AppLanguage.FRENCH -> "Impossible de couvrir le mois incomplet."
        AppLanguage.SPANISH -> "No se pudo cubrir el mes incompleto."
    }

fun AppStrings.chooseLighterImage(maxKilobytes: Int): String = when (language) {
    AppLanguage.ENGLISH -> "Choose a lighter image: maximum ${maxKilobytes} KB."
    AppLanguage.ITALIAN -> "Scegli un'immagine più leggera: massimo ${maxKilobytes} KB."
    AppLanguage.GERMAN -> "Wähle ein leichteres Bild: maximal ${maxKilobytes} KB."
    AppLanguage.FRENCH -> "Choisis une image plus légère : maximum ${maxKilobytes} Ko."
    AppLanguage.SPANISH -> "Elige una imagen más ligera: máximo ${maxKilobytes} KB."
}

val AppStrings.checkEntityFieldsBeforeSaving: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Review the EXT entity fields before saving."
        AppLanguage.ITALIAN -> "Controlla i campi dell'entità EXT prima di salvare."
        AppLanguage.GERMAN -> "prüfe die Felder der EXT-Entität vor dem Speichern."
        AppLanguage.FRENCH -> "Vérifie les champs de l'entité EXT avant d'enregistrer."
        AppLanguage.SPANISH -> "Revisa los campos de la entidad EXT antes de guardar."
    }

val AppStrings.unableToSaveEntity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to save the EXT entity."
        AppLanguage.ITALIAN -> "Impossibile salvare l'entità EXT."
        AppLanguage.GERMAN -> "Die EXT-Entität konnte nicht gespeichert werden."
        AppLanguage.FRENCH -> "Impossible d'enregistrer l'entité EXT."
        AppLanguage.SPANISH -> "No se pudo guardar la entidad EXT."
    }

val AppStrings.unableToDeleteEntity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to delete the selected entity."
        AppLanguage.ITALIAN -> "Impossibile eliminare l'entità selezionata."
        AppLanguage.GERMAN -> "Die ausgewählte Entität konnte nicht gelöscht werden."
        AppLanguage.FRENCH -> "Impossible de supprimer l'entité sélectionnée."
        AppLanguage.SPANISH -> "No se pudo eliminar la entidad seleccionada."
    }

val AppStrings.unableToPrepareExport: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to prepare the requested export."
        AppLanguage.ITALIAN -> "Impossibile preparare l'export richiesto."
        AppLanguage.GERMAN -> "Der angeforderte Export konnte nicht vorbereitet werden."
        AppLanguage.FRENCH -> "Impossible de préparer l'export demandé."
        AppLanguage.SPANISH -> "No se pudo preparar la exportación solicitada."
    }

val AppStrings.exportSaveDialogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Save export"
        AppLanguage.ITALIAN -> "Salva esportazione"
        AppLanguage.GERMAN -> "Export speichern"
        AppLanguage.FRENCH -> "Enregistrer l'export"
        AppLanguage.SPANISH -> "Guardar exportación"
    }

val AppStrings.saveCancelledMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Save cancelled."
        AppLanguage.ITALIAN -> "Salvataggio annullato."
        AppLanguage.GERMAN -> "Speichern abgebrochen."
        AppLanguage.FRENCH -> "Enregistrement annulé."
        AppLanguage.SPANISH -> "Guardado cancelado."
    }

val AppStrings.invalidDestinationMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Invalid destination."
        AppLanguage.ITALIAN -> "Destinazione non valida."
        AppLanguage.GERMAN -> "Ungültiges Ziel."
        AppLanguage.FRENCH -> "Destination non valide."
        AppLanguage.SPANISH -> "Destino no válido."
    }

fun AppStrings.fileSavedMessage(fileName: String): String = when (language) {
    AppLanguage.ENGLISH -> "$fileName saved successfully."
    AppLanguage.ITALIAN -> "$fileName salvato correttamente."
    AppLanguage.GERMAN -> "$fileName wurde erfolgreich gespeichert."
    AppLanguage.FRENCH -> "$fileName a été enregistré avec succès."
    AppLanguage.SPANISH -> "$fileName se guardó correctamente."
}

val AppStrings.saveErrorMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Error while saving the file."
        AppLanguage.ITALIAN -> "Errore durante il salvataggio del file."
        AppLanguage.GERMAN -> "Fehler beim Speichern der Datei."
        AppLanguage.FRENCH -> "Erreur lors de l'enregistrement du fichier."
        AppLanguage.SPANISH -> "Error al guardar el archivo."
    }

val AppStrings.chooseProjectIconDialogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose project icon"
        AppLanguage.ITALIAN -> "Scegli icona progetto"
        AppLanguage.GERMAN -> "Projekticon auswählen"
        AppLanguage.FRENCH -> "Choisir l'icône du projet"
        AppLanguage.SPANISH -> "Elegir icono del proyecto"
    }

val AppStrings.chooseBrandLogoDialogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose brand logo"
        AppLanguage.ITALIAN -> "Scegli logo brand"
        AppLanguage.GERMAN -> "Brand-Logo auswählen"
        AppLanguage.FRENCH -> "Choisir le logo de marque"
        AppLanguage.SPANISH -> "Elegir logo de marca"
    }

val AppStrings.imageFilesLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Images"
        AppLanguage.ITALIAN -> "Immagini"
        AppLanguage.GERMAN -> "Bilder"
        AppLanguage.FRENCH -> "Images"
        AppLanguage.SPANISH -> "Imágenes"
    }

val AppStrings.jsonFilesLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "JSON files"
        AppLanguage.ITALIAN -> "File JSON"
        AppLanguage.GERMAN -> "JSON-Dateien"
        AppLanguage.FRENCH -> "Fichiers JSON"
        AppLanguage.SPANISH -> "Archivos JSON"
    }

val AppStrings.chooseBackupFileDialogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose backup file"
        AppLanguage.ITALIAN -> "Scegli file di backup"
        AppLanguage.GERMAN -> "Backup-Datei auswählen"
        AppLanguage.FRENCH -> "Choisir le fichier de sauvegarde"
        AppLanguage.SPANISH -> "Elegir archivo de copia"
    }

val AppStrings.unableToReadSelectedFile: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to read the selected file."
        AppLanguage.ITALIAN -> "Impossibile leggere il file selezionato."
        AppLanguage.GERMAN -> "Die ausgewählte Datei konnte nicht gelesen werden."
        AppLanguage.FRENCH -> "Impossible de lire le fichier sélectionné."
        AppLanguage.SPANISH -> "No se pudo leer el archivo seleccionado."
    }

val AppStrings.unableToReadSelectedImage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to read the selected image."
        AppLanguage.ITALIAN -> "Impossibile leggere l'immagine selezionata."
        AppLanguage.GERMAN -> "Das ausgewählte Bild konnte nicht gelesen werden."
        AppLanguage.FRENCH -> "Impossible de lire l'image sélectionnée."
        AppLanguage.SPANISH -> "No se pudo leer la imagen seleccionada."
    }

val AppStrings.unableToOpenDestinationFile: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to open the destination file."
        AppLanguage.ITALIAN -> "Impossibile aprire il file di destinazione."
        AppLanguage.GERMAN -> "Die Zieldatei konnte nicht geöffnet werden."
        AppLanguage.FRENCH -> "Impossible d'ouvrir le fichier de destination."
        AppLanguage.SPANISH -> "No se pudo abrir el archivo de destino."
    }

val AppStrings.appFileLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "File"
        AppLanguage.ITALIAN -> "File"
        AppLanguage.GERMAN -> "Datei"
        AppLanguage.FRENCH -> "Fichier"
        AppLanguage.SPANISH -> "Archivo"
    }

val AppStrings.exportReportLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Report"
        AppLanguage.ITALIAN -> "Report"
        AppLanguage.GERMAN -> "Bericht"
        AppLanguage.FRENCH -> "Rapport"
        AppLanguage.SPANISH -> "Informe"
    }

val AppStrings.exportPeriodLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Period"
        AppLanguage.ITALIAN -> "Periodo"
        AppLanguage.GERMAN -> "Zeitraum"
        AppLanguage.FRENCH -> "Période"
        AppLanguage.SPANISH -> "Periodo"
    }

val AppStrings.exportUserLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "User"
        AppLanguage.ITALIAN -> "Utente"
        AppLanguage.GERMAN -> "Benutzer"
        AppLanguage.FRENCH -> "Utilisateur"
        AppLanguage.SPANISH -> "Usuario"
    }

val AppStrings.exportOfficeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Office"
        AppLanguage.ITALIAN -> "Sede"
        AppLanguage.GERMAN -> "Standort"
        AppLanguage.FRENCH -> "Site"
        AppLanguage.SPANISH -> "Sede"
    }

val AppStrings.exportEmployeeIdMetadataLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Employee ID"
        AppLanguage.ITALIAN -> "Dipendente ID"
        AppLanguage.GERMAN -> "Mitarbeiter-ID"
        AppLanguage.FRENCH -> "ID employé"
        AppLanguage.SPANISH -> "ID empleado"
    }

val AppStrings.exportPersonIdMetadataLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Person ID"
        AppLanguage.ITALIAN -> "Person ID"
        AppLanguage.GERMAN -> "Personen-ID"
        AppLanguage.FRENCH -> "ID personne"
        AppLanguage.SPANISH -> "ID persona"
    }

val AppStrings.exportRecordedDaysLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Recorded days"
        AppLanguage.ITALIAN -> "Giorni registrati"
        AppLanguage.GERMAN -> "Erfasste Tage"
        AppLanguage.FRENCH -> "Jours saisis"
        AppLanguage.SPANISH -> "Días registrados"
    }

val AppStrings.exportActivitiesLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Activities"
        AppLanguage.ITALIAN -> "Attività"
        AppLanguage.GERMAN -> "Aktivitäten"
        AppLanguage.FRENCH -> "Activités"
        AppLanguage.SPANISH -> "Actividades"
    }

val AppStrings.exportTotalHoursLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Total hours"
        AppLanguage.ITALIAN -> "Ore totali"
        AppLanguage.GERMAN -> "Gesamtstunden"
        AppLanguage.FRENCH -> "Heures totales"
        AppLanguage.SPANISH -> "Horas totales"
    }

val AppStrings.exportTypeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Type"
        AppLanguage.ITALIAN -> "Tipo"
        AppLanguage.GERMAN -> "Typ"
        AppLanguage.FRENCH -> "Type"
        AppLanguage.SPANISH -> "Tipo"
    }

val AppStrings.exportGeneratedAtLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Exported at"
        AppLanguage.ITALIAN -> "Esportato il"
        AppLanguage.GERMAN -> "Exportiert am"
        AppLanguage.FRENCH -> "Exporté le"
        AppLanguage.SPANISH -> "Exportado el"
    }

fun AppStrings.exportGeneratedAtValue(instant: Instant): String = formatInstant(instant)

fun AppStrings.exportReadableDate(date: kotlinx.datetime.LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "${monthNames[date.month.ordinal]} ${date.day}, ${date.year}"
    AppLanguage.ITALIAN -> "${date.day} ${monthNames[date.month.ordinal]} ${date.year}"
    AppLanguage.GERMAN -> "${date.day}. ${monthNames[date.month.ordinal]} ${date.year}"
    AppLanguage.FRENCH -> "${date.day} ${monthNames[date.month.ordinal]} ${date.year}"
    AppLanguage.SPANISH -> "${date.day} de ${monthNames[date.month.ordinal]} de ${date.year}"
}

fun AppStrings.exportReadableDateRange(
    startDate: kotlinx.datetime.LocalDate,
    endDate: kotlinx.datetime.LocalDate,
): String {
    if (startDate == endDate) return exportReadableDate(startDate)

    val sameYear = startDate.year == endDate.year
    val sameMonth = sameYear && startDate.month == endDate.month

    return when (language) {
        AppLanguage.ENGLISH -> when {
            sameMonth -> "from ${monthNames[startDate.month.ordinal]} ${startDate.day} to ${endDate.day}, ${startDate.year}"
            sameYear -> "from ${monthNames[startDate.month.ordinal]} ${startDate.day} to ${monthNames[endDate.month.ordinal]} ${endDate.day}, ${startDate.year}"
            else -> "from ${exportReadableDate(startDate)} to ${exportReadableDate(endDate)}"
        }

        AppLanguage.ITALIAN -> when {
            sameMonth -> "dal ${startDate.day} al ${endDate.day} ${monthNames[startDate.month.ordinal]} ${startDate.year}"
            sameYear -> "dal ${startDate.day} ${monthNames[startDate.month.ordinal]} al ${endDate.day} ${monthNames[endDate.month.ordinal]} ${startDate.year}"
            else -> "dal ${exportReadableDate(startDate)} al ${exportReadableDate(endDate)}"
        }

        AppLanguage.GERMAN -> when {
            sameMonth -> "vom ${startDate.day}. bis ${endDate.day}. ${monthNames[startDate.month.ordinal]} ${startDate.year}"
            sameYear -> "vom ${startDate.day}. ${monthNames[startDate.month.ordinal]} bis ${endDate.day}. ${monthNames[endDate.month.ordinal]} ${startDate.year}"
            else -> "vom ${exportReadableDate(startDate)} bis ${exportReadableDate(endDate)}"
        }

        AppLanguage.FRENCH -> when {
            sameMonth -> "du ${startDate.day} au ${endDate.day} ${monthNames[startDate.month.ordinal]} ${startDate.year}"
            sameYear -> "du ${startDate.day} ${monthNames[startDate.month.ordinal]} au ${endDate.day} ${monthNames[endDate.month.ordinal]} ${startDate.year}"
            else -> "du ${exportReadableDate(startDate)} au ${exportReadableDate(endDate)}"
        }

        AppLanguage.SPANISH -> when {
            sameMonth -> "del ${startDate.day} al ${endDate.day} de ${monthNames[startDate.month.ordinal]} de ${startDate.year}"
            sameYear -> "del ${startDate.day} de ${monthNames[startDate.month.ordinal]} al ${endDate.day} de ${monthNames[endDate.month.ordinal]} de ${startDate.year}"
            else -> "del ${exportReadableDate(startDate)} al ${exportReadableDate(endDate)}"
        }
    }
}

val AppStrings.exportActivityCodeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Activity code"
        AppLanguage.ITALIAN -> "Codice attività"
        AppLanguage.GERMAN -> "Aktivitätscode"
        AppLanguage.FRENCH -> "Code activité"
        AppLanguage.SPANISH -> "Código actividad"
    }

val AppStrings.exportActivityLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Activity"
        AppLanguage.ITALIAN -> "Attività"
        AppLanguage.GERMAN -> "Aktivität"
        AppLanguage.FRENCH -> "Activité"
        AppLanguage.SPANISH -> "Actividad"
    }

val AppStrings.exportPeriodsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Periods"
        AppLanguage.ITALIAN -> "Periodi"
        AppLanguage.GERMAN -> "Zeiträume"
        AppLanguage.FRENCH -> "Périodes"
        AppLanguage.SPANISH -> "Periodos"
    }

val AppStrings.exportValueLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Value"
        AppLanguage.ITALIAN -> "Valore"
        AppLanguage.GERMAN -> "Wert"
        AppLanguage.FRENCH -> "Valeur"
        AppLanguage.SPANISH -> "Valor"
    }

val AppStrings.exportHoursPerDayLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Hours/day"
        AppLanguage.ITALIAN -> "Ore/giorno"
        AppLanguage.GERMAN -> "Std./Tag"
        AppLanguage.FRENCH -> "Heures/jour"
        AppLanguage.SPANISH -> "Horas/día"
    }

val AppStrings.exportDaysLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Days"
        AppLanguage.ITALIAN -> "Giorni"
        AppLanguage.GERMAN -> "Tage"
        AppLanguage.FRENCH -> "Jours"
        AppLanguage.SPANISH -> "Días"
    }

val AppStrings.exportHoursPerDayCompactLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Hrs/day"
        AppLanguage.ITALIAN -> "Ore/g"
        AppLanguage.GERMAN -> "Std/T"
        AppLanguage.FRENCH -> "H/j"
        AppLanguage.SPANISH -> "H/d"
    }

val AppStrings.exportTotalHoursCompactLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Tot hrs"
        AppLanguage.ITALIAN -> "Ore tot"
        AppLanguage.GERMAN -> "Gesamt"
        AppLanguage.FRENCH -> "Tot h"
        AppLanguage.SPANISH -> "Tot h"
    }

val AppStrings.exportValueLinePrefix: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Value: "
        AppLanguage.ITALIAN -> "Valore: "
        AppLanguage.GERMAN -> "Wert: "
        AppLanguage.FRENCH -> "Valeur : "
        AppLanguage.SPANISH -> "Valor: "
    }

val AppStrings.exportNoActivitiesForSelectedPeriod: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No activities were recorded for the selected period."
        AppLanguage.ITALIAN -> "Nessuna attività registrata per il periodo selezionato."
        AppLanguage.GERMAN -> "Keine Aktivitäten für den ausgewählten Zeitraum erfasst."
        AppLanguage.FRENCH -> "Aucune activité enregistrée pour la période sélectionnée."
        AppLanguage.SPANISH -> "No se registraron actividades para el período seleccionado."
    }

fun AppStrings.exportDocumentTitle(periodLabel: String): String = "$appName - $periodLabel"
