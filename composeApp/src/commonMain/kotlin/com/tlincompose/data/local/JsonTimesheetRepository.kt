package com.tlincompose.data.local

import co.touchlab.kermit.Logger
import com.tlincompose.core.DispatcherProvider
import com.tlincompose.core.LocalDateComparator
import com.tlincompose.data.mapper.toDomain
import com.tlincompose.data.mapper.toEntity
import com.tlincompose.domain.model.ActivityDefinition
import com.tlincompose.domain.model.CalendarMonth
import com.tlincompose.domain.model.DailyEntry
import com.tlincompose.domain.model.DateRange
import com.tlincompose.domain.repository.TimesheetRepository
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

class JsonTimesheetRepository(
    private val storageDriver: StorageDriver,
    private val dispatcherProvider: DispatcherProvider,
    logger: Logger,
    private val fileName: String = "tlincompose-timesheet.json",
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    },
) : TimesheetRepository {
    private val log = logger.withTag("JsonTimesheetRepository")

    override suspend fun loadMonth(month: CalendarMonth): Map<LocalDate, DailyEntry> = withContext(dispatcherProvider.io) {
        val entries = loadAllEntries()
            .filterKeys(month::contains)
            .toSortedMap(LocalDateComparator)
        log.d { "Mese ${month.fileStamp} caricato con ${entries.size} giorni salvati." }
        entries
    }

    override suspend fun loadRange(range: DateRange): Map<LocalDate, DailyEntry> = withContext(dispatcherProvider.io) {
        val entries = loadAllEntries()
            .filterKeys(range::contains)
            .toSortedMap(LocalDateComparator)
        log.d { "Intervallo ${range.startDate} - ${range.endDate} caricato con ${entries.size} giorni salvati." }
        entries
    }

    override suspend fun saveEntry(entry: DailyEntry) = withContext(dispatcherProvider.io) {
        val allEntries = loadAllEntries()
        allEntries[entry.date] = entry
        persistEntries(allEntries.values)
        log.i { "Salvato il giorno ${entry.date} con ${entry.activities.size} attività." }
    }

    override suspend fun deleteEntry(date: LocalDate) = withContext(dispatcherProvider.io) {
        val allEntries = loadAllEntries()
        allEntries.remove(date)
        persistEntries(allEntries.values)
        log.i { "Eliminato il giorno $date dal timesheet." }
    }

    override suspend fun syncActivitiesWithDefinition(
        previousExtCode: String,
        definition: ActivityDefinition,
    ): Int = withContext(dispatcherProvider.io) {
        val allEntries = loadAllEntries()
        var updatedActivities = 0
        val synchronizedEntries = allEntries.mapValues { (_, entry) ->
            val updatedEntryActivities = entry.activities.map { activity ->
                if (activity.extCode != previousExtCode) {
                    activity
                } else {
                    val synchronizedActivity = activity.copy(
                        type = definition.type,
                        extCode = definition.extCode,
                        title = definition.title,
                        description = definition.description,
                        projectUrl = definition.projectUrl,
                    )
                    if (synchronizedActivity != activity) {
                        updatedActivities += 1
                    }
                    synchronizedActivity
                }
            }

            if (updatedEntryActivities == entry.activities) {
                entry
            } else {
                entry.copy(activities = updatedEntryActivities)
            }
        }

        if (updatedActivities > 0) {
            persistEntries(synchronizedEntries.values)
            log.i { "Sincronizzate $updatedActivities attività collegate a $previousExtCode." }
        } else {
            log.d { "Nessuna attività da sincronizzare per $previousExtCode." }
        }

        updatedActivities
    }

    private fun loadAllEntries(): MutableMap<LocalDate, DailyEntry> {
        val raw = storageDriver.read(fileName) ?: return mutableMapOf()
        val decoded = runCatching {
            json.decodeFromString(TimesheetStore.serializer(), raw)
        }.getOrElse {
            log.w(it) { "Impossibile leggere il file $fileName. Uso uno store vuoto." }
            return mutableMapOf()
        }
        return decoded.entries
            .map(DailyEntryEntity::toDomain)
            .associateByTo(mutableMapOf()) { it.date }
    }

    private fun persistEntries(entries: Collection<DailyEntry>) {
        val orderedEntries = entries.sortedWith(compareBy(LocalDateComparator) { it.date })
        val store = TimesheetStore(entries = orderedEntries.map(DailyEntry::toEntity))
        storageDriver.write(fileName, json.encodeToString(TimesheetStore.serializer(), store))
    }
}
