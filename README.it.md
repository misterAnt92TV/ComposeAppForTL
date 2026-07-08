# TLInCompose

`TLInCompose` è un'app Kotlin Multiplatform con Compose Multiplatform per la gestione di un timesheet mensile su Android e Desktop.

## 🌍 Lingue disponibili

- 🇬🇧 Inglese (default): [README.md](README.md)
- 🇫🇷 Francese: [README.fr.md](README.fr.md)
- 🇩🇪 Tedesco: [README.de.md](README.de.md)
- 🇪🇸 Spagnolo: [README.es.md](README.es.md)

## Panoramica

- Calendario mensile con navigazione mese precedente/successivo
- Gestione multi-attività per giorno
- Export in `CSV`, `XLSX`, `PDF`
- Supporto export su mese visibile o intervallo personalizzato

## Regole funzionali

- Lingua app: Italiano
- Timezone: `Europe/Rome`
- Settimana da lunedì
- Tipi attività: `PROJECT`, `VACATION`, `PERMIT`

## Build DMG per Mac OS

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:packageDmg
```

Percorso DMG generato:

`/home/runner/work/ComposeAppForTL/ComposeAppForTL/composeApp/build/compose/binaries/main/dmg/`

## Sviluppatore

- GitHub: [@misterAnt92TV](https://github.com/misterAnt92TV)
- Email: `simone.formica@emeal.nttdata.com`

## Licenza

Distribuito con **Apache License 2.0**.
