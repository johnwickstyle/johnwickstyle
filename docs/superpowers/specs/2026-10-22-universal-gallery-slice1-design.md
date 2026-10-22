# Universal Gallery — Slice 1 Design

Date: 2026-10-22
Status: Approved for implementation planning
Product: Universal Gallery (internal name). Display name: `YOUR_APP_NAME`.

## 1. Problem and slice boundary

Build the first shippable vertical slice of a production-oriented Android gallery: a real app shell plus Image > Photos browsing from MediaStore, a full-screen zoom viewer, and a system-camera capture action.

This is the first sub-project of a larger product. Later slices add Album/Folder/Private, editing, Video, Audio, Documents, folder file operations, Private Vault, and Search/Settings. Slice 1 must not fake those features.

### In scope

- Android 14+ (min SDK 34) Kotlin app in `app/`, Jetpack Compose, Material 3.
- Gradle Kotlin DSL multi-module layout: `:app` + `:core:common` + `:core:model` + `:core:ui` + `:core:data`.
- MVVM with unidirectional data flow and Hilt.
- Bottom destinations Image, Video, Audio, Documents, Folder.
- Image top tabs Photos | Album | Folder | Private, with only Photos implemented.
- Real MediaStore image discovery, Coil thumbnails, date-grouped grid.
- Full-screen image viewer: pinch-zoom, pan, swipe between photos, close, filename and date.
- Permission model supporting all-photos and selected-photos access.
- Camera FAB launching the system camera via `ACTION_IMAGE_CAPTURE`, then verified MediaStore insert and Photos refresh.
- Typed error handling, loading/empty/success/failure/permission states.
- DataStore theme: System / Light / Dark.
- Room database with a favorites table schema only (no favorites UI).
- Unit tests for error mapping, MediaStore mapping, and PhotosViewModel states.
- `PROJECT_STATE.md`, `CHANGELOG.md`, and documented limitations.

### Out of scope

- Image Album, Folder, and Private tab behavior beyond an honest empty state.
- Photo editing (crop, rotate, filters, undo/redo, export).
- Favorites, share, print, details sheet, multi-select, rename, delete, copy, move.
- Video playback, Audio playback, document viewing, folder file operations.
- Private Vault, encryption, PIN/biometric.
- Search, filters, sort UI, location hiding.
- In-app camera, video capture, PiP, playlists, equalizer.
- Instrumented / on-device tests (not required for this slice).
- Tracking, ads, diagnostic uploads.

## 2. Recommended build order (product context)

1. Slice 1 (this spec): foundation + Image Photos + viewer + system camera.
2. Image extras: Album, Folder, favorites, share, details, non-destructive editing.
3. Video: Media3 playback, formats, folder, honest unsupported-codec errors.
4. Audio: library, queue, MediaSessionService background playback.
5. Documents: read-only viewers for formats that have a real renderer.
6. Folder operations: copy/move/paste/rename/delete/create with verified results.
7. Private Vault: authenticated encryption, Keystore, biometric.
8. Search and Settings remaining options.

Later slices get their own spec → plan → implementation cycle.

## 3. Architecture

### 3.1 Gradle and SDK

- Gradle Kotlin DSL, version catalog in `gradle/libs.versions.toml`.
- Android Gradle Plugin 8.7.x, Kotlin 2.0.x with Compose compiler plugin.
- `compileSdk` 35, `targetSdk` 35, `minSdk` 34.
- Java 17 bytecode.
- Application id: `com.universalgallery.app`.
- Namespace per module: `com.universalgallery.app`, `com.universalgallery.core.common`, `com.universalgallery.core.model`, `com.universalgallery.core.ui`, `com.universalgallery.core.data`.
- Hilt, KSP, Compose BOM, Coil Compose, AndroidX Navigation Compose, DataStore Preferences, Room (KSP), Lifecycle, Activity Compose.

### 3.2 Modules

```
:app
  shell, navigation, Image Photos UI and ViewModel, camera capture flow, Hilt Application

:core:common
  GalleryResult, GalleryError, CoroutineDispatchers, extensions used by all modules

:core:model
  MediaItem, MediaType, PermissionState, ThemeMode, user-facing error codes (no Android types)

:core:ui
  Material 3 theme, gallery scaffold, empty/error/permission/loading composables, snackbar host

:core:data
  Room, DataStore, MediaStore photo data source, permission repository, theme repository
```

