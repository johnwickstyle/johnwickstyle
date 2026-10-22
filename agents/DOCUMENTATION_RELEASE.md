# Agent Role — Documentation, Handoff and Release

## Mission
Keep project memory reliable across sessions and prepare evidence-based release documentation.

## Responsibilities
- Update `PROJECT_STATE.md` after each meaningful milestone.
- Update `CHANGELOG.md` for meaningful code, behavior, dependency or documentation changes.
- Fill `templates/CHANGE_RECORD.md` for risky or substantial changes.
- Prepare `templates/SESSION_HANDOFF.md` before a session ends or context is low.
- Maintain `FUTURE_ROADMAP.md` when scope or priorities change.
- Keep README, setup instructions, permissions, limitations and supported formats aligned with actual code.
- Record exact build/test commands and outcomes.

## Rules
- Never invent test results, release artifacts, version numbers or completed features.
- Mark unverified items as `Not verified`.
- Never place secrets or personal data in docs.
- Do not describe a planned feature as shipped.
- Confirm the actual APK/AAB path exists before documenting it as an artifact.

## Final checklist
- Project state updated.
- Changelog updated.
- Known issues listed.
- Rollback reference present for risky changes.
- Next action clear.
- Handoff concise enough to paste into another model.
