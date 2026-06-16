@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.tlincompose.core

import com.tlincompose.domain.model.ActivityDefinitionFieldError
import com.tlincompose.domain.model.Activity
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

data class AppStrings(
    val language: AppLanguage,
)

fun appStrings(language: AppLanguage): AppStrings = AppStrings(language)

val AppStrings.appName: String
    get() = "TLInCompose"

fun CalendarMonth.displayLabel(language: AppLanguage): String = appStrings(language).monthLabel(monthNumber, year)

fun MonthRange.displayLabel(language: AppLanguage): String =
    if (startMonth == endMonth) {
        startMonth.displayLabel(language)
    } else {
        "${startMonth.displayLabel(language)} - ${endMonth.displayLabel(language)}"
    }

fun EntryType.displayName(language: AppLanguage): String = appStrings(language).entryTypeLabel(this)

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
            "Marzo",
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
            "Marz",
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
            "Fevrier",
            "Mars",
            "Avril",
            "Mai",
            "Juin",
            "Juillet",
            "Aout",
            "Septembre",
            "Octobre",
            "Novembre",
            "Decembre",
        )
        AppLanguage.SPANISH -> listOf(
            "Enero",
            "Febrero",
            "Marzo",
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
        AppLanguage.SPANISH -> listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
    }

val AppStrings.weekdayLongLabelsMondayFirst: List<String>
    get() = when (language) {
        AppLanguage.ENGLISH -> listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        AppLanguage.ITALIAN -> listOf("Lunedi", "Martedi", "Mercoledi", "Giovedi", "Venerdi", "Sabato", "Domenica")
        AppLanguage.GERMAN -> listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag")
        AppLanguage.FRENCH -> listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche")
        AppLanguage.SPANISH -> listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
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
        EntryType.VACATION -> "Conges"
        EntryType.PERMIT -> "Autorisation"
    }
    AppLanguage.SPANISH -> when (type) {
        EntryType.PROJECT -> "Proyecto"
        EntryType.COURSE -> "Cursos"
        EntryType.VACATION -> "Vacaciones"
        EntryType.PERMIT -> "Permiso"
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
        ProjectIconPreset.SCHOOL -> "Etude"
    }
    AppLanguage.SPANISH -> when (preset) {
        ProjectIconPreset.WORK -> "Trabajo"
        ProjectIconPreset.CODE -> "Codigo"
        ProjectIconPreset.PALETTE -> "Diseno"
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
        HolidayKey.CHRISTMAS -> "Christmas"
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
        HolidayKey.EPIPHANY -> "Epiphanie"
        HolidayKey.LIBERATION_DAY -> "Fete de la Liberation"
        HolidayKey.LABOUR_DAY -> "Fete du Travail"
        HolidayKey.REPUBLIC_DAY -> "Fete de la Republique"
        HolidayKey.FERRAGOSTO -> "Ferragosto"
        HolidayKey.ALL_SAINTS -> "Toussaint"
        HolidayKey.IMMACULATE_CONCEPTION -> "Immaculee Conception"
        HolidayKey.CHRISTMAS -> "Noel"
        HolidayKey.SAINT_STEPHENS_DAY -> "Saint-Etienne"
        HolidayKey.EASTER -> "Paques"
        HolidayKey.EASTER_MONDAY -> "Lundi de Paques"
    }
    AppLanguage.SPANISH -> when (key) {
        HolidayKey.NEW_YEAR -> "Ano Nuevo"
        HolidayKey.EPIPHANY -> "Epifania"
        HolidayKey.LIBERATION_DAY -> "Dia de la Liberacion"
        HolidayKey.LABOUR_DAY -> "Dia del Trabajo"
        HolidayKey.REPUBLIC_DAY -> "Dia de la Republica"
        HolidayKey.FERRAGOSTO -> "Ferragosto"
        HolidayKey.ALL_SAINTS -> "Todos los Santos"
        HolidayKey.IMMACULATE_CONCEPTION -> "Inmaculada Concepcion"
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
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Seleziona un'entita EXT valida."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Le ore devono essere maggiori di zero."
    }
    AppLanguage.GERMAN -> when (error) {
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Wahle eine gultige EXT-Entitat aus."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Die Stunden mussen grosser als null sein."
    }
    AppLanguage.FRENCH -> when (error) {
        DailyEntryValidationError.INVALID_EXT_SELECTION -> "Selectionne une entite EXT valide."
        DailyEntryValidationError.NON_POSITIVE_HOURS -> "Les heures doivent etre superieures a zero."
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
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Questo codice EXT e gia usato nel catalogo."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Inserisci un titolo chiaro per l'entita."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "La durata deve essere maggiore di zero."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "L'URL progetto deve iniziare con http:// oppure https://."
    }
    AppLanguage.GERMAN -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Gib einen EXT-Code ein."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Dieser EXT-Code wird im Katalog bereits verwendet."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Gib einen klaren Titel fur die Entitat ein."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "Die Dauer muss grosser als null sein."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "Die Projekt-URL muss mit http:// oder https:// beginnen."
    }
    AppLanguage.FRENCH -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Saisis un code EXT."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Ce code EXT est deja utilise dans le catalogue."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Saisis un titre clair pour l'entite."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "La duree doit etre superieure a zero."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "L'URL du projet doit commencer par http:// ou https://."
    }
    AppLanguage.SPANISH -> when (error) {
        ActivityDefinitionFieldError.BLANK_EXT_CODE -> "Introduce un codigo EXT."
        ActivityDefinitionFieldError.DUPLICATE_EXT_CODE -> "Este codigo EXT ya se usa en el catalogo."
        ActivityDefinitionFieldError.BLANK_TITLE -> "Introduce un titulo claro para la entidad."
        ActivityDefinitionFieldError.NON_POSITIVE_DURATION -> "La duracion debe ser mayor que cero."
        ActivityDefinitionFieldError.INVALID_PROJECT_URL -> "La URL del proyecto debe empezar por http:// o https://."
    }
}

fun AppStrings.languageLabel(option: AppLanguage): String = when (option) {
    AppLanguage.ENGLISH -> "English"
    AppLanguage.ITALIAN -> "Italiano"
    AppLanguage.GERMAN -> "Deutsch"
    AppLanguage.FRENCH -> "Francais"
    AppLanguage.SPANISH -> "Espanol"
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
        AppLanguage.GERMAN -> "Tippe auf einen Tag, um Aktivitaten hinzuzufugen, halte einen Tag langer gedruckt fur einen Bereich, oder ziehe eine sichtbare Aktivitat auf einen anderen Tag, um sie zu kopieren."
        AppLanguage.FRENCH -> "Touchez un jour pour ajouter des activites, maintenez un jour pour remplir une plage, ou faites glisser une activite visible vers un autre jour pour la copier."
        AppLanguage.SPANISH -> "Toca un dia para agregar actividades, manten pulsado un dia para compilar un intervalo, o arrastra una actividad visible a otro dia para copiarla."
    }

val AppStrings.unableToCopyDraggedActivity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to copy the dragged activity."
        AppLanguage.ITALIAN -> "Impossibile copiare l'attività trascinata."
        AppLanguage.GERMAN -> "Die gezogene Aktivitat konnte nicht kopiert werden."
        AppLanguage.FRENCH -> "Impossible de copier l'activite glissee."
        AppLanguage.SPANISH -> "No se puede copiar la actividad arrastrada."
    }

val AppStrings.appTagline: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "A clearer calendar for projects, vacation, and permits, with export for one month or multiple selected months."
        AppLanguage.ITALIAN -> "Un calendario piu chiaro per progetti, ferie e permessi, con export per un mese o per piu mesi selezionati."
        AppLanguage.GERMAN -> "Ein klarerer Kalender fur Projekte, Urlaub und Genehmigungen mit Export fur einen oder mehrere ausgewahlte Monate."
        AppLanguage.FRENCH -> "Un calendrier plus clair pour les projets, les conges et les autorisations, avec export sur un ou plusieurs mois selectionnes."
        AppLanguage.SPANISH -> "Un calendario mas claro para proyectos, vacaciones y permisos, con exportacion de uno o varios meses seleccionados."
    }

