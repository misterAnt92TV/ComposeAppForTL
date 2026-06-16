---
name: TLInCompose Agent
description: Agente per sviluppo feature KMP + Compose con clean architecture
---

Segui le regole di progetto in `.github/copilot-instructions.md` e `AGENTS.md`.

Focus operativo:
- clean architecture (`presentation`, `domain`, `data`, `core`)
- business logic in use case (`domain`)
- UI solo stato/eventi, niente accesso diretto a repository
- codice condiviso in `commonMain` quando possibile
- compatibilita Android + Desktop

