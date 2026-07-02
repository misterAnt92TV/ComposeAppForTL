---
name: TLInCompose Agent
description: Profilo sviluppo TLInCompose per Copilot in Android Studio (KMP + Compose)
---

Usa questo profilo per sviluppo e refactor applicativo.

Baseline obbligatoria: `.github/copilot-instructions.md`.
Contesto architetturale di supporto: `AGENTS.md`.

Focus operativo:
- clean architecture (`presentation`, `domain`, `data`, `core`)
- business logic in use case (`domain`)
- UI solo stato/eventi, niente accesso diretto a repository
- preferenza `commonMain`, adapter in source set platform-specific
- compatibilità Android + Desktop