val AppStrings.weekStartsMonday: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Week starts on Monday"
        AppLanguage.ITALIAN -> "Settimana da lunedi"
        AppLanguage.GERMAN -> "Woche beginnt am Montag"
        AppLanguage.FRENCH -> "Semaine a partir du lundi"
        AppLanguage.SPANISH -> "La semana empieza el lunes"
    }

fun AppStrings.standardWorkday(hoursText: String): String = when (language) {
    AppLanguage.ENGLISH -> "Standard workday $hoursText h"
    AppLanguage.ITALIAN -> "Giornata standard ${hoursText}h"
    AppLanguage.GERMAN -> "Standardtag $hoursText h"
    AppLanguage.FRENCH -> "Journee standard ${hoursText} h"
    AppLanguage.SPANISH -> "Jornada estandar ${hoursText} h"
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
    AppLanguage.FRENCH -> "Progression jours travailles : ${completedHours}h sur ${targetHours}h"
    AppLanguage.SPANISH -> "Avance dias laborables: ${completedHours}h de ${targetHours}h"
}

fun AppStrings.monthCompletionRemaining(remainingHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "${remainingHours}h still to log on workdays"
    AppLanguage.ITALIAN -> "Restano ${remainingHours}h da segnare sui giorni lavorativi"
    AppLanguage.GERMAN -> "Es fehlen noch ${remainingHours}h an Arbeitstagen"
    AppLanguage.FRENCH -> "Il reste ${remainingHours}h a renseigner sur les jours travailles"
    AppLanguage.SPANISH -> "Quedan ${remainingHours}h por registrar en los dias laborables"
}

fun AppStrings.monthCompletionComplete(standardHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "All workdays are covered up to the standard ${standardHours}h"
    AppLanguage.ITALIAN -> "Tutte le giornate lavorative sono coperte fino alle ${standardHours}h standard"
    AppLanguage.GERMAN -> "Alle Arbeitstage sind bis zum Standard von ${standardHours}h abgedeckt"
    AppLanguage.FRENCH -> "Tous les jours travailles sont couverts jusqu'aux ${standardHours}h standard"
    AppLanguage.SPANISH -> "Todos los dias laborables estan cubiertos hasta las ${standardHours}h estandar"
}

val AppStrings.workdayHoursTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Standard workday"
        AppLanguage.ITALIAN -> "Giornata standard"
        AppLanguage.GERMAN -> "Standardarbeitstag"
        AppLanguage.FRENCH -> "Journee standard"
        AppLanguage.SPANISH -> "Jornada estandar"
    }

val AppStrings.workdayHoursDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Use 30-minute steps to adapt the daily target used in summaries and new defaults."
        AppLanguage.ITALIAN -> "Usa passi da 30 minuti per adattare il target giornaliero usato nei riepiloghi e nei nuovi predefiniti."
        AppLanguage.GERMAN -> "Verwende 30-Minuten-Schritte, um das Tagesziel fur Zusammenfassungen und neue Vorgaben anzupassen."
        AppLanguage.FRENCH -> "Utilise des pas de 30 minutes pour ajuster la cible journaliere des resumes et des nouvelles valeurs par defaut."
        AppLanguage.SPANISH -> "Usa pasos de 30 minutos para adaptar el objetivo diario usado en los resumenes y en los nuevos valores predeterminados."
    }

val AppStrings.decreaseWorkdayHours: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Reduce by 30 minutes"
        AppLanguage.ITALIAN -> "Riduci di 30 minuti"
        AppLanguage.GERMAN -> "Um 30 Minuten reduzieren"
        AppLanguage.FRENCH -> "Reduire de 30 minutes"
        AppLanguage.SPANISH -> "Reducir 30 minutos"
    }

val AppStrings.increaseWorkdayHours: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Increase by 30 minutes"
        AppLanguage.ITALIAN -> "Aumenta di 30 minuti"
        AppLanguage.GERMAN -> "Um 30 Minuten erhohen"
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
        AppLanguage.GERMAN -> "Hoher Kontrast aktiv"
        AppLanguage.FRENCH -> "Contraste eleve actif"
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

val AppStrings.cancelRangeSelection: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Cancel month selection"
        AppLanguage.ITALIAN -> "Annulla selezione mesi"
        AppLanguage.GERMAN -> "Monatsauswahl abbrechen"
        AppLanguage.FRENCH -> "Annuler la selection des mois"
        AppLanguage.SPANISH -> "Cancelar seleccion de meses"
    }

val AppStrings.selectExportRange: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Select export months"
        AppLanguage.ITALIAN -> "Seleziona mesi export"
        AppLanguage.GERMAN -> "Exportmonate auswahlen"
        AppLanguage.FRENCH -> "Selectionner les mois d'export"
        AppLanguage.SPANISH -> "Seleccionar meses de exportacion"
    }

val AppStrings.exportSelectedRange: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export selected months"
        AppLanguage.ITALIAN -> "Esporta mesi selezionati"
        AppLanguage.GERMAN -> "Ausgewahlte Monate exportieren"
        AppLanguage.FRENCH -> "Exporter les mois selectionnes"
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
    AppLanguage.GERMAN -> "Ausgewahlte Monate: $rangeLabel. Wahle jetzt das Exportformat."
    AppLanguage.FRENCH -> "Mois selectionnes : $rangeLabel. Choisis maintenant le format d'export."
    AppLanguage.SPANISH -> "Meses seleccionados: $rangeLabel. Ahora elige el formato de exportacion."
}

val AppStrings.selectFirstMonthInstruction: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "1/2 Select the first month."
        AppLanguage.ITALIAN -> "1/2 Seleziona il primo mese."
        AppLanguage.GERMAN -> "1/2 Wahle den ersten Monat."
        AppLanguage.FRENCH -> "1/2 Selectionne le premier mois."
        AppLanguage.SPANISH -> "1/2 Selecciona el primer mes."
    }

val AppStrings.selectLastMonthInstruction: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "2/2 Select the last month."
        AppLanguage.ITALIAN -> "2/2 Seleziona l'ultimo mese."
        AppLanguage.GERMAN -> "2/2 Wahle den letzten Monat."
        AppLanguage.FRENCH -> "2/2 Selectionne le dernier mois."
        AppLanguage.SPANISH -> "2/2 Selecciona el ultimo mes."
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
        AppLanguage.ITALIAN -> "Il file conterra i mesi selezionati, con un riepilogo piu leggibile e i giorni consecutivi raggruppati."
        AppLanguage.GERMAN -> "Die Datei enthalt die ausgewahlten Monate mit einer klareren Zusammenfassung und gruppierten aufeinanderfolgenden Tagen."
        AppLanguage.FRENCH -> "Le fichier contiendra les mois selectionnes avec un resume plus lisible et les jours consecutifs regroupes."
        AppLanguage.SPANISH -> "El archivo incluira los meses seleccionados, con un resumen mas legible y los dias consecutivos agrupados."
    }

val AppStrings.exportVisibleMonthDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The file will include the visible month, with a clearer summary and grouped consecutive days."
        AppLanguage.ITALIAN -> "Il file conterra il mese visibile, con un riepilogo piu leggibile e i giorni consecutivi raggruppati."
        AppLanguage.GERMAN -> "Die Datei enthalt den sichtbaren Monat mit einer klareren Zusammenfassung und gruppierten aufeinanderfolgenden Tagen."
        AppLanguage.FRENCH -> "Le fichier contiendra le mois visible avec un resume plus lisible et les jours consecutifs regroupes."
        AppLanguage.SPANISH -> "El archivo incluira el mes visible, con un resumen mas legible y los dias consecutivos agrupados."
    }

val AppStrings.exportTypeFilterTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Export filter"
        AppLanguage.ITALIAN -> "Filtro export"
        AppLanguage.GERMAN -> "Exportfilter"
        AppLanguage.FRENCH -> "Filtre d'export"
        AppLanguage.SPANISH -> "Filtro de exportacion"
    }

