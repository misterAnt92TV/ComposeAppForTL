# TLInCompose

`TLInCompose` es una app Kotlin Multiplatform con Compose Multiplatform para gestionar un parte mensual en Android y Desktop.

## 🌍 Idiomas disponibles

- 🇬🇧 Inglés (predeterminado): [README.md](README.md)
- 🇮🇹 Italiano: [README.it.md](README.it.md)
- 🇫🇷 Francés: [README.fr.md](README.fr.md)
- 🇩🇪 Alemán: [README.de.md](README.de.md)

## Resumen

- Calendario mensual con navegación al mes anterior/siguiente
- Gestión de múltiples actividades por día
- Exportación en `CSV`, `XLSX`, `PDF`
- Exportación por mes visible o rango personalizado

## Reglas funcionales

- Idioma de la app: Italiano
- Zona horaria: `Europe/Rome`
- La semana empieza en lunes
- Tipos de actividad: `PROJECT`, `VACATION`, `PERMIT`

## Build DMG para Mac OS

```bash
GRADLE_USER_HOME=$(pwd)/.gradle-local ./gradlew :composeApp:packageDmg
```

Ruta del DMG generado:

`/home/runner/work/ComposeAppForTL/ComposeAppForTL/composeApp/build/compose/binaries/main/dmg/`

## Desarrollador

- GitHub: [@misterAnt92TV](https://github.com/misterAnt92TV)
- Email: `simone.formica@emeal.nttdata.com`

## Licencia

Distribuido bajo **Apache License 2.0**.