`:app` depends on all core modules. Core modules do not depend on `:app`. `:core:data` depends on `:core:model` and `:core:common`. `:core:ui` depends on `:core:model` and `:core:common`.

Image Photos stays in `:app` until a second real media feature exists. Empty destinations stay in `:app` as thin screens so later feature modules can replace them without a shell rewrite.

### 3.3 Layering and UDF

```
Composable (collects StateFlow)
    -> ViewModel (maps user intents to use of repositories)
        -> Repository (Permission, Media, Theme)
            -> DataSource (MediaStore, DataStore, Room)
```

UI never talks to MediaStore, ContentResolver, or DataStore directly. ViewModels expose an immutable UI state and a small set of user events. Side effects (snackbar, open settings, launch camera, launch picker) are one-shot `SharedFlow` / `Channel` events, not duplicated in state.

### 3.4 Dependency injection

Hilt `SingletonComponent` bindings in `:core:data` for repositories and Room/DataStore. `:app` provides `@HiltAndroidApp`, `MainActivity`, and navigation. Coroutine dispatchers are injectable (`io`, `default`, `main`).

## 4. Navigation and UI

### 4.1 Shell

- Single `MainActivity` with edge-to-edge drawing.
- `GalleryScaffold`: top app bar (destination title), content, `NavigationBar`, camera `FloatingActionButton`.
- Bottom destinations, in order: Image, Video, Audio, Documents, Folder.
- Camera FAB sits at the bottom-end of the scaffold, with padding equal to navigation bar height plus content padding so it never overlaps system navigation or the photo grid.
- FAB is always visible on the main shell (all five bottom destinations). It always launches the same still-photo capture flow; the captured file belongs to the photo library, not to Video/Audio/Documents.

### 4.2 Image tabs

Top `PrimaryTabRow` / scrollable tabs: Photos | Album | Folder | Private.

- Photos: implemented library.
- Album, Folder, Private: honest empty state. Title plus body: "This section is not available yet. It will ship in a later update." No placeholder grids, no fake albums.

### 4.3 Other bottom destinations

Video, Audio, Documents, and Folder each show the same honest empty pattern: destination name and "This section is not available yet. It will ship in a later update." They must not show fake media.

### 4.4 Photos grid

- Adaptive grid (min cell ~118.dp) of square thumbnails loaded by Coil from `content://` URIs.
- Grouped by capture/modified date (Today, Yesterday, then `MMMM d, yyyy`).
- Content descriptions use display name.
- States:
  - Loading: centered circular progress.
  - Empty (permission granted, zero images): "No photos yet" plus hint to capture with the camera button.
  - Success: date-grouped grid.
  - Partial access: grid of granted photos plus a persistent banner: "Showing selected photos" with actions "Add photos" (system Photo Picker) and "Manage access" (permission settings / system selected-media dialog).
  - Denied: permission screen with "Allow access" and, if permanently denied, "Open settings".
  - Error: inline error with retry.

### 4.5 Full-screen viewer

- Horizontal pager over the current Photos list (not a separate query).
- Pinch-zoom and pan; double-tap toggles zoom.
- Swipe between photos at zoom scale 1.
- Top bar: close, display name.
- Bottom bar: date/time.
- Immersive: tap toggles chrome; system bars contrast with scrim.
- If the current item becomes unreadable (deleted, permission revoked, corrupt), show an inline error and keep pager position for neighbors.

### 4.6 Theme

Material 3 dynamic color when available, otherwise a static seed scheme. Theme mode from DataStore: System (default), Light, Dark. Slice 1 exposes theme via a simple overflow action on the Image top bar ("Appearance") that writes DataStore. Full Settings screen is later.

### 4.7 Accessibility and responsive behavior

- Touch targets at least 48.dp for nav, FAB, tabs, permission actions.
- TalkBack labels on thumbnails, FAB ("Take photo"), tabs, and permission buttons.
- Layout survives orientation change, font scale, and large-screen width (grid columns grow; viewer remains a pager).
- Light and dark both implemented; no hard-coded contrast failures for primary text on surface.

Display name in the launcher and top bar: `YOUR_APP_NAME`.

## 5. Permissions

### 5.1 Declared permissions