val AppStrings.exportTypeFilterDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Leave all types active to export everything, or choose only project, vacation, and permit combinations."
        AppLanguage.ITALIAN -> "Lascia tutti i tipi attivi per esportare tutto, oppure scegli solo le combinazioni di progetto, ferie e permesso."
        AppLanguage.GERMAN -> "Lass alle Typen aktiv, um alles zu exportieren, oder wahle nur Kombinationen aus Projekt, Urlaub und Genehmigung."
        AppLanguage.FRENCH -> "Laisse tous les types actifs pour tout exporter, ou choisis seulement les combinaisons de projet, conges et autorisation."
        AppLanguage.SPANISH -> "Deja todos los tipos activos para exportarlo todo, o elige solo las combinaciones de proyecto, vacaciones y permiso."
    }

val AppStrings.exportTypeFilterRequiredMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Select at least one type to enable export."
        AppLanguage.ITALIAN -> "Seleziona almeno un tipo per abilitare l'export."
        AppLanguage.GERMAN -> "Wahle mindestens einen Typ aus, um den Export zu aktivieren."
        AppLanguage.FRENCH -> "Selectionne au moins un type pour activer l'export."
        AppLanguage.SPANISH -> "Selecciona al menos un tipo para habilitar la exportacion."
    }

val AppStrings.settingsTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.ITALIAN -> "Impostazioni"
        AppLanguage.GERMAN -> "Einstellungen"
        AppLanguage.FRENCH -> "Parametres"
        AppLanguage.SPANISH -> "Configuracion"
    }

val AppStrings.settingsDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Adjust readability, contrast, language, and layout without changing timesheet data."
        AppLanguage.ITALIAN -> "Regola leggibilita, contrasto, lingua e densita del calendario senza modificare i dati del timesheet."
        AppLanguage.GERMAN -> "Passe Lesbarkeit, Kontrast, Sprache und Layout an, ohne die Timesheet-Daten zu andern."
        AppLanguage.FRENCH -> "Ajuste la lisibilite, le contraste, la langue et la mise en page sans modifier les donnees du timesheet."
        AppLanguage.SPANISH -> "Ajusta legibilidad, contraste, idioma y distribucion sin modificar los datos del timesheet."
    }

val AppStrings.closeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Close"
        AppLanguage.ITALIAN -> "Chiudi"
        AppLanguage.GERMAN -> "Schliessen"
        AppLanguage.FRENCH -> "Fermer"
        AppLanguage.SPANISH -> "Cerrar"
    }

val AppStrings.openSettingsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Open settings"
        AppLanguage.ITALIAN -> "Apri impostazioni"
        AppLanguage.GERMAN -> "Einstellungen offnen"
        AppLanguage.FRENCH -> "Ouvrir les parametres"
        AppLanguage.SPANISH -> "Abrir configuracion"
    }

val AppStrings.closeSettingsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Close settings"
        AppLanguage.ITALIAN -> "Chiudi impostazioni"
        AppLanguage.GERMAN -> "Einstellungen schliessen"
        AppLanguage.FRENCH -> "Fermer les parametres"
        AppLanguage.SPANISH -> "Cerrar configuracion"
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
        AppLanguage.GERMAN -> "Textgrosse"
        AppLanguage.FRENCH -> "Taille du texte"
        AppLanguage.SPANISH -> "Tamano del texto"
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
        AppLanguage.ITALIAN -> "Scegli la lingua dell'app. L'inglese e la lingua predefinita."
        AppLanguage.GERMAN -> "Wahle die App-Sprache. Englisch ist die Standardsprache."
        AppLanguage.FRENCH -> "Choisis la langue de l'application. L'anglais est la langue par defaut."
        AppLanguage.SPANISH -> "Elige el idioma de la aplicacion. El ingles es el idioma predeterminado."
    }

val AppStrings.pdfExportStyleTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "PDF export style"
        AppLanguage.ITALIAN -> "Stile export PDF"
        AppLanguage.GERMAN -> "PDF-Exportstil"
        AppLanguage.FRENCH -> "Style d'export PDF"
        AppLanguage.SPANISH -> "Estilo de exportacion PDF"
    }

val AppStrings.pdfExportStyleDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose how PDF exports present the summary and activity rows."
        AppLanguage.ITALIAN -> "Scegli come presentare riepilogo e righe attività negli export PDF."
        AppLanguage.GERMAN -> "Wahle, wie Zusammenfassung und Aktivitatszeilen im PDF dargestellt werden."
        AppLanguage.FRENCH -> "Choisis comment presenter le resume et les lignes d'activite dans les exports PDF."
        AppLanguage.SPANISH -> "Elige como presentar el resumen y las filas de actividad en las exportaciones PDF."
    }

fun AppStrings.pdfExportStyleLabel(style: PdfExportStyle): String = when (language) {
    AppLanguage.ENGLISH -> when (style) {
        PdfExportStyle.RETRO -> "Retro"
        PdfExportStyle.SIMPLE_TABLE -> "Simple table"
        PdfExportStyle.COMPACT_LIST -> "Compact list"
        PdfExportStyle.DETAIL_BLOCKS -> "Detail blocks"
    }
    AppLanguage.ITALIAN -> when (style) {
        PdfExportStyle.RETRO -> "Retro"
        PdfExportStyle.SIMPLE_TABLE -> "Tabella semplice"
        PdfExportStyle.COMPACT_LIST -> "Elenco compatto"
        PdfExportStyle.DETAIL_BLOCKS -> "Blocchi dettaglio"
    }
    AppLanguage.GERMAN -> when (style) {
        PdfExportStyle.RETRO -> "Retro"
        PdfExportStyle.SIMPLE_TABLE -> "Einfache Tabelle"
        PdfExportStyle.COMPACT_LIST -> "Kompakte Liste"
        PdfExportStyle.DETAIL_BLOCKS -> "Detailblocke"
    }
    AppLanguage.FRENCH -> when (style) {
        PdfExportStyle.RETRO -> "Retro"
        PdfExportStyle.SIMPLE_TABLE -> "Table simple"
        PdfExportStyle.COMPACT_LIST -> "Liste compacte"
        PdfExportStyle.DETAIL_BLOCKS -> "Blocs detail"
    }
    AppLanguage.SPANISH -> when (style) {
        PdfExportStyle.RETRO -> "Retro"
        PdfExportStyle.SIMPLE_TABLE -> "Tabla simple"
        PdfExportStyle.COMPACT_LIST -> "Lista compacta"
        PdfExportStyle.DETAIL_BLOCKS -> "Bloques detalle"
    }
}

