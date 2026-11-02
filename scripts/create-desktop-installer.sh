#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"
export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$ROOT_DIR/.gradle-local}"
GRADLE=("$ROOT_DIR/gradlew" --no-daemon)
COMMAND="${1:-help}"
TIMESTAMP="$(date +%Y-%m-%d_%H-%M-%S)"
LOG_DIR="$ROOT_DIR/build/reports"
LOG_FILE="$LOG_DIR/create-desktop-installer-$TIMESTAMP.log"
START_TIME=$SECONDS

mkdir -p "$LOG_DIR"

on_error() {
  local code=$?
  echo "Risultato: FALLIMENTO | durata: $((SECONDS - START_TIME))s | codice: $code | log: $LOG_FILE" >&2
  exit "$code"
}
trap on_error ERR

usage() {
  cat <<'EOF'
Uso: ./scripts/create-desktop-installer.sh <comando>

Comandi:
  help      Mostra questo manuale
  doctor    Verifica macOS, JDK 17, Gradle e strumenti di packaging
  test      Esegue :composeApp:allTests
  package   Crea il DMG macOS
  all       Esegue test e packaging
  clean     Pulisce gli output Gradle

EOF
}

if [[ "${2:-}" == "--no-log" ]]; then
  : # Il log viene comunque mantenuto per avere un output diagnostico coerente.
fi

find_java17() {
  if [[ "$(uname -s)" == "Darwin" ]] && [[ -x /usr/libexec/java_home ]]; then
    /usr/libexec/java_home -v 17 2>/dev/null || true
  fi
}

configure_java() {
  local java17
  java17="$(find_java17)"
  if [[ -n "$java17" ]]; then
    export JAVA_HOME="$java17"
  fi
}

java_major() {
  java -version 2>&1 | sed -nE 's/.*version "([0-9]+).*/\1/p' | head -1
}

doctor() {
  local os major
  os="$(uname -s)"
  echo "Sistema: $os ($(uname -m))"
  [[ "$os" == "Darwin" ]] || { echo "ERRORE: questo entrypoint crea installer macOS."; return 1; }
  configure_java
  command -v java >/dev/null || { echo "ERRORE: Java non trovato."; return 1; }
  major="$(java_major)"
  echo "JAVA_HOME: ${JAVA_HOME:-non impostato}"
  echo "Java: ${major:-sconosciuta}"
  [[ "$major" == "17" ]] || { echo "ERRORE: serve JDK 17; Java 25 non è compatibile con Gradle 8.11.1."; return 1; }
  command -v hdiutil >/dev/null || { echo "ERRORE: hdiutil non trovato."; return 1; }
  run_gradle --version
  echo "Diagnostica completata con successo."
}

run_gradle() { echo ">> ${GRADLE[*]} $*" | tee -a "$LOG_FILE"; "${GRADLE[@]}" "$@" 2>&1 | tee -a "$LOG_FILE"; }

find_installer() {
  find "$ROOT_DIR/composeApp/build/compose/binaries" -type f -name '*.dmg' -print 2>/dev/null | sort | tail -1
}

test_cmd() { configure_java; run_gradle :composeApp:allTests; }
package_cmd() {
  configure_java
  run_gradle :composeApp:packageDmg
  local artifact; artifact="$(find_installer)"
  [[ -n "$artifact" ]] || { echo "ERRORE: DMG non trovato."; return 1; }
  echo "Installer DMG: $artifact"
}
clean_cmd() { run_gradle clean; }

case "$COMMAND" in
  help|-h|--help) usage ;;
  doctor) doctor ;;
  test) test_cmd ;;
  package) package_cmd ;;
  all) test_cmd && package_cmd ;;
  clean) clean_cmd ;;
  *) echo "Comando non riconosciuto: $COMMAND"; usage; exit 2 ;;
esac

echo "Risultato: SUCCESSO | durata: $((SECONDS - START_TIME))s | log: $LOG_FILE"
