# TLInCompose

`TLInCompose` ist eine Kotlin-Multiplatform-App mit Compose Multiplatform zur Verwaltung eines monatlichen Timesheets auf Android und Desktop.

## 🌍 Verfügbare Sprachen

- 🇬🇧 Englisch (Standard): [README.md](README.md)
- 🇮🇹 Italienisch: [README.it.md](README.it.md)
- 🇫🇷 Französisch: [README.fr.md](README.fr.md)
- 🇪🇸 Spanisch: [README.es.md](README.es.md)

## Überblick

- Monatskalender mit Navigation zum vorherigen/nächsten Monat
- Mehrere Aktivitäten pro Tag
- Export in `CSV`, `XLSX`, `PDF`
- Export für sichtbaren Monat oder benutzerdefinierten Zeitraum

## Funktionale Regeln

- App-Sprache: Italienisch
- Zeitzone: `Europe/Rome`
- Woche beginnt am Montag
- Aktivitätstypen: `PROJECT`, `VACATION`, `PERMIT`

## DMG-Build für Mac OS

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:packageDmg
```

Pfad zur erzeugten DMG-Datei:

`/home/runner/work/ComposeAppForTL/ComposeAppForTL/composeApp/build/compose/binaries/main/dmg/`

## Entwickler

- GitHub: [@misterAnt92TV](https://github.com/misterAnt92TV)
- E-Mail: `simone.formica@emeal.nttdata.com`

## Lizenz

Veröffentlicht unter der **Apache License 2.0**.