fun AppStrings.pdfExportStyleOptionDescription(style: PdfExportStyle): String = when (language) {
    AppLanguage.ENGLISH -> when (style) {
        PdfExportStyle.RETRO -> "Keeps the current boxed table with pipes and classic monospace separators."
        PdfExportStyle.SIMPLE_TABLE -> "Uses a lighter table layout without pipes, with aligned columns and clean separators."
        PdfExportStyle.COMPACT_LIST -> "Presents each activity as a compact list item with the key values on a few lines."
        PdfExportStyle.DETAIL_BLOCKS -> "Separates each activity into stacked detail blocks for easier reading."
    }
    AppLanguage.ITALIAN -> when (style) {
        PdfExportStyle.RETRO -> "Mantiene la tabella attuale incorniciata con pipe e separatori monospaziati classici."
        PdfExportStyle.SIMPLE_TABLE -> "Usa una tabella piu leggera senza pipe, con colonne allineate e separatori puliti."
        PdfExportStyle.COMPACT_LIST -> "Presenta ogni attività come elemento compatto con i valori chiave su poche righe."
        PdfExportStyle.DETAIL_BLOCKS -> "Separa ogni attività in blocchi di dettaglio impilati, piu facili da leggere."
    }
    AppLanguage.GERMAN -> when (style) {
        PdfExportStyle.RETRO -> "Behalt die aktuelle eingerahmte Tabelle mit Pipes und klassischen Monospace-Trennern bei."
        PdfExportStyle.SIMPLE_TABLE -> "Verwendet eine leichtere Tabelle ohne Pipes mit ausgerichteten Spalten."
        PdfExportStyle.COMPACT_LIST -> "Zeigt jede Aktivitat als kompakten Listeneintrag mit den wichtigsten Werten."
        PdfExportStyle.DETAIL_BLOCKS -> "Trennt jede Aktivitat in gestapelte Detailblocke fur leichteres Lesen."
    }
    AppLanguage.FRENCH -> when (style) {
        PdfExportStyle.RETRO -> "Conserve le tableau encadre actuel avec des pipes et des separateurs monospace."
        PdfExportStyle.SIMPLE_TABLE -> "Utilise un tableau plus leger sans pipes, avec des colonnes alignees."
        PdfExportStyle.COMPACT_LIST -> "Affiche chaque activite comme une ligne compacte avec les valeurs principales."
        PdfExportStyle.DETAIL_BLOCKS -> "Separe chaque activite en blocs de detail empiles plus faciles a lire."
    }
    AppLanguage.SPANISH -> when (style) {
        PdfExportStyle.RETRO -> "Mantiene la tabla actual enmarcada con pipes y separadores monoespaciados clasicos."
        PdfExportStyle.SIMPLE_TABLE -> "Usa una tabla mas ligera sin pipes, con columnas alineadas y separadores limpios."
        PdfExportStyle.COMPACT_LIST -> "Presenta cada actividad como un elemento compacto con los valores clave en pocas lineas."
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
        AppLanguage.FRENCH -> "Televerse un logo partage pour l'afficher dans l'en-tete de l'application et dans les exports PDF ou Excel."
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
        AppLanguage.FRENCH -> "Televerser le logo"
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
        AppLanguage.ITALIAN -> "Il file selezionato non e un logo PNG, JPEG o SVG valido."
        AppLanguage.GERMAN -> "Die ausgewahlte Datei ist kein gultiges PNG-, JPEG- oder SVG-Logo."
        AppLanguage.FRENCH -> "Le fichier selectionne n'est pas un logo PNG, JPEG ou SVG valide."
        AppLanguage.SPANISH -> "El archivo seleccionado no es un logo PNG, JPEG o SVG valido."
    }

fun AppStrings.brandingLogoTooLargeMessage(maxKilobytes: Int): String = when (language) {
    AppLanguage.ENGLISH -> "The selected logo is too large. Stay within ${maxKilobytes} KB after optimization."
    AppLanguage.ITALIAN -> "Il logo selezionato e troppo pesante. Resta entro ${maxKilobytes} KB dopo l'ottimizzazione."
    AppLanguage.GERMAN -> "Das ausgewahlte Logo ist zu gross. Bleibe nach der Optimierung innerhalb von ${maxKilobytes} KB."
    AppLanguage.FRENCH -> "Le logo selectionne est trop volumineux. Reste sous ${maxKilobytes} KB apres l'optimisation."
    AppLanguage.SPANISH -> "El logo seleccionado es demasiado pesado. Mantenlo dentro de ${maxKilobytes} KB tras la optimizacion."
}

fun AppStrings.brandingLogoContentDescription(context: String): String = when (language) {
    AppLanguage.ENGLISH -> "Brand logo for $context"
    AppLanguage.ITALIAN -> "Logo brand per $context"
    AppLanguage.GERMAN -> "Brand-Logo fur $context"
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
        AppLanguage.FRENCH -> "Cette application ne collecte aucune donnee personnelle."
        AppLanguage.SPANISH -> "Esta app no recopila datos personales."
    }

val AppStrings.privacyBody: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No account, analytics, tracking, or cloud upload is required. Data stays on your device unless you explicitly export a file."
        AppLanguage.ITALIAN -> "Non sono richiesti account, analytics, tracciamenti o upload cloud. I dati restano sul dispositivo finche non esporti esplicitamente un file."
        AppLanguage.GERMAN -> "Es sind kein Konto, keine Analysen, kein Tracking und kein Cloud-Upload erforderlich. Die Daten bleiben auf deinem Gerat, bis du bewusst eine Datei exportierst."
        AppLanguage.FRENCH -> "Aucun compte, analytics, suivi ou envoi cloud n'est requis. Les donnees restent sur ton appareil jusqu'a un export explicite."
        AppLanguage.SPANISH -> "No se requiere cuenta, analitica, seguimiento ni carga en la nube. Los datos permanecen en tu dispositivo hasta que exportes un archivo de forma explicita."
    }

val AppStrings.highContrastTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "High contrast"
        AppLanguage.ITALIAN -> "Alto contrasto"
        AppLanguage.GERMAN -> "Hoher Kontrast"
        AppLanguage.FRENCH -> "Contraste eleve"
        AppLanguage.SPANISH -> "Alto contraste"
    }

val AppStrings.highContrastDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Creates a clearer separation between text, buttons, selections, and calendar cells."
        AppLanguage.ITALIAN -> "Rende piu netta la separazione tra testo, pulsanti, selezioni e celle del calendario."
        AppLanguage.GERMAN -> "Sorgt fur klarere Abgrenzung zwischen Text, Schaltflachen, Auswahl und Kalenderzellen."
        AppLanguage.FRENCH -> "Renforce la separation entre le texte, les boutons, les selections et les cellules du calendrier."
        AppLanguage.SPANISH -> "Hace mas clara la separacion entre texto, botones, selecciones y celdas del calendario."
    }

val AppStrings.comfortableLayoutTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Comfortable layout"
        AppLanguage.ITALIAN -> "Layout confortevole"
        AppLanguage.GERMAN -> "Komfortables Layout"
        AppLanguage.FRENCH -> "Mise en page confortable"
        AppLanguage.SPANISH -> "Diseno comodo"
    }

val AppStrings.comfortableLayoutDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Increases spacing, button height, and minimum cell size for a more relaxed reading experience."
        AppLanguage.ITALIAN -> "Aumenta spazi, altezza dei pulsanti e dimensione minima delle celle per una lettura piu rilassata."
        AppLanguage.GERMAN -> "Erhoht Abstande, Button-Hohe und minimale Zellgrosse fur entspannteres Lesen."
        AppLanguage.FRENCH -> "Augmente les espacements, la hauteur des boutons et la taille minimale des cellules pour une lecture plus confortable."
        AppLanguage.SPANISH -> "Aumenta espacios, altura de botones y tamano minimo de celdas para una lectura mas comoda."
    }

val AppStrings.focusModeTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Focus mode"
        AppLanguage.ITALIAN -> "Modalita concentrata"
        AppLanguage.GERMAN -> "Fokusmodus"
        AppLanguage.FRENCH -> "Mode concentration"
        AppLanguage.SPANISH -> "Modo concentracion"
    }

val AppStrings.focusModeDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Reduces secondary copy and hides extra details when you want a cleaner screen."
        AppLanguage.ITALIAN -> "Riduce il testo secondario e mostra meno dettagli superflui quando vuoi una schermata piu pulita."
        AppLanguage.GERMAN -> "Reduziert Sekundartext und blendet uberflussige Details aus fur einen ruhigeren Bildschirm."
        AppLanguage.FRENCH -> "Reduit le texte secondaire et affiche moins de details superflus pour un ecran plus propre."
        AppLanguage.SPANISH -> "Reduce el texto secundario y muestra menos detalles superfluos cuando quieres una pantalla mas limpia."
    }

val AppStrings.activityCatalogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "EXT activity catalog"
        AppLanguage.ITALIAN -> "Catalogo attività EXT"
        AppLanguage.GERMAN -> "EXT-Aktivitatenkatalog"
        AppLanguage.FRENCH -> "Catalogue d'activites EXT"
        AppLanguage.SPANISH -> "Catalogo de actividades EXT"
    }

