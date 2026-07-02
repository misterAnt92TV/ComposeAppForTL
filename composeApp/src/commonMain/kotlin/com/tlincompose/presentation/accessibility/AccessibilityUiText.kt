package com.tlincompose.presentation.accessibility

import com.tlincompose.core.AppStrings
import com.tlincompose.domain.model.AppLanguage

fun ThemeModeUiState.label(strings: AppStrings): String = when (this) {
    ThemeModeUiState.SYSTEM -> when (strings.language) {
        AppLanguage.ENGLISH -> "System"
        AppLanguage.ITALIAN -> "Sistema"
        AppLanguage.GERMAN -> "System"
        AppLanguage.FRENCH -> "Systeme"
        AppLanguage.SPANISH -> "Sistema"
    }
    ThemeModeUiState.LIGHT -> when (strings.language) {
        AppLanguage.ENGLISH -> "Light"
        AppLanguage.ITALIAN -> "Chiaro"
        AppLanguage.GERMAN -> "Hell"
        AppLanguage.FRENCH -> "Clair"
        AppLanguage.SPANISH -> "Claro"
    }
    ThemeModeUiState.DARK -> when (strings.language) {
        AppLanguage.ENGLISH -> "Dark"
        AppLanguage.ITALIAN -> "Scuro"
        AppLanguage.GERMAN -> "Dunkel"
        AppLanguage.FRENCH -> "Sombre"
        AppLanguage.SPANISH -> "Oscuro"
    }
}

fun ThemeModeUiState.description(strings: AppStrings): String = when (this) {
    ThemeModeUiState.SYSTEM -> when (strings.language) {
        AppLanguage.ENGLISH -> "Follow the device or operating system theme automatically."
        AppLanguage.ITALIAN -> "Segue automaticamente il tema del dispositivo o del sistema operativo."
        AppLanguage.GERMAN -> "Folgt automatisch dem Theme des Gerats oder Betriebssystems."
        AppLanguage.FRENCH -> "Suit automatiquement le theme de l'appareil ou du systeme."
        AppLanguage.SPANISH -> "Sigue automaticamente el tema del dispositivo o del sistema operativo."
    }
    ThemeModeUiState.LIGHT -> when (strings.language) {
        AppLanguage.ENGLISH -> "Always use the app light theme."
        AppLanguage.ITALIAN -> "Usa sempre il tema chiaro dell'app."
        AppLanguage.GERMAN -> "Verwendet immer das helle App-Theme."
        AppLanguage.FRENCH -> "Utilise toujours le theme clair de l'application."
        AppLanguage.SPANISH -> "Usa siempre el tema claro de la app."
    }
    ThemeModeUiState.DARK -> when (strings.language) {
        AppLanguage.ENGLISH -> "Always use the app dark theme."
        AppLanguage.ITALIAN -> "Usa sempre il tema scuro dell'app."
        AppLanguage.GERMAN -> "Verwendet immer das dunkle App-Theme."
        AppLanguage.FRENCH -> "Utilise toujours le theme sombre de l'application."
        AppLanguage.SPANISH -> "Usa siempre el tema oscuro de la app."
    }
}

fun AccessibilityTextScaleUiState.label(strings: AppStrings): String = when (this) {
    AccessibilityTextScaleUiState.STANDARD -> when (strings.language) {
        AppLanguage.ENGLISH -> "Standard"
        AppLanguage.ITALIAN -> "Normale"
        AppLanguage.GERMAN -> "Standard"
        AppLanguage.FRENCH -> "Standard"
        AppLanguage.SPANISH -> "Normal"
    }
    AccessibilityTextScaleUiState.LARGE -> when (strings.language) {
        AppLanguage.ENGLISH -> "Large"
        AppLanguage.ITALIAN -> "Grande"
        AppLanguage.GERMAN -> "Gross"
        AppLanguage.FRENCH -> "Grand"
        AppLanguage.SPANISH -> "Grande"
    }
    AccessibilityTextScaleUiState.EXTRA_LARGE -> when (strings.language) {
        AppLanguage.ENGLISH -> "Extra large"
        AppLanguage.ITALIAN -> "Molto grande"
        AppLanguage.GERMAN -> "Sehr gross"
        AppLanguage.FRENCH -> "Tres grand"
        AppLanguage.SPANISH -> "Muy grande"
    }
}

fun AccessibilityTextScaleUiState.description(strings: AppStrings): String = when (this) {
    AccessibilityTextScaleUiState.STANDARD -> when (strings.language) {
        AppLanguage.ENGLISH -> "Balanced size for everyday use."
        AppLanguage.ITALIAN -> "Dimensione bilanciata per l'uso quotidiano."
        AppLanguage.GERMAN -> "Ausgewogene Grosse fur den Alltag."
        AppLanguage.FRENCH -> "Taille equilibree pour un usage quotidien."
        AppLanguage.SPANISH -> "Tamano equilibrado para el uso diario."
    }
    AccessibilityTextScaleUiState.LARGE -> when (strings.language) {
        AppLanguage.ENGLISH -> "More readable text without sacrificing too much space."
        AppLanguage.ITALIAN -> "Testi piu leggibili senza sacrificare troppo spazio."
        AppLanguage.GERMAN -> "Besser lesbarer Text ohne zu viel Platz zu verlieren."
        AppLanguage.FRENCH -> "Texte plus lisible sans sacrifier trop d'espace."
        AppLanguage.SPANISH -> "Textos mas legibles sin sacrificar demasiado espacio."
    }
    AccessibilityTextScaleUiState.EXTRA_LARGE -> when (strings.language) {
        AppLanguage.ENGLISH -> "Maximum readability for labels and buttons."
        AppLanguage.ITALIAN -> "Massima leggibilita per label e pulsanti."
        AppLanguage.GERMAN -> "Maximale Lesbarkeit fur Labels und Schaltflachen."
        AppLanguage.FRENCH -> "Lisibilite maximale pour les libelles et boutons."
        AppLanguage.SPANISH -> "Maxima legibilidad para etiquetas y botones."
    }
}
