# Cross-Model AI Workflow

These instructions are intended for Antigravity IDE and other AI coding tools/models. Adapt command syntax to the tool, but preserve the safety rules.

## Source of truth
Use this priority order:
1. Actual source files and repository state.
2. Test/build output from commands actually executed.
3. `PROJECT_STATE.md` and `CHANGELOG.md`.
4. Other project docs.
5. Chat history or model memory.

If sources conflict, inspect the repository and update the docs to reflect verified reality.

## Session startup
- Read the project docs and the relevant agent role file.
- Inspect the project tree and Git status.
- Check current build configuration, SDK versions, and existing tests.
- Summarize the verified state in a few bullets.
- Choose one bounded task with acceptance criteria.

## Implementation loop
1. Plan: state files likely to change and risks.
2. Inspect: read existing implementation and tests.
3. Checkpoint: use Git or backup for risky changes.
4. Implement: make a minimal coherent change.
5. Validate: run the narrowest useful tests/build first.
6. Review: inspect diff, privacy, security, regressions and incomplete code.
7. Record: update state/changelog/change record.
8. Continue only after the current change is stable.

## Model-independent instruction
Do not depend on any proprietary agent format. The Markdown role files under `agents/` are plain-language contracts. In Antigravity, Cursor, Claude Code, Codex, Gemini, or another tool, paste the relevant role file into the agent/task instructions or adapt it to the tool's native rules format. Preserve the full requirements even if a platform requires a different filename or frontmatter.

## When tools are unavailable
If the AI cannot run shell commands, access the repository, inspect diffs, or execute tests:
- Say exactly which checks could not be run.
- Provide commands the user can run.
- Do not claim the result.
- Make only changes that can be reasoned about from available source.
- Update `PROJECT_STATE.md` with verification marked as not run.

## Delegation
Use separate agents only when their tasks can be isolated:
- Architect: plans interfaces and boundaries; does not edit broadly.
- Implementer: changes a bounded feature.
- Tester: independently reviews tests and failure paths.
- Security reviewer: reviews vault, permissions and file safety.
- Documentation/release agent: maintains state, changelog and release notes.

Avoid multiple agents editing the same files concurrently. Assign one owner per file/module and integrate changes sequentially.

## Handoff requirement
Before ending, write a handoff using `templates/SESSION_HANDOFF.md`, update `PROJECT_STATE.md`, and list the exact next action.
