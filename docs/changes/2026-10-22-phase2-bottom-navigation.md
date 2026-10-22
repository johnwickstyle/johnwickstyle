# Feature Task

## Goal
Replace the Phase 1 greeting scaffold with a 5-destination Material 3 bottom navigation shell, media top tabs, and a safe system-camera FAB.

## User story
As a user, I want Image, Video, Audio, Documents, and Folder destinations so that I can move around the gallery once media features exist.

## Acceptance criteria
- [x] Five bottom destinations in order: Image, Video, Audio, Documents, Folder
- [x] Image tabs: Photos | Album | Folder | Private
- [x] Video tabs: Videos | Format | Folder | Private
- [x] Audio tabs: Audios | Genre | Folder | Private
- [x] Documents tabs: Documents | Format | Folder | Private
- [x] Unfinished tab/destination content is an honest empty state, not a fake library
- [x] Camera FAB launches ACTION_IMAGE_CAPTURE when a camera app exists; otherwise a snackbar
- [x] FAB does not overlap the bottom bar or system insets
- [x] NavDestination unit tests for count, order, unique routes
- [x] `:app:testDebugUnitTest` and `:app:assembleDebug` run and observed
- [x] `PROJECT_STATE.md` and `CHANGELOG.md` updated

## Scope
- In scope: navigation, tabs, MainScreen, camera intent launch, strings, tests
- Out of scope: MediaStore queries, runtime permission UI, viewers, vault, playback

## Likely files/modules
- `gradle/libs.versions.toml`, `app/build.gradle.kts`
- `navigation/*`, `ui/main/MainScreen.kt`, `ui/components/*`, destination screens
- `MainActivity.kt`, `strings.xml`, `NavDestinationTest.kt`

## Risks
- Camera intent without FileProvider does not save a full-resolution library file (documented; capture insert is a later phase)

## Rollback checkpoint
- Current Phase 1 tree before these edits

## Test plan
- NavDestinationTest: 5 destinations, order, unique routes
- Gradle `:app:testDebugUnitTest :app:assembleDebug`
