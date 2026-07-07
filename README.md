# TLInCompose

`TLInCompose` is a Kotlin Multiplatform app built with Compose Multiplatform to manage a monthly timesheet on Android and Desktop.

## 🌍 README in other languages

- 🇮🇹 Italiano: [README.it.md](README.it.md)
- 🇫🇷 Français: [README.fr.md](README.fr.md)
- 🇩🇪 Deutsch: [README.de.md](README.de.md)
- 🇪🇸 Español: [README.es.md](README.es.md)

## Overview

TLInCompose provides a fast workflow to:

- track daily activities in a monthly calendar
- navigate previous/next month
- manage multiple activities per day
- export reports in `CSV`, `XLSX`, and `PDF`

## Functional rules

- App language: Italian
- Reference timezone: `Europe/Rome`
- Week starts on Monday
- Supported holidays: Italian national fixed holidays, `Easter`, and `Easter Monday`

Activity types:

- `PROJECT`: requires a free-text description
- `VACATION`: supports partial hours
- `PERMIT`: supports partial hours

## Export

Supported export scope:

- visible month
- custom date range

Default naming:

- `TLInCompose_YYYY-MM.csv`
- `TLInCompose_YYYY-MM.xlsx`
- `TLInCompose_YYYY-MM.pdf`
- `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.csv`
- `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.xlsx`
- `TLInCompose_YYYY-MM-DD_YYYY-MM-DD.pdf`

Exported columns:

- Start date
- End date
- Type
- Value
- Hours/day
- Days
- Total hours

## Tech stack

- Kotlin `2.2.0`
- Compose Multiplatform `1.10.3`
- Android Gradle Plugin `8.9.3`
- Kotlin Coroutines `1.10.2`
- kotlinx-datetime `0.7.1`
- kotlinx-serialization JSON `1.9.0`
- Kermit `2.1.0`
- Koin `4.1.1`
- Java toolchain / JVM target `17`

Supported targets:

- Android (`applicationId`: `com.tlincompose`, `minSdk`: `24`, `targetSdk`: `36`, `compileSdk`: `36`)
- Desktop JVM (`com.tlincompose.desktop.MainKt`, packaging: `DMG`, `MSI`, `DEB`)

## Architecture

The project follows an incremental clean architecture:

- `com.tlincompose.presentation`: composables, theme, UI state holders, dialogs, UI wiring
- `com.tlincompose.presentation.calendar`: presentation state and domain-to-UI mapping
- `com.tlincompose.domain.model`: pure domain models
- `com.tlincompose.domain.repository`: repository and export contracts
- `com.tlincompose.domain.usecase`: focused use cases for calendar, validation, load/save, export
- `com.tlincompose.data.local`: storage driver, serializable DTOs, JSON repository
- `com.tlincompose.data.mapper`: explicit entity-to-domain mappers
- `com.tlincompose.data.export`: export grouping, CSV/PDF writers, platform-specific XLSX encoder
- `com.tlincompose.core`: shared date/format/dispatcher utilities

## Build and run

Prerequisites:

- JDK `17`
- Android SDK
- Android device or emulator (for Android run/install)

Recommended local Gradle cache:

```bash
export GRADLE_USER_HOME=$(pwd)/.gradle-local
```

Full build:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew build
```

Useful tasks:

- `:composeApp:build`
- `:composeApp:test`
- `:composeApp:allTests`
- `:composeApp:lint`

Run Desktop:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:run
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:desktopRun
```

Android build/install:

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:assembleDebug
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:installDebug
adb shell am start -n com.tlincompose/.MainActivity
```

## License

This project is licensed under the **Apache License 2.0**.

Full license text: [`LICENSE`](LICENSE) or <http://www.apache.org/licenses/LICENSE-2.0>.
