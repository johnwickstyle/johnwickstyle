# Feature Task

## Goal
Add Android 14+ MediaStore runtime permission flow with full, partial, denied, and settings fallback states.

## User story
As a user, I want to grant all or selected photo/video/audio access so the gallery can read only what I allow.

## Acceptance criteria
- [x] Images, Videos, and Audio screens check the matching Android 14 permission set
- [x] Do not request until the user taps Allow access
- [x] Partial visual access shows a banner with Add photos and Manage access
- [x] Permanently denied shows Open settings only
- [x] Camera FAB requests CAMERA before capture
- [x] PermissionHelper evaluation is unit-tested
- [x] Build and unit tests observed
- [x] PROJECT_STATE.md and CHANGELOG.md updated

## Scope
- In scope: permission model, UI, wiring, camera permission, tests
- Out of scope: MediaStore grids, viewers, vault

## Likely files/modules
- `permissions/*`, `ui/components/*`, Image/Video/Audio screens, MainScreen, strings, tests

## Risks
- Partial access detection differs by OEM; evaluation uses platform permission flags only

## Rollback checkpoint
- Phase 2 tree

## Test plan
- PermissionHelperTest for full, partial, denied, permanent, not-requested
- `:app:testDebugUnitTest :app:assembleDebug`
