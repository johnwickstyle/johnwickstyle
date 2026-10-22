# Future Roadmap and Update Policy

This roadmap is a planning document, not a claim that these features already exist. Prioritize stability and data safety before expanding scope.

## Release stages

### Stage 0 — Foundation
- Inspect repository and establish project configuration.
- App shell, Material 3 Adaptive theme, navigation and reusable components.
- Dependency injection, preferences, database, typed error model.
- CI/build instructions and baseline tests.

### Stage 1 — Media discovery
- Android 14+ permissions and partial media access.
- MediaStore discovery and SAF-granted locations.
- Image/video/audio/document listing, metadata, thumbnails, empty/loading/error states.
- Incremental updates and large-library performance.

### Stage 2 — Browsing and search
- Required top tabs.
- Sorting, search, filters, selection, favorites, albums/collections.
- Folder browsing and location manager.

### Stage 3 — Viewers and players
- Full-screen image viewer.
- Read-only document viewing with honest format support.
- Media3 video player.
- Audio player, background service, media session and system controls.

### Stage 4 — Safe file operations
- Copy/move/paste/rename/delete/create folder/share/print/export.
- Conflict handling, progress, cancellation, partial failures and verification.
- Undo only where reliably supported.

### Stage 5 — Private Vault
- Threat model and key-management design review.
- Authenticated encryption and Keystore integration.
- Authentication, import/export, lock behavior and secure failure cleanup.
- Security and interruption tests before release.

### Stage 6 — Photo editor
- Non-destructive edits, crop/rotate/flip, adjustments, filters, undo/redo, export copies.
- Memory and performance testing with large images.

### Stage 7 — Settings and polish
- Theme, player settings, security, locations, per-media settings.
- Camera integration, sharing, printing, accessibility, adaptive layouts.

### Stage 8 — Release readiness
- Automated tests, regression tests, dependency/license review.
- Crash and performance review.
- Privacy policy if required, release notes, signed build configuration.
- Verify Android 14 and newer supported versions.

## Candidate future enhancements
Only add these after core features are reliable:
- Duplicate-media detection with safe review (never auto-delete).
- Recently played and recently viewed.
- Smart collections based on metadata.
- Optional backup/export workflow with explicit destination and clear privacy rules.
- More photo-editing tools.
- Additional document viewers if licenses and maintenance permit.
- Optional local network features only with a separate threat model and explicit user choice.
- Optional cloud sync only after defining encryption, conflict resolution, account security, costs and privacy.
- Localization and RTL support.
- Widget support only if private content can be kept protected.
- Performance dashboards for developer diagnostics, with no sensitive data collection.

## Update rules
- Every update must have a stated user benefit, scope, risks and acceptance criteria.
- Avoid unrelated feature additions in a bug-fix release.
- Use semantic versioning where practical: MAJOR.MINOR.PATCH.
- Add a migration plan before changing database schemas or persistent file formats.
- Do not change the Android application ID after publishing unless intentionally creating a separate app.
- Test upgrade paths from the last supported version.
- Maintain a compatibility/dependency note.
- Update `CHANGELOG.md`, `PROJECT_STATE.md`, tests and user-facing documentation with each meaningful change.
- Never modify the vault file format or key scheme without a reviewed migration/recovery plan.
