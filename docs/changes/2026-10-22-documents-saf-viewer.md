# Change Record

- Date/time: 2026-10-22T18:05 UTC
- Task / issue: Documents SAF library and read-only PDF/text viewer
- Owner: MonkeyCode-AI
- Branch: main (uncommitted)
- Before checkpoint commit or backup: Phase 6 verified tree
- Risk level: Medium

## Goal
Let the user add documents via SAF and view PDF/text in-app without editing originals. Unsupported formats open externally.

## Scope
- Files to change: document model/repository, Documents screen, viewer, navigation, strings, tests
- Files explicitly out of scope: MediaSessionService, folder tree, vault, document editing

## Changes made
- Added: DocumentItem, DocumentRepository, SafDocumentRepository, DocumentLogic, DocumentsViewModel, DocumentViewerViewModel, DocumentViewerScreen, DocumentLogicTest
- Modified: DocumentsScreen, GalleryNavHost, NavDestination, strings, AndroidManifest, NavDestinationTest
- Removed: none

## Validation
- Command: `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon`
- Observed result: BUILD SUCCESSFUL in 1m 48s; 46 tests passed; APK 64 MB
- Tests not run and why: No emulator/device run in this environment

## Risks / limitations
- Relies on persistable URI permission; some providers may not grant it
- PDF rendering uses PdfRenderer (no password-protected PDFs)
- Text preview truncated at 1 MB
- Format/Folder/Private tabs remain unavailable

## Data and migration impact
- SharedPreferences `saf_documents` stores URI strings only (no file copies)

## Rollback
- Preferred Git revert commit: uncommitted; restore Phase 6 files listed above
- Files to restore: documents package, SafDocumentRepository, navigation, strings
- Warnings before rollback: persisted SAF URIs in SharedPreferences would remain until cleared
