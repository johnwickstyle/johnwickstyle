# Project State — Update at Every Checkpoint

> This file is the durable handoff record for any AI model or human. Replace the template fields with verified facts. Never guess.

## Last updated
- Date/time: 2026-10-22T18:05 UTC
- Updated by: MonkeyCode-AI (this workspace)
- Branch: main
- Commit/hash: uncommitted Phases 1–6 + documents viewer

## Current status
- Current phase: **Documents viewer complete (SAF, read-only PDF/text)**
- Overall status: **Verified working build. Ready for background audio (MediaSessionService) or folders.**
- Last successful build command: `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon`
- Last successful build result: `BUILD SUCCESSFUL in 1m 48s`
- Last successful test command: `:app:testDebugUnitTest` — 46 tests passed
- Device/emulator and Android version tested: Build & unit tests only (no emulator run)

## Repository facts
- Project root: `/workspace`
- App module(s): `:app`
- Package / application ID: `com.example.universalgallery`
- Display name: `YOUR_APP_NAME`
- Kotlin: 2.1.0 / AGP: 8.7.0 / Gradle: 8.9
- Compose BOM: 2024.12.01 / Navigation Compose 2.8.5 / Coil 2.7.0 / Media3 1.5.1
- minSdk: 34 / targetSdk: 35 / compileSdk: 35
- SDK location: `/opt/android-sdk`

## Completed and verified (this workspace)
- [x] Phase 1 scaffold
- [x] Phase 2 bottom navigation and tabs
- [x] Phase 3 Android 14+ permission flow
- [x] Phase 4 MediaStore image discovery grid
- [x] Phase 5 full-screen photo viewer
- [x] Phase 6 MediaStore video/audio libraries + Media3 play/pause/seek/speed
- [x] Documents: SAF picker, persisted read-only access, PDF page viewer, text preview, external open
- [x] 46 unit tests passed
- [x] APK `app/build/outputs/apk/debug/app-debug.apk` (64 MB)

## In progress
- None. Documents viewer is the current verified boundary.

## Known issues / blockers
- Documents Format/Folder/Private remain honest empty states
- In-app document preview is PDF + text/json only; other types open externally
- Audio player is in-app only (no MediaSessionService / lock-screen / background yet)
- Video player has no PiP or subtitle selection
- Camera FAB still does not insert a verified MediaStore item
- Placeholders: applicationId, display name, launcher icons
- No emulator/device run
- Non-fatal: `Unable to strip libandroidx.graphics.path.so`

## Next actions
1. MediaSessionService background audio
2. Or folder browsing (SAF tree)

## Important constraints
- Do not delete or overwrite user data.
- Do not claim unverified success.
- Repository source is authoritative over older handoff docs from another machine.
