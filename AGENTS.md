# Agent Index and Orchestration Rules

This file defines role-based agents as portable Markdown instructions. They are not automatically executable in every IDE; load or paste the selected file into the coding tool's agent/task instructions. If the platform supports native agent files, adapt these roles to its supported format.

## Available roles
- `agents/PROJECT_ARCHITECT.md` — architecture, task decomposition and interfaces.
- `agents/ANDROID_IMPLEMENTER.md` — bounded Kotlin/Compose implementation.
- `agents/QA_TESTER.md` — independent test and regression review.
- `agents/SECURITY_REVIEWER.md` — vault, permissions, file operations and privacy review.
- `agents/DOCUMENTATION_RELEASE.md` — state, changelog, handoff and release documentation.

## Orchestration workflow
1. Architect defines one bounded task, acceptance criteria, affected files and risks.
2. Implementer inspects files and implements only the assigned task.
3. Tester reviews the diff independently and runs relevant tests when tools allow.
4. Security reviewer must review high-risk storage, vault, authentication, permission, deletion, migration or manifest changes.
5. Documentation/release role updates `PROJECT_STATE.md`, `CHANGELOG.md`, and any relevant docs.
6. Main agent reconciles feedback, fixes issues, validates, and records the final state.

## Shared rules for all agents
- Read `PROJECT_BRIEF.md`, `CHANGE_CONTROL.md`, and `TOKEN_SAFETY_AND_RESUME.md`.
- Inspect actual repository state before editing.
- Do not fabricate build/test results.
- Do not overwrite or delete user data.
- Keep changes scoped and reviewable.
- Record files changed and rollback method.
- Do not store credentials, keys, secrets, or sensitive user data in documentation.
- Do not report completion until acceptance criteria are met.
- When context is low, invoke the safe-stop procedure before beginning another task.

## File ownership
Assign a single writer for each file during parallel work. Reviewers should report findings instead of editing the same file concurrently unless the orchestrator explicitly reassigns ownership.