- `READ_MEDIA_IMAGES`
- `READ_MEDIA_VISUAL_USER_SELECTED`
- `CAMERA` (used only when the user taps the camera FAB)

No `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO`, `WRITE_EXTERNAL_STORAGE`, or `MANAGE_EXTERNAL_STORAGE` in this slice.

### 5.2 States

`PermissionState`:

- `Granted` — `READ_MEDIA_IMAGES` is granted; query MediaStore as a full image library (whatever the OS exposes to the app).
- `Partial` — only selected photos (`READ_MEDIA_VISUAL_USER_SELECTED` without `READ_MEDIA_IMAGES`, including `ACCESS_MEDIA_LIMITED`).
- `Denied` — can ask again.
- `PermanentlyDenied` — user must open system settings.
- `Unknown` — not yet checked.

Partial access is a first-class success path, not treated as failure.

### 5.3 Flows

1. On first Photos open, if not granted and not partial, show the permission screen (do not request before the user taps Allow).
2. Allow requests `READ_MEDIA_IMAGES`. On Android 14 the system may offer selected photos; the app must accept that result as `Partial`.
3. "Add photos" launches `ActivityResultContracts.PickMultipleVisualMedia`. After a result, call `takePersistableUriPermission` when the platform offers it, then re-query MediaStore. Do not add picker-only URIs to the grid unless MediaStore returns them.
4. "Manage access" opens `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` for this app so the user can change photo access.
5. Permanently denied: only "Open settings".
6. Camera FAB: if `CAMERA` is missing, request it; if denied, snackbar with Open settings; do not capture.

Revoked permission while the grid is showing: ContentObserver / lifecycle `ON_RESUME` re-checks and switches to the matching state. Never keep showing URIs the app can no longer read.

## 6. Data

### 6.1 MediaStore photos

Query `MediaStore.Images.Media.EXTERNAL_CONTENT_URI` with projection:

- `_ID`, `DISPLAY_NAME`, `DATE_TAKEN`, `DATE_MODIFIED`, `SIZE`, `WIDTH`, `HEIGHT`, `MIME_TYPE`, `RELATIVE_PATH`, `BUCKET_ID`, `BUCKET_DISPLAY_NAME`

Sort: `DATE_TAKEN DESC`, then `DATE_MODIFIED DESC`, then `_ID DESC`.

Map each row to `MediaItem` (id, contentUri, displayName, mimeType, sizeBytes, width, height, takenAtEpochMs, modifiedAtEpochMs, bucketId, bucketName, mediaType = Image). Skip rows with unreadable URI or non-image mime.

Register a `ContentObserver` on the images URI and refresh the repository stream. Also refresh on `ON_RESUME`.

Do not scan app-private vault directories, do not index hidden files, do not query video/audio in this slice.

### 6.2 Photo Picker add-to-partial

When the user adds photos via `PickMultipleVisualMedia`, persist access where the platform grants persistable permission, then re-query MediaStore. Newly granted items must appear only after the query returns them. Do not insert fake `MediaItem`s from picker URIs that are not in MediaStore.

### 6.3 Camera capture

1. User taps FAB.
2. Ensure `CAMERA` permission.
3. Create a temp file in `cacheDir` via `FileProvider` (`${applicationId}.fileprovider`).
4. Launch `ACTION_IMAGE_CAPTURE` with `EXTRA_OUTPUT`.
5. On `RESULT_OK`, verify the file exists and length > 0.
6. Insert into `MediaStore.Images.Media` with display name, mime `image/jpeg` (or the captured mime), and copy bytes. Verify the insert by querying the new `_ID` / URI.
7. Delete the temp file.
8. Refresh Photos. Only then treat capture as success.
9. On cancel: no insert, no snackbar success.
10. On failure (no camera app, write failure, insert failure): typed error, source temp file cleaned up, no claim of success.

If insert fails after a successful camera write, keep the temp file only long enough to retry once; then clean up. Never delete a user MediaStore item in this flow.

### 6.4 Room

Database `universal_gallery.db`, version 1.

Entity `FavoriteEntity` (`mediaId: Long` primary key, `mediaType: String`, `createdAtEpochMs: Long`). DAO with insert/delete/observe — unused by UI in slice 1, present so later favorites do not migrate from nothing. No other entities.

### 6.5 DataStore

Preferences keys:

