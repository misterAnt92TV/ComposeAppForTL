param([ValidateSet('help','doctor','test','package','all','clean')][string]$Command = 'help')
$ErrorActionPreference = 'Stop'
$Root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $Root
$env:GRADLE_USER_HOME = if ($env:GRADLE_USER_HOME) { $env:GRADLE_USER_HOME } else { Join-Path $Root '.gradle-local' }
$LogDir = Join-Path $Root 'build/reports'
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null
$Log = Join-Path $LogDir ("create-desktop-installer-{0}.log" -f (Get-Date -Format 'yyyy-MM-dd_HH-mm-ss'))
$Started = Get-Date
Start-Transcript -Path $Log | Out-Null
try {
  function Show-Help { @"
Uso: .\scripts\create-desktop-installer.ps1 <comando>

Comandi:
  help      Mostra questo manuale
  doctor    Verifica Windows, JDK 17, Gradle e strumenti di packaging
  test      Esegue :composeApp:allTests
  package   Crea l'MSI Windows
  all       Esegue test e packaging
  clean     Pulisce gli output Gradle
"@ }
  function Gradle([string[]]$Args) { & "$Root\gradlew.bat" '--no-daemon' @Args; if ($LASTEXITCODE -ne 0) { throw "Gradle fallito ($LASTEXITCODE)" } }
  function Configure-Java {
    if ($env:JAVA_HOME) { return }
    $Candidates = @("$env:ProgramFiles\Java", "$env:ProgramFiles\Eclipse Adoptium") | Where-Object { Test-Path $_ }
    $Java17 = Get-ChildItem $Candidates -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -match '(^|[^0-9])17([^0-9]|$)' } | Select-Object -First 1
    if ($Java17) { $env:JAVA_HOME = $Java17.FullName }
  }
  function Doctor {
    Configure-Java
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw 'Java non trovato.' }
    $Version = (& java -version 2>&1 | Select-String 'version').ToString()
    Write-Host "JAVA_HOME: $($env:JAVA_HOME)`nJava: $Version"
    if ($Version -notmatch 'version "17') { throw 'Serve JDK 17; Java 25 non è compatibile con Gradle 8.11.1.' }
    Gradle @('--version')
    Write-Host 'Diagnostica completata con successo.'
  }
  function Package-App { Configure-Java; Gradle @(':composeApp:packageMsi'); $Artifact = Get-ChildItem "$Root\composeApp\build\compose\binaries" -Recurse -Filter '*.msi' | Select-Object -Last 1; if (-not $Artifact) { throw 'MSI non trovato.' }; Write-Host "Installer MSI: $($Artifact.FullName)" }
  switch ($Command) { 'help' { Show-Help }; 'doctor' { Doctor }; 'test' { Configure-Java; Gradle @(':composeApp:allTests') }; 'package' { Package-App }; 'all' { Configure-Java; Gradle @(':composeApp:allTests'); Package-App }; 'clean' { Gradle @('clean') } }
  $Elapsed = ((Get-Date) - $Started).TotalSeconds.ToString('0')
  Write-Host "Risultato: SUCCESSO | durata: ${Elapsed}s | log: $Log"
} catch {
  $Elapsed = ((Get-Date) - $Started).TotalSeconds.ToString('0')
  Write-Error "Risultato: FALLIMENTO | durata: ${Elapsed}s | log: $Log"
  exit 1
} finally { Stop-Transcript | Out-Null }
