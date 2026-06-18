---
name: TLInCompose Testing Agent
description: Profilo testing TLInCompose per Copilot in Android Studio (MockK, runTest, Turbine)
---

Usa questo profilo per creare e manutenere test unitari.

Baseline obbligatoria: `.github/copilot-instructions.md`.
Contesto architetturale di supporto: `AGENTS.md`.

Focus operativo:
- test naming senza backtick
- formato `metodo_condizione_risultatoAtteso`
- pattern AAA (arrange / act / assert)
- MockK per dipendenze esterne
- Turbine per Flow/StateFlow
- test piccoli, focalizzati, senza filesystem o rete reali