fun AppStrings.activityCatalogDescription(defaultHours: String): String = when (language) {
    AppLanguage.ENGLISH -> "Manage reusable entities for project, vacation, and permit. The default duration starts at ${defaultHours}h and weekends are usually considered non-working."
    AppLanguage.ITALIAN -> "Gestisci le entita riutilizzabili per progetto, ferie e permesso. La durata standard parte da ${defaultHours}h e il weekend e considerato normalmente non lavorativo."
    AppLanguage.GERMAN -> "Verwalte wiederverwendbare Eintrage fur Projekt, Urlaub und Genehmigung. Die Standarddauer startet bei ${defaultHours}h und Wochenenden gelten normalerweise als arbeitsfrei."
    AppLanguage.FRENCH -> "Gere les entites reutilisables pour projet, conges et autorisation. La duree standard commence a ${defaultHours}h et le week-end est generalement non travaille."
    AppLanguage.SPANISH -> "Gestiona entidades reutilizables para proyecto, vacaciones y permiso. La duracion estandar parte de ${defaultHours}h y el fin de semana suele considerarse no laborable."
}

val AppStrings.newExtEntity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "New EXT entity"
        AppLanguage.ITALIAN -> "Nuova entita EXT"
        AppLanguage.GERMAN -> "Neue EXT-Entitat"
        AppLanguage.FRENCH -> "Nouvelle entite EXT"
        AppLanguage.SPANISH -> "Nueva entidad EXT"
    }

val AppStrings.showActivityCatalog: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Show EXT catalog"
        AppLanguage.ITALIAN -> "Mostra catalogo EXT"
        AppLanguage.GERMAN -> "EXT-Katalog anzeigen"
        AppLanguage.FRENCH -> "Afficher le catalogue EXT"
        AppLanguage.SPANISH -> "Mostrar catalogo EXT"
    }

val AppStrings.hideActivityCatalog: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Hide EXT catalog"
        AppLanguage.ITALIAN -> "Nascondi catalogo EXT"
        AppLanguage.GERMAN -> "EXT-Katalog ausblenden"
        AppLanguage.FRENCH -> "Masquer le catalogue EXT"
        AppLanguage.SPANISH -> "Ocultar catalogo EXT"
    }

val AppStrings.noSavedEntities: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No entities have been saved yet. Create the first EXT code to populate the catalog."
        AppLanguage.ITALIAN -> "Non ci sono ancora entita salvate. Crea il primo codice EXT per popolare il catalogo."
        AppLanguage.GERMAN -> "Es sind noch keine Entitaten gespeichert. Erstelle den ersten EXT-Code, um den Katalog zu popuIieren."
        AppLanguage.FRENCH -> "Aucune entite n'est encore enregistree. Cree le premier code EXT pour alimenter le catalogue."
        AppLanguage.SPANISH -> "Todavia no hay entidades guardadas. Crea el primer codigo EXT para poblar el catalogo."
    }

fun AppStrings.baseDuration(hours: String): String = when (language) {
    AppLanguage.ENGLISH -> "Base duration ${hours}h"
    AppLanguage.ITALIAN -> "Durata base ${hours}h"
    AppLanguage.GERMAN -> "Basisdauer ${hours}h"
    AppLanguage.FRENCH -> "Duree de base ${hours}h"
    AppLanguage.SPANISH -> "Duracion base ${hours}h"
}

fun AppStrings.createdOn(date: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Created ${formatDate(date)}"
    AppLanguage.ITALIAN -> "Creata ${formatDate(date)}"
    AppLanguage.GERMAN -> "Erstellt ${formatDate(date)}"
    AppLanguage.FRENCH -> "Creee ${formatDate(date)}"
    AppLanguage.SPANISH -> "Creada ${formatDate(date)}"
}

fun AppStrings.updatedOn(date: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Updated ${formatDate(date)}"
    AppLanguage.ITALIAN -> "Aggiornata ${formatDate(date)}"
    AppLanguage.GERMAN -> "Aktualisiert ${formatDate(date)}"
    AppLanguage.FRENCH -> "Mise a jour ${formatDate(date)}"
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
        AppLanguage.GERMAN -> "Loschen"
        AppLanguage.FRENCH -> "Supprimer"
        AppLanguage.SPANISH -> "Eliminar"
    }

fun AppStrings.projectIconContentDescription(title: String): String = when (language) {
    AppLanguage.ENGLISH -> "Project icon $title"
    AppLanguage.ITALIAN -> "Icona progetto $title"
    AppLanguage.GERMAN -> "Projekticon $title"
    AppLanguage.FRENCH -> "Icone du projet $title"
    AppLanguage.SPANISH -> "Icono del proyecto $title"
}

fun AppStrings.projectPresetContentDescription(label: String): String = when (language) {
    AppLanguage.ENGLISH -> "Project icon $label"
    AppLanguage.ITALIAN -> "Icona progetto $label"
    AppLanguage.GERMAN -> "Projekticon $label"
    AppLanguage.FRENCH -> "Icone du projet $label"
    AppLanguage.SPANISH -> "Icono del proyecto $label"
}

fun AppStrings.activityTypeIconContentDescription(label: String): String = when (language) {
    AppLanguage.ENGLISH -> "Activity icon $label"
    AppLanguage.ITALIAN -> "Icona attività $label"
    AppLanguage.GERMAN -> "Aktivitatssymbol $label"
    AppLanguage.FRENCH -> "Icone activite $label"
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
    AppLanguage.GERMAN -> "Tagesdetails fur ${formatDate(date)} offnen"
    AppLanguage.FRENCH -> "Ouvrir le detail du jour ${formatDate(date)}"
    AppLanguage.SPANISH -> "Abrir el detalle del dia ${formatDate(date)}"
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
    AppLanguage.SPANISH -> "+$count mas"
}

fun AppStrings.dayDialogTitle(date: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Day ${formatDate(date)}"
    AppLanguage.ITALIAN -> "Giorno ${formatDate(date)}"
    AppLanguage.GERMAN -> "Tag ${formatDate(date)}"
    AppLanguage.FRENCH -> "Jour ${formatDate(date)}"
    AppLanguage.SPANISH -> "Dia ${formatDate(date)}"
}

fun AppStrings.dayRangeDialogTitle(startDate: LocalDate, endDate: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Days ${formatDateRange(startDate, endDate)}"
    AppLanguage.ITALIAN -> "Giorni ${formatDateRange(startDate, endDate)}"
    AppLanguage.GERMAN -> "Tage ${formatDateRange(startDate, endDate)}"
    AppLanguage.FRENCH -> "Jours ${formatDateRange(startDate, endDate)}"
    AppLanguage.SPANISH -> "Dias ${formatDateRange(startDate, endDate)}"
}

val AppStrings.dayDialogDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Enter one or more activities and review the hours before saving."
        AppLanguage.ITALIAN -> "Inserisci una o piu attività e verifica le ore prima del salvataggio."
        AppLanguage.GERMAN -> "Fuge eine oder mehrere Aktivitaten hinzu und prufe die Stunden vor dem Speichern."
        AppLanguage.FRENCH -> "Saisis une ou plusieurs activites et verifie les heures avant l'enregistrement."
        AppLanguage.SPANISH -> "Introduce una o mas actividades y revisa las horas antes de guardar."
    }

fun AppStrings.dayRangeDialogDescription(dayCount: Int): String = when (language) {
    AppLanguage.ENGLISH -> "The same activities will be saved on $dayCount selected days."
    AppLanguage.ITALIAN -> "Le stesse attività verranno salvate su $dayCount giorni selezionati."
    AppLanguage.GERMAN -> "Dieselben Aktivitaten werden fur $dayCount ausgewahlte Tage gespeichert."
    AppLanguage.FRENCH -> "Les memes activites seront enregistrees sur $dayCount jours selectionnes."
    AppLanguage.SPANISH -> "Las mismas actividades se guardaran en $dayCount dias seleccionados."
}

val AppStrings.weekendDayDialogNote: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Saturday and Sunday are usually non-working days. Add activities only when needed."
        AppLanguage.ITALIAN -> "Sabato e domenica sono considerati normalmente non lavorativi: registra attività solo quando necessario."
        AppLanguage.GERMAN -> "Samstag und Sonntag sind normalerweise arbeitsfrei. Erfasse Aktivitaten nur bei Bedarf."
        AppLanguage.FRENCH -> "Le samedi et le dimanche sont generalement non travailles : saisis des activites uniquement si necessaire."
        AppLanguage.SPANISH -> "Sabado y domingo suelen ser no laborables: registra actividades solo cuando sea necesario."
    }

