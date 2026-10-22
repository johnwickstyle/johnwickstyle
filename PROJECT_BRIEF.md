# Project Brief — Universal Gallery

## Purpose
Build a production-oriented Android gallery and media management application for browsing, viewing, playing, organizing, editing, and protecting photos, videos, audio, and documents.

## App Folder
-  android app is in "app" folder

## Current product decisions
- Platform: Android 14 (API 34) and newer.
- Language: Kotlin.
- UI: Jetpack Compose, Material 3, Material 3 Adaptive.
- Architecture: MVVM with unidirectional data flow and modular boundaries.
- Persistence: Room for structured app metadata; DataStore for preferences.
- Media: AndroidX Media3 / ExoPlayer and MediaSessionService.
- Image loading: Coil.
- Storage: MediaStore and Storage Access Framework (SAF), respecting scoped storage.
- Security: encrypted Private Vault, Android Keystore, AndroidX Biometric where available.
- App display name: `YOUR_APP_NAME` (not finalized).
- Internal project name: `UniversalGallery`.
- Offline-first for features that do not inherently require external services.

## Main navigation
Bottom destinations:
1. Image
2. Video
3. Audio
4. Documents
5. Folder

Provide a camera action at the bottom-right of the main gallery experience, without overlapping system navigation or content.

## Media sections and top tabs
- Image: `Photos | Album | Folder | Private`
- Video: `Videos | Format | Folder | Private`
- Audio: `Audios | Genre | Folder | Private`
- Documents: `Documents | Format | Folder | Private`

## Required feature groups
### Images
- Real MediaStore/authorized-location discovery, thumbnails, album/folder browsing, full-screen viewer, zoom, metadata, favorites, sharing, safe file actions.
- Non-destructive photo editing: crop, rotate, flip, filters, common adjustments, undo/redo, export copy by default; advanced editing only when genuinely implemented.

### Video
- Media3-based playback with seeking, speed controls, supported track/subtitle selection, aspect ratios, full screen, resume, PiP where supported, and clear unsupported-format errors.
- Never promise universal codec support.

### Audio
- Library, genre and folder browsing, queue, playlists, favorites, mini/full player, background playback through MediaSessionService, lock-screen and headset controls, audio focus handling.
- Equalizer/effects only when connected to a real supported audio effect implementation.

### Documents
- Read-only document viewing. No editing of document contents.
- Support formats only when a real in-app renderer or compatible external viewer exists. Preserve originals.

### Folder and file operations
- Browse accessible locations; copy, move, paste, rename, delete, create folders, share, print, export, view details, multi-select.
- Respect Android permissions, MediaStore, SAF, scoped storage and content URIs.
- Confirm destructive actions; verify operations before reporting success; preserve source data if an operation fails.

### Private Vault
- Authenticated encrypted storage for photos, videos, audio and documents.
- Use vetted authenticated encryption (for example AES-GCM) and Android Keystore key protection.
- PIN/password/pattern and biometric authentication where supported.
- Do not merely hide or rename files.
- Never remove source data before encrypted import has been verified.
- Do not expose vault contents to ordinary indexing, search, thumbnails, notifications, widgets or public media.
- Document recovery limitations honestly.

### Search and settings
- Search by name and supported metadata; filter by date, type, folder, size and relevant media properties; sort by name/date/size/type/duration.
- Settings: System/Light/Dark theme, appearance, player preferences, file locations, security, per-media options, share/print settings, about/support.
- Hiding/removing a configured location must not delete the actual files.

### Global error handling
- Centralized typed error handling across repositories, ViewModels, services, workers and UI.
- Clear user-facing messages with useful next steps.
- Snackbar/dialog/inline notification/progress indicators according to severity.
- Safe retry, undo where genuinely reversible, cancellation and partial-batch reporting.
- Handle denied/revoked permissions, missing/corrupt/unsupported files, storage full, removable storage disconnect, player/document/editor errors, database errors and vault failures.
- Never expose stack traces, credentials, encryption keys, sensitive paths or private content in user messages/logs.
- Never show success unless the result is verified.

## Engineering quality
- Inspect existing code before restructuring.
- Build and test incrementally.
- No fake data, placeholder actions or empty catch blocks in completed features.
- Use stable, compatible dependencies and review third-party license/security implications.
- Keep UI accessible and responsive across phones, tablets, orientation changes, font scaling and light/dark themes.
- No default tracking, ads or uploads of diagnostics without explicit consent.
- Document unsupported formats, permissions, recovery limitations and unfinished work.
- Do not claim tests/builds passed unless actually run and observed.

## Definition of done
A feature is complete only when:
1. Its real implementation exists.
2. Loading, empty, success, failure, cancellation and permission states are considered.
3. Relevant tests are added and executed where possible.
4. The app builds after the change.
5. Known limitations are documented.
6. `PROJECT_STATE.md` and `CHANGELOG.md` are updated.
