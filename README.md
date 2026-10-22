# Universal Gallery — Project Documentation Pack

This documentation pack is the project's durable memory for development across AI models, IDE sessions, free/paid token limits, and interruptions.

## Start here
1. Read `PROJECT_BRIEF.md`.
2. Read `AI_WORKFLOW.md`.
3. Read `AGENTS.md` and select the relevant agent instructions from `agents/`.
4. Before changing code, read `CHANGE_CONTROL.md`.
5. Before ending a session or when context/token limits are near, follow `TOKEN_SAFETY_AND_RESUME.md`.
6. Record completed work in `PROJECT_STATE.md` and notable changes in `CHANGELOG.md`.

## Files in this pack
- `PROJECT_BRIEF.md` — project goals, features, technical requirements, and quality bar.
- `FUTURE_ROADMAP.md` — staged feature roadmap and future update policy.
- `PROJECT_STATE.md` — current phase, verified status, next actions, and known issues. Update this at the end of every work session.
- `TOKEN_SAFETY_AND_RESUME.md` — safe-stop protocol, token-limit warning behavior, and resume procedure.
- `CHANGE_CONTROL.md` — rules for making, reviewing, testing, and reverting code changes.
- `CHANGELOG.md` — human-readable record of important changes.
- `AI_WORKFLOW.md` — instructions designed to work with different AI models and coding agents.
- `AGENTS.md` — agent directory and orchestration rules.
- `agents/*.md` — role-specific instructions.
- `templates/` — reusable session handoff and change record templates.

## Important limitation
Markdown files cannot automatically detect a model's remaining tokens, stop a model before a limit, or guarantee that an AI platform will preserve session state. The instructions here require the AI to monitor available context when the platform exposes it, save progress early, and create a durable handoff before stopping. If the platform provides no token meter or warning, use the manual checkpoints described in `TOKEN_SAFETY_AND_RESUME.md`.

## Project naming
The display name is currently a placeholder: `YOUR_APP_NAME`.
The internal project name is `UniversalGallery`.
Set the final Android `applicationId` before release; do not casually change it after publishing.