val AppStrings.rangeWeekendDayDialogNote: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The selected range includes Saturday or Sunday. Review the hours before applying the same activities everywhere."
        AppLanguage.ITALIAN -> "L'intervallo selezionato include sabato o domenica. Controlla bene le ore prima di applicare le stesse attività a tutti i giorni."
        AppLanguage.GERMAN -> "Der ausgewahlte Bereich enthalt Samstag oder Sonntag. Prufe die Stunden, bevor dieselben Aktivitaten uberall angewendet werden."
        AppLanguage.FRENCH -> "La plage selectionnee inclut un samedi ou un dimanche. Verifie bien les heures avant d'appliquer les memes activites a tous les jours."
        AppLanguage.SPANISH -> "El rango seleccionado incluye sabado o domingo. Revisa bien las horas antes de aplicar las mismas actividades a todos los dias."
    }

fun AppStrings.activityRowLabel(index: Int): String = when (language) {
    AppLanguage.ENGLISH -> "Activity ${index + 1}"
    AppLanguage.ITALIAN -> "Attività ${index + 1}"
    AppLanguage.GERMAN -> "Aktivitat ${index + 1}"
    AppLanguage.FRENCH -> "Activite ${index + 1}"
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
        AppLanguage.ITALIAN -> "Seleziona entita EXT"
        AppLanguage.GERMAN -> "EXT-Entitat auswahlen"
        AppLanguage.FRENCH -> "Selectionner l'entite EXT"
        AppLanguage.SPANISH -> "Selecciona entidad EXT"
    }

fun AppStrings.noEntityAvailableForType(type: EntryType): String = when (language) {
    AppLanguage.ENGLISH -> "No ${entryTypeLabel(type).lowercase()} entity is available in the catalog. Create one in the dedicated section."
    AppLanguage.ITALIAN -> "Nessuna entita ${entryTypeLabel(type).lowercase()} disponibile nel catalogo. Creane una nella sezione dedicata."
    AppLanguage.GERMAN -> "Keine ${entryTypeLabel(type).lowercase()}-Entitat im Katalog verfugbar. Erstelle eine im entsprechenden Bereich."
    AppLanguage.FRENCH -> "Aucune entite ${entryTypeLabel(type).lowercase()} disponible dans le catalogue. Cree-en une dans la section dediee."
    AppLanguage.SPANISH -> "No hay ninguna entidad ${entryTypeLabel(type).lowercase()} disponible en el catalogo. Crea una en la seccion dedicada."
}

val AppStrings.hoursOrFractionsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Hours or fractions"
        AppLanguage.ITALIAN -> "Ore o frazioni"
        AppLanguage.GERMAN -> "Stunden oder Bruchteile"
        AppLanguage.FRENCH -> "Heures ou fractions"
        AppLanguage.SPANISH -> "Horas o fracciones"
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
        AppLanguage.GERMAN -> "Aktivitat hinzufugen"
        AppLanguage.FRENCH -> "Ajouter une activite"
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
    AppLanguage.GERMAN -> "Fur $dayCount Tage speichern"
    AppLanguage.FRENCH -> "Enregistrer sur $dayCount jours"
    AppLanguage.SPANISH -> "Guardar en $dayCount dias"
}

fun AppStrings.chooseEntityForType(type: EntryType): String = when (language) {
    AppLanguage.ENGLISH -> "Choose ${entryTypeLabel(type).lowercase()} entity"
    AppLanguage.ITALIAN -> "Scegli entita ${entryTypeLabel(type).lowercase()}"
    AppLanguage.GERMAN -> "${entryTypeLabel(type)}-Entitat auswahlen"
    AppLanguage.FRENCH -> "Choisir une entite ${entryTypeLabel(type).lowercase()}"
    AppLanguage.SPANISH -> "Elegir entidad ${entryTypeLabel(type).lowercase()}"
}

val AppStrings.noEntitiesForType: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "No entities are available for this type. Create one in the EXT catalog and try again."
        AppLanguage.ITALIAN -> "Non ci sono entita disponibili per questo tipo. Creane una nel catalogo EXT e poi riprova."
        AppLanguage.GERMAN -> "Keine Entitaten fur diesen Typ verfugbar. Erstelle eine im EXT-Katalog und versuche es erneut."
        AppLanguage.FRENCH -> "Aucune entite disponible pour ce type. Cree-en une dans le catalogue EXT puis reessaie."
        AppLanguage.SPANISH -> "No hay entidades disponibles para este tipo. Crea una en el catalogo EXT y vuelve a intentarlo."
    }

fun AppStrings.editEntityTitle(isEditing: Boolean): String = when (language) {
    AppLanguage.ENGLISH -> if (isEditing) "Edit EXT entity" else "New EXT entity"
    AppLanguage.ITALIAN -> if (isEditing) "Modifica entita EXT" else "Nuova entita EXT"
    AppLanguage.GERMAN -> if (isEditing) "EXT-Entitat bearbeiten" else "Neue EXT-Entitat"
    AppLanguage.FRENCH -> if (isEditing) "Modifier l'entite EXT" else "Nouvelle entite EXT"
    AppLanguage.SPANISH -> if (isEditing) "Editar entidad EXT" else "Nueva entidad EXT"
}

fun AppStrings.uniqueCode(code: String): String = when (language) {
    AppLanguage.ENGLISH -> "Unique code $code"
    AppLanguage.ITALIAN -> "Codice univoco $code"
    AppLanguage.GERMAN -> "Eindeutiger Code $code"
    AppLanguage.FRENCH -> "Code unique $code"
    AppLanguage.SPANISH -> "Codigo unico $code"
}

val AppStrings.extCodeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "EXT code"
        AppLanguage.ITALIAN -> "Codice EXT"
        AppLanguage.GERMAN -> "EXT-Code"
        AppLanguage.FRENCH -> "Code EXT"
        AppLanguage.SPANISH -> "Codigo EXT"
    }

val AppStrings.titleFieldLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Title"
        AppLanguage.ITALIAN -> "Titolo"
        AppLanguage.GERMAN -> "Titel"
        AppLanguage.FRENCH -> "Titre"
        AppLanguage.SPANISH -> "Titulo"
    }

val AppStrings.titlePlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "E.g. Client Alpha or Summer vacation"
        AppLanguage.ITALIAN -> "Es. Cliente Alfa oppure Ferie estive"
        AppLanguage.GERMAN -> "Z. B. Kunde Alpha oder Sommerurlaub"
        AppLanguage.FRENCH -> "Ex. Client Alpha ou conges d'ete"
        AppLanguage.SPANISH -> "Ej. Cliente Alfa o vacaciones de verano"
    }

val AppStrings.descriptionFieldLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Description"
        AppLanguage.ITALIAN -> "Descrizione"
        AppLanguage.GERMAN -> "Beschreibung"
        AppLanguage.FRENCH -> "Description"
        AppLanguage.SPANISH -> "Descripcion"
    }

val AppStrings.descriptionPlaceholder: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Helpful details to recognize the activity"
        AppLanguage.ITALIAN -> "Dettagli utili per capire l'attività"
        AppLanguage.GERMAN -> "Hilfreiche Details, um die Aktivitat zu erkennen"
        AppLanguage.FRENCH -> "Details utiles pour comprendre l'activite"
        AppLanguage.SPANISH -> "Detalles utiles para reconocer la actividad"
    }

val AppStrings.defaultDurationHoursLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Default duration (hours)"
        AppLanguage.ITALIAN -> "Durata predefinita (ore)"
        AppLanguage.GERMAN -> "Standarddauer (Stunden)"
        AppLanguage.FRENCH -> "Duree par defaut (heures)"
        AppLanguage.SPANISH -> "Duracion predeterminada (horas)"
    }

