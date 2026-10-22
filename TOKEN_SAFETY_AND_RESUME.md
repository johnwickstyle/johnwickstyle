# Token Safety, Safe Stop, and Resume Protocol

## Purpose
Keep work recoverable when an AI model reaches a context/token limit, a free usage cap, a paid usage cap, a network interruption, an IDE crash, or a session ends unexpectedly.

## Important limitation
An AI instruction file cannot reliably inspect the platform's remaining token balance, guarantee a warning before cutoff, or force the model to stop. Different models and IDEs expose different context and usage information. Therefore, use early checkpoints and durable repository files instead of relying on a last-minute warning.

## Required behavior for every AI coding session
1. At the start, read `README.md`, `PROJECT_BRIEF.md`, `PROJECT_STATE.md`, `CHANGE_CONTROL.md`, and the relevant agent files.
2. Inspect the actual repository status (`git status` if Git is available) before editing.
3. Work in small, testable changes. Prefer one coherent task at a time.
4. After each meaningful milestone, update `PROJECT_STATE.md` and `CHANGELOG.md`.
5. Save files to disk frequently. Do not keep essential project decisions only in chat.
6. Before a potentially long operation, create a checkpoint and state the intended operation.
7. If context appears low, a usage warning appears, or the model starts losing earlier context, stop starting new features. Finish or safely pause the current operation.
8. Run the quickest relevant validation if time/context allows.
9. Update `PROJECT_STATE.md` with exact files changed, tests run, failures, and the next action.
10. Provide a short handoff message that can be pasted into any AI model.
11. Do not make speculative cleanup changes at the end of a session.
12. Do not claim that unexecuted commands or tests succeeded.

## Checkpoint cadence
- Before every multi-file change.
- After each completed feature or coherent fix.
- Before database migrations, dependency upgrades, vault/security changes, file-format changes, or broad refactors.
- When approximately one third of the available working context remains, if the platform provides a reliable estimate.
- Immediately when any usage/context warning appears.
- At the end of every work session, even if no warning appeared.

## Safe-stop procedure
When nearing a limit, use this exact priority:
1. Stop adding features.
2. Save all currently valid edits.
3. If possible, run formatting/compile/tests relevant to the change.
4. If validation fails, record the failure; do not hide it.
5. Update `PROJECT_STATE.md`.
6. Update `CHANGELOG.md` if code or project decisions changed.
7. Record rollback instructions in `CHANGE_CONTROL.md` for risky changes.
8. Record uncommitted files and whether they are safe, incomplete, or need review.
9. Produce a handoff using `templates/SESSION_HANDOFF.md`.
10. End with one clearly stated next action.

If the AI stops without warning, the next session must inspect Git status and the actual files before continuing. Never assume the last response accurately reflects disk state.

## Resume procedure (works across AI models)
Paste this message into the new session:

"Read `README.md`, `PROJECT_BRIEF.md`, `PROJECT_STATE.md`, `TOKEN_SAFETY_AND_RESUME.md`, `CHANGE_CONTROL.md`, `CHANGELOG.md`, `AGENTS.md`, and the relevant agent instructions. Inspect the actual repository and Git status before editing. Treat source files and test output as authoritative over chat history. Summarize the current verified state, identify incomplete or risky changes, and continue from the first safe next action in `PROJECT_STATE.md`. Do not repeat completed work, invent test results, or overwrite user data. Make one coherent change at a time, validate it, and update the state and change records before continuing."

## If no token/usage remains
- Stop asking the exhausted model to continue.
- Wait until the platform's free or paid usage allowance resets, or switch to another available model/provider.
- Start a fresh session and paste the resume procedure above.
- Ensure the documentation files are saved in the repository or backed up outside the AI chat.
- The new model must inspect the current code and status rather than trust chat memory alone.

## Recovery after a crash or abrupt stop
1. Open the project root.
2. Inspect `git status --short` if Git is initialized.
3. Inspect diffs before discarding anything.
4. Read `PROJECT_STATE.md` and the latest changelog entry.
5. Check for incomplete writes, merge conflicts, generated files, and untracked files.
6. Run a build/test only after understanding the current changes.
7. Continue from the documented safe next action.

## Do not do this
- Do not use `git reset --hard`, `git clean -fd`, or broad destructive commands as routine recovery.
- Do not delete untracked files until their purpose is understood.
- Do not restore an entire old project snapshot over newer user work.
- Do not store secrets, passwords, API keys, private user data, or vault keys in these Markdown files.
