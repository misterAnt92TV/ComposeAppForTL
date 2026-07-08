# TLInCompose

`TLInCompose` est une application Kotlin Multiplatform avec Compose Multiplatform pour gérer une feuille de temps mensuelle sur Android et Desktop.

## 🌍 Langues disponibles

- 🇬🇧 Anglais (par défaut) : [README.md](README.md)
- 🇮🇹 Italien : [README.it.md](README.it.md)
- 🇩🇪 Allemand : [README.de.md](README.de.md)
- 🇪🇸 Espagnol : [README.es.md](README.es.md)

## Vue d'ensemble

- Calendrier mensuel avec navigation mois précédent/suivant
- Gestion de plusieurs activités par jour
- Export en `CSV`, `XLSX`, `PDF`
- Export du mois visible ou d'une plage personnalisée

## Règles fonctionnelles

- Langue de l'app : Italien
- Fuseau horaire : `Europe/Rome`
- Semaine commençant le lundi
- Types d'activité : `PROJECT`, `VACATION`, `PERMIT`

## Build DMG pour Mac OS

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:packageDmg
```

Chemin du DMG généré :

`/home/runner/work/ComposeAppForTL/ComposeAppForTL/composeApp/build/compose/binaries/main/dmg/`

## Développeur

- GitHub : [@misterAnt92TV](https://github.com/misterAnt92TV)
- Email : `simone.formica@emeal.nttdata.com`

## Licence

Distribué sous **Apache License 2.0**.