val AppStrings.projectIconTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Project icon"
        AppLanguage.ITALIAN -> "Icona progetto"
        AppLanguage.GERMAN -> "Projekticon"
        AppLanguage.FRENCH -> "Icone du projet"
        AppLanguage.SPANISH -> "Icono del proyecto"
    }

val AppStrings.projectIconDescription: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose a Material preset or upload a PNG, JPG, or WebP file from the device."
        AppLanguage.ITALIAN -> "Scegli un preset Material oppure carica un file PNG, JPG o WebP dal dispositivo."
        AppLanguage.GERMAN -> "Wahle ein Material-Preset oder lade eine PNG-, JPG- oder WebP-Datei vom Gerat hoch."
        AppLanguage.FRENCH -> "Choisis un preset Material ou charge un fichier PNG, JPG ou WebP depuis l'appareil."
        AppLanguage.SPANISH -> "Elige un preset Material o carga un archivo PNG, JPG o WebP desde el dispositivo."
    }

val AppStrings.customIconActive: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Custom icon enabled"
        AppLanguage.ITALIAN -> "Icona personalizzata attiva"
        AppLanguage.GERMAN -> "Benutzerdefiniertes Icon aktiv"
        AppLanguage.FRENCH -> "Icone personnalisee active"
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
        AppLanguage.GERMAN -> "Kein Icon ausgewahlt"
        AppLanguage.FRENCH -> "Aucune icone selectionnee"
        AppLanguage.SPANISH -> "Ningun icono seleccionado"
    }

val AppStrings.customIconHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "The image will be stored together with the project entity."
        AppLanguage.ITALIAN -> "L'immagine verra salvata insieme all'entita progetto."
        AppLanguage.GERMAN -> "Das Bild wird zusammen mit der Projekt-Entitat gespeichert."
        AppLanguage.FRENCH -> "L'image sera enregistree avec l'entite projet."
        AppLanguage.SPANISH -> "La imagen se guardara junto con la entidad del proyecto."
    }

val AppStrings.presetIconHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "You can replace it anytime with another preset or a file."
        AppLanguage.ITALIAN -> "Puoi sostituirla in qualsiasi momento con un altro preset o con un file."
        AppLanguage.GERMAN -> "Du kannst es jederzeit durch ein anderes Preset oder eine Datei ersetzen."
        AppLanguage.FRENCH -> "Tu peux le remplacer a tout moment par un autre preset ou un fichier."
        AppLanguage.SPANISH -> "Puedes reemplazarlo en cualquier momento por otro preset o por un archivo."
    }

val AppStrings.noIconHelp: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Add a visual symbol to recognize the project faster."
        AppLanguage.ITALIAN -> "Aggiungi un simbolo visivo per riconoscere il progetto piu rapidamente."
        AppLanguage.GERMAN -> "Fuge ein visuelles Symbol hinzu, um das Projekt schneller zu erkennen."
        AppLanguage.FRENCH -> "Ajoute un symbole visuel pour reconnaitre plus vite le projet."
        AppLanguage.SPANISH -> "Agrega un simbolo visual para reconocer el proyecto mas rapido."
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
        AppLanguage.FRENCH -> "Retirer l'icone"
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
    AppLanguage.ITALIAN -> if (isEditing) "Salva modifiche" else "Crea entita"
    AppLanguage.GERMAN -> if (isEditing) "Anderungen speichern" else "Entitat erstellen"
    AppLanguage.FRENCH -> if (isEditing) "Enregistrer les modifications" else "Creer l'entite"
    AppLanguage.SPANISH -> if (isEditing) "Guardar cambios" else "Crear entidad"
}

fun AppStrings.deleteExtCode(code: String): String = when (language) {
    AppLanguage.ENGLISH -> "Delete $code"
    AppLanguage.ITALIAN -> "Elimina $code"
    AppLanguage.GERMAN -> "$code loschen"
    AppLanguage.FRENCH -> "Supprimer $code"
    AppLanguage.SPANISH -> "Eliminar $code"
}

fun AppStrings.deleteEntityDescription(title: String): String = when (language) {
    AppLanguage.ENGLISH -> "The entity \"$title\" will be removed from the catalog. Existing calendar entries will keep their stored snapshot."
    AppLanguage.ITALIAN -> "L'entita \"$title\" verra rimossa dal catalogo. Le registrazioni gia salvate nel calendario manterranno comunque il loro snapshot."
    AppLanguage.GERMAN -> "Die Entitat \"$title\" wird aus dem Katalog entfernt. Bereits gespeicherte Kalendereintrage behalten ihren Snapshot."
    AppLanguage.FRENCH -> "L'entite \"$title\" sera retiree du catalogue. Les enregistrements deja sauvegardes conserveront leur instantane."
    AppLanguage.SPANISH -> "La entidad \"$title\" se eliminara del catalogo. Los registros ya guardados en el calendario conservaran su snapshot."
}

fun AppStrings.exportDialogTitle(title: String): String = title

fun AppStrings.dayCellDescription(cellDate: LocalDate): String = when (language) {
    AppLanguage.ENGLISH -> "Day ${formatDate(cellDate)}"
    AppLanguage.ITALIAN -> "Giorno ${formatDate(cellDate)}"
    AppLanguage.GERMAN -> "Tag ${formatDate(cellDate)}"
    AppLanguage.FRENCH -> "Jour ${formatDate(cellDate)}"
    AppLanguage.SPANISH -> "Dia ${formatDate(cellDate)}"
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

fun AppStrings.activityCountPhrase(count: Int): String = when (language) {
    AppLanguage.ENGLISH -> "$count activities"
    AppLanguage.ITALIAN -> "$count attività"
    AppLanguage.GERMAN -> "$count Aktivitaten"
    AppLanguage.FRENCH -> "$count activites"
    AppLanguage.SPANISH -> "$count actividades"
}

val AppStrings.rangeStartAndEndPhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "range start and end"
        AppLanguage.ITALIAN -> "inizio e fine intervallo"
        AppLanguage.GERMAN -> "Start und Ende des Bereichs"
        AppLanguage.FRENCH -> "debut et fin de la plage"
        AppLanguage.SPANISH -> "inicio y fin del rango"
    }

val AppStrings.rangeStartPhrase: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "range start"
        AppLanguage.ITALIAN -> "inizio intervallo"
        AppLanguage.GERMAN -> "Start des Bereichs"
        AppLanguage.FRENCH -> "debut de la plage"
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
        AppLanguage.GERMAN -> "innerhalb des ausgewahlten Bereichs"
        AppLanguage.FRENCH -> "dans la plage selectionnee"
        AppLanguage.SPANISH -> "dentro del rango seleccionado"
    }

val AppStrings.checkDayFieldsBeforeSaving: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Review the highlighted fields before saving."
        AppLanguage.ITALIAN -> "Controlla i campi evidenziati prima di salvare."
        AppLanguage.GERMAN -> "Prufe die markierten Felder vor dem Speichern."
        AppLanguage.FRENCH -> "Verifie les champs mis en evidence avant d'enregistrer."
        AppLanguage.SPANISH -> "Revisa los campos resaltados antes de guardar."
    }

val AppStrings.unableToSaveSelectedDay: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to save the selected day."
        AppLanguage.ITALIAN -> "Impossibile salvare il giorno selezionato."
        AppLanguage.GERMAN -> "Der ausgewahlte Tag konnte nicht gespeichert werden."
        AppLanguage.FRENCH -> "Impossible d'enregistrer le jour selectionne."
        AppLanguage.SPANISH -> "No se pudo guardar el dia seleccionado."
    }

val AppStrings.unableToSaveSelectedDays: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to save the selected days."
        AppLanguage.ITALIAN -> "Impossibile salvare i giorni selezionati."
        AppLanguage.GERMAN -> "Die ausgewahlten Tage konnten nicht gespeichert werden."
        AppLanguage.FRENCH -> "Impossible d'enregistrer les jours selectionnes."
        AppLanguage.SPANISH -> "No se pudieron guardar los dias seleccionados."
    }