- `theme_mode`: `system` | `light` | `dark` (default `system`)

No telemetry keys.

## 7. Error handling

### 7.1 Typed errors

`GalleryError` (sealed) in `:core:common`:

- `PermissionDenied` (canRationale: Boolean)
- `MediaNotFound`
- `UnsupportedMedia` (mimeType: String?)
- `StorageUnavailable`
- `StorageFull`
- `CameraUnavailable`
- `CaptureFailed`
- `Database`
- `Unknown` (no throwable message leaked)

Repositories return `GalleryResult<T>` (`Success`, `Failure(GalleryError)`). ViewModels never swallow exceptions in empty `catch`. Data sources map `SecurityException` → permission, `FileNotFoundException` → `MediaNotFound`, `IOException` with ENOSPC → `StorageFull`, missing camera resolve → `CameraUnavailable`.

### 7.2 User-facing mapping

`UserMessage` in `:core:model` holds title, body, and `UserMessageAction` (`Retry`, `AllowAccess`, `OpenSettings`, `None`). A mapper in `:core:ui` (`GalleryErrorMapper`) converts `GalleryError` to `UserMessage` using string resources in `:core:ui`. ViewModels expose `GalleryError`; composables call the mapper.

Examples:

- Permission denied: "Photos need permission" / "Allow access to show your library." / Allow access
- Partial is not an error (banner only)
- Media not found in viewer: "This photo is unavailable." / Close or go to next
- Storage full: "Not enough storage to save the photo."
- Camera unavailable: "No camera app is available."
- Capture failed: "The photo could not be saved. Nothing was added to your library."

Never show stack traces, encryption keys, internal paths, content URIs, or private filenames beyond the MediaStore display name already visible to the user. Never show a success snackbar unless the MediaStore query/insert was verified.

### 7.3 Presentation by severity

- Permission: full-screen state
- Load failure: inline error with retry
- Capture failure: snackbar
- Viewer item failure: inline in viewer, rest of pager intact

## 8. Testing

Unit tests (JUnit 4, Truth, kotlinx-coroutines-test, optional Robolectric):

1. `GalleryErrorMapperTest` — each error type maps to the expected title/action; unknown errors do not include exception messages.
2. `ImageMediaMapperTest` — cursor rows map to `MediaItem`; invalid mime/null id skipped; sort fields preserved.
3. `PhotosViewModelTest` — loading → empty; loading → success; denied; partial with banner flags; error with retry re-queries; permission grant after denied loads photos.
4. `CameraCaptureVerifierTest` — `CameraCaptureVerifier` in `:core:data` treats a missing or zero-length temp file as `CaptureFailed`; a MediaStore URI that re-queries successfully is success.

No fake media lists in production code. Test fakes live only under `src/test`.

Do not claim tests passed unless they were executed.

## 9. Project files and limitations to document

After implementation:

- `PROJECT_STATE.md` — current slice, modules, what works, what does not.
- `CHANGELOG.md` — slice 1 entry.
- Limitations (must appear in `PROJECT_STATE.md`):
  - Album, Folder, Private, Video, Audio, Documents, Folder destination, Vault, Search are not implemented.
  - No in-app editor.
  - Camera uses the system camera app for still photos only.
  - Selected-photos access shows only granted items.
  - No universal codec, document, or vault claims.
  - Favorites table exists but has no UI.
  - Theme can be changed from Image overflow; full Settings come later.
  - App display name is the placeholder `YOUR_APP_NAME`.

## 10. Definition of done for this slice

1. Real MediaStore Photos grid and viewer exist; empty tabs are honest, not fake.
2. Loading, empty, success, partial, denied, permanently denied, error, capture cancel, and capture failure are handled.
3. Unit tests listed above exist and have been executed.
4. The app module compiles (`:app:assembleDebug` or equivalent observed).
5. Limitations are written in `PROJECT_STATE.md`.
6. `PROJECT_STATE.md` and `CHANGELOG.md` are updated.

## 11. Non-goals and explicit honesty rules

- Do not ship placeholder click handlers that pretend to edit, share, or encrypt.
- Do not index or thumbnail anything outside MediaStore images the user granted.
- Do not claim capture or load success without a verified URI/query.
- Do not add ads, tracking, or network media backup.
- Do not introduce Video/Audio permissions until those slices need them.
