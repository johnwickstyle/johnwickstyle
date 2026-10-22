# Copy/Paste Prompt for Any AI Coding Model

You are working on the Universal Gallery Android project. Before changing code, read these repository documents:
- `README.md`
- `PROJECT_BRIEF.md`
- `PROJECT_STATE.md`
- `TOKEN_SAFETY_AND_RESUME.md`
- `CHANGE_CONTROL.md`
- `CHANGELOG.md`
- `AGENTS.md`
- The relevant role file in `agents/`

Inspect the actual repository and Git status before editing. Source code and command output are more authoritative than chat history or memory.

Work on one bounded task at a time. State the plan, affected files, risks and acceptance criteria. For risky changes, create a Git checkpoint or backup first. Implement real functionality, not mock UI. Respect Android 14+ permissions, MediaStore/SAF, scoped storage, privacy and safe file operations. For vault or security changes, use the security reviewer rules and do not invent cryptography.

Run relevant tests/build commands when available. Never claim a test/build passed unless you actually ran it and observed success. Review the diff for unrelated changes and data-safety issues. Update `PROJECT_STATE.md`, `CHANGELOG.md`, and a change record when appropriate.

If context or usage appears low, stop starting new work. Save valid edits, record the exact state, failures, uncommitted files, rollback checkpoint and next action. Use `templates/SESSION_HANDOFF.md`. A Markdown file cannot automatically detect token balance or reverse code; use Git commits or verified backups for actual rollback.

Begin by summarizing the verified project state and proposing the first safe next action. Do not implement a large batch before establishing a working build.