fun AppStrings.chooseLighterImage(maxKilobytes: Int): String = when (language) {
    AppLanguage.ENGLISH -> "Choose a lighter image: maximum ${maxKilobytes} KB."
    AppLanguage.ITALIAN -> "Scegli un'immagine piu leggera: massimo ${maxKilobytes} KB."
    AppLanguage.GERMAN -> "Wahle ein leichteres Bild: maximal ${maxKilobytes} KB."
    AppLanguage.FRENCH -> "Choisis une image plus legere : maximum ${maxKilobytes} Ko."
    AppLanguage.SPANISH -> "Elige una imagen mas ligera: maximo ${maxKilobytes} KB."
}

val AppStrings.checkEntityFieldsBeforeSaving: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Review the EXT entity fields before saving."
        AppLanguage.ITALIAN -> "Controlla i campi dell'entita EXT prima di salvare."
        AppLanguage.GERMAN -> "Prufe die Felder der EXT-Entitat vor dem Speichern."
        AppLanguage.FRENCH -> "Verifie les champs de l'entite EXT avant d'enregistrer."
        AppLanguage.SPANISH -> "Revisa los campos de la entidad EXT antes de guardar."
    }

val AppStrings.unableToSaveEntity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to save the EXT entity."
        AppLanguage.ITALIAN -> "Impossibile salvare l'entita EXT."
        AppLanguage.GERMAN -> "Die EXT-Entitat konnte nicht gespeichert werden."
        AppLanguage.FRENCH -> "Impossible d'enregistrer l'entite EXT."
        AppLanguage.SPANISH -> "No se pudo guardar la entidad EXT."
    }

val AppStrings.unableToDeleteEntity: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to delete the selected entity."
        AppLanguage.ITALIAN -> "Impossibile eliminare l'entita selezionata."
        AppLanguage.GERMAN -> "Die ausgewahlte Entitat konnte nicht geloscht werden."
        AppLanguage.FRENCH -> "Impossible de supprimer l'entite selectionnee."
        AppLanguage.SPANISH -> "No se pudo eliminar la entidad seleccionada."
    }

val AppStrings.unableToPrepareExport: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to prepare the requested export."
        AppLanguage.ITALIAN -> "Impossibile preparare l'export richiesto."
        AppLanguage.GERMAN -> "Der angeforderte Export konnte nicht vorbereitet werden."
        AppLanguage.FRENCH -> "Impossible de preparer l'export demande."
        AppLanguage.SPANISH -> "No se pudo preparar la exportacion solicitada."
    }

val AppStrings.exportSaveDialogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Save export"
        AppLanguage.ITALIAN -> "Salva esportazione"
        AppLanguage.GERMAN -> "Export speichern"
        AppLanguage.FRENCH -> "Enregistrer l'export"
        AppLanguage.SPANISH -> "Guardar exportacion"
    }

val AppStrings.saveCancelledMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Save cancelled."
        AppLanguage.ITALIAN -> "Salvataggio annullato."
        AppLanguage.GERMAN -> "Speichern abgebrochen."
        AppLanguage.FRENCH -> "Enregistrement annule."
        AppLanguage.SPANISH -> "Guardado cancelado."
    }

val AppStrings.invalidDestinationMessage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Invalid destination."
        AppLanguage.ITALIAN -> "Destinazione non valida."
        AppLanguage.GERMAN -> "Ungultiges Ziel."
        AppLanguage.FRENCH -> "Destination non valide."
        AppLanguage.SPANISH -> "Destino no valido."
    }

fun AppStrings.fileSavedMessage(fileName: String): String = when (language) {
    AppLanguage.ENGLISH -> "$fileName saved successfully."
    AppLanguage.ITALIAN -> "$fileName salvato correttamente."
    AppLanguage.GERMAN -> "$fileName wurde erfolgreich gespeichert."
    AppLanguage.FRENCH -> "$fileName a ete enregistre avec succes."
    AppLanguage.SPANISH -> "$fileName se guardo correctamente."
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
        AppLanguage.GERMAN -> "Projekticon auswahlen"
        AppLanguage.FRENCH -> "Choisir l'icone du projet"
        AppLanguage.SPANISH -> "Elegir icono del proyecto"
    }

val AppStrings.chooseBrandLogoDialogTitle: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Choose brand logo"
        AppLanguage.ITALIAN -> "Scegli logo brand"
        AppLanguage.GERMAN -> "Brand-Logo auswahlen"
        AppLanguage.FRENCH -> "Choisir le logo de marque"
        AppLanguage.SPANISH -> "Elegir logo de marca"
    }

val AppStrings.imageFilesLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Images"
        AppLanguage.ITALIAN -> "Immagini"
        AppLanguage.GERMAN -> "Bilder"
        AppLanguage.FRENCH -> "Images"
        AppLanguage.SPANISH -> "Imagenes"
    }

val AppStrings.unableToReadSelectedImage: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to read the selected image."
        AppLanguage.ITALIAN -> "Impossibile leggere l'immagine selezionata."
        AppLanguage.GERMAN -> "Das ausgewahlte Bild konnte nicht gelesen werden."
        AppLanguage.FRENCH -> "Impossible de lire l'image selectionnee."
        AppLanguage.SPANISH -> "No se pudo leer la imagen seleccionada."
    }

val AppStrings.unableToOpenDestinationFile: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Unable to open the destination file."
        AppLanguage.ITALIAN -> "Impossibile aprire il file di destinazione."
        AppLanguage.GERMAN -> "Die Zieldatei konnte nicht geoffnet werden."
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
        AppLanguage.FRENCH -> "Periode"
        AppLanguage.SPANISH -> "Periodo"
    }

val AppStrings.exportRecordedDaysLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Recorded days"
        AppLanguage.ITALIAN -> "Giorni registrati"
        AppLanguage.GERMAN -> "Erfasste Tage"
        AppLanguage.FRENCH -> "Jours saisis"
        AppLanguage.SPANISH -> "Dias registrados"
    }

val AppStrings.exportActivitiesLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Activities"
        AppLanguage.ITALIAN -> "Attività"
        AppLanguage.GERMAN -> "Aktivitaten"
        AppLanguage.FRENCH -> "Activites"
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
        AppLanguage.FRENCH -> "Exporte le"
        AppLanguage.SPANISH -> "Exportado el"
    }

fun AppStrings.exportGeneratedAtValue(instant: Instant): String = formatInstant(instant)

val AppStrings.exportActivityCodeLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Activity code"
        AppLanguage.ITALIAN -> "Codice attività"
        AppLanguage.GERMAN -> "Aktivitatscode"
        AppLanguage.FRENCH -> "Code activite"
        AppLanguage.SPANISH -> "Codigo actividad"
    }

val AppStrings.exportActivityLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Activity"
        AppLanguage.ITALIAN -> "Attività"
        AppLanguage.GERMAN -> "Aktivitat"
        AppLanguage.FRENCH -> "Activite"
        AppLanguage.SPANISH -> "Actividad"
    }

val AppStrings.exportPeriodsLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Periods"
        AppLanguage.ITALIAN -> "Periodi"
        AppLanguage.GERMAN -> "Zeitraume"
        AppLanguage.FRENCH -> "Periodes"
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
        AppLanguage.SPANISH -> "Horas/dia"
    }

val AppStrings.exportDaysLabel: String
    get() = when (language) {
        AppLanguage.ENGLISH -> "Days"
        AppLanguage.ITALIAN -> "Giorni"
        AppLanguage.GERMAN -> "Tage"
        AppLanguage.FRENCH -> "Jours"
        AppLanguage.SPANISH -> "Dias"
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
        AppLanguage.GERMAN -> "Keine Aktivitaten fur den ausgewahlten Zeitraum erfasst."
        AppLanguage.FRENCH -> "Aucune activite enregistree pour la periode selectionnee."
        AppLanguage.SPANISH -> "No se registraron actividades para el periodo seleccionado."
    }

fun AppStrings.exportDocumentTitle(periodLabel: String): String = "$appName - $periodLabel"
