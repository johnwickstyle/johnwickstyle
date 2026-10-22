# Code Change Control, Review, and Rollback

## Core rule
Markdown records explain what changed and how to revert it; they do not themselves reverse code. Use Git commits/branches or explicit backups as the actual rollback mechanism. Never rely on a prose description alone to reconstruct previous source code.

## Before changing code
1. Inspect `git status --short` and current diffs.
2. Read relevant source files and tests.
3. Identify expected behavior, affected modules, data migrations and security implications.
4. Record the plan in a task note or `templates/CHANGE_RECORD.md`.
5. For risky changes, create a Git branch or checkpoint commit before editing.
6. Do not mix unrelated features into one change.

## Change implementation
- Make the smallest coherent change.
- Preserve existing behavior unless the task explicitly changes it.
- Avoid broad formatting or unrelated refactors.
- Keep secrets and user data out of logs and documentation.
- Add/update tests alongside the code change.
- For file operations, verify source/destination behavior and failure cleanup.
- For vault/security work, require a threat-model-aware review and failure-path tests.
- For database changes, provide migrations and test upgrades from the previous schema.
- For dependency changes, record why the dependency is needed and review compatibility/license.

## Before marking a change complete
- Run formatting or static analysis if configured.
- Run relevant unit tests.
- Run build/compile checks.
- Run integration/UI tests when practical.
- Review `git diff --check` and the full diff.
- Confirm no unrelated files or secrets were added.
- Confirm actual command results before recording them.
- Update `PROJECT_STATE.md` and `CHANGELOG.md`.

## Rollback strategy
### Preferred: Git
- Create a branch for risky work, e.g. `feature/vault-import`.
- Commit known-good checkpoints with descriptive messages.
- To revert a committed change, prefer `git revert <commit>` when preserving history matters.
- To restore a single file, inspect it first and use a targeted restore only when sure it will not discard later work.
- Avoid destructive reset/clean commands unless the user explicitly approves and a verified backup exists.

### Without Git
- Before editing, create a timestamped copy of the affected files or a project archive outside the source directory.
- Record the exact backup location and the exact files changed.
- Do not overwrite the only copy.
- Restore only affected files from the checkpoint after comparing versions.

## Required change record
For every meaningful or risky change, record:
- Date/time.
- Goal and reason.
- Files added/modified/deleted.
- Summary of behavior changes.
- Tests/commands actually run and results.
- Known risks or unresolved failures.
- Rollback method and exact commit/backup identifier.
- Data migration or vault-format implications.

Use `templates/CHANGE_RECORD.md`.

## Special high-risk changes
Require explicit review before merging:
- Vault encryption/key management.
- Authentication and biometric flows.
- File deletion/move semantics.
- Database schema migrations.
- Media scanning or permission changes.
- Android manifest exported components and deep links.
- Dependency upgrades affecting storage, playback, encryption, or document rendering.

## AI must never
- Claim a Markdown file can reverse code by itself.
- Revert unreviewed user changes.
- delete untracked files without inspecting them.
- rewrite unrelated modules during a small fix.
- declare a task complete when build/test evidence is absent.
