# Changelog

Record meaningful changes in reverse chronological order. Use actual dates and verified facts; do not invent entries.

---

### 2026-10-22 — Documents: SAF library and read-only PDF/text viewer

- **Type:** Added / Documents
- **Summary:** Documents tab uses Storage Access Framework to add user-chosen files (persisted read-only URI permission). PDF pages render in-app; text/json shows a capped preview. Unsupported types can open in another app. Original files are never edited. Format/Folder/Private stay unavailable.
- **Files added:**
  - `data/model/DocumentItem.kt`, `data/repository/{DocumentRepository,SafDocumentRepository}.kt`
  - `ui/documents/{DocumentLogic,DocumentsViewModel,DocumentViewerViewModel,DocumentViewerScreen}.kt`
  - `app/src/test/java/com/example/universalgallery/ui/documents/DocumentLogicTest.kt`
- **Files modified:**
  - `DocumentsScreen.kt`, `GalleryNavHost.kt`, `NavDestination.kt`, `strings.xml`, `AndroidManifest.xml`, `NavDestinationTest.kt`
- **Validation:**
  - `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL in 1m 48s
  - Unit tests: 46 passed
  - APK: 64 MB
- **Known issues:** No in-app Word/Excel renderer. Text preview capped at 1 MB.
- **Rollback:** Restore the Phase 6 tree for the files listed above.

---

### 2026-10-22 — Phase 6: Video/audio libraries and Media3 players

- **Type:** Added / Media / Playback
- **Summary:** Videos and Audios tabs now load real MediaStore items. Tap a video for a Media3 player with controls and speed chips. Tap audio for play/pause/seek. Unsupported, IO, missing, and empty states are honest. Format/Genre/Folder/Private tabs stay unavailable.
- **Files added:**
  - `data/model/MediaCursorRow.kt`, `data/repository/AvMediaMapper.kt`
  - `ui/videos/VideosViewModel.kt`, `ui/audio/AudioViewModel.kt`, `ui/components/AudioList.kt`
  - `ui/player/{PlayerLogic,PlaybackViewModel,VideoPlayerScreen,AudioPlayerScreen}.kt`
  - tests: `AvMediaMapperTest.kt`, `PlayerLogicTest.kt`
- **Files modified:**
  - `MediaRepository.kt`, `MediaStoreMediaRepository.kt`, `VideosScreen.kt`, `AudioScreen.kt`
  - `GalleryNavHost.kt`, `NavDestination.kt`, `MediaGrid.kt`, `strings.xml`
  - `gradle/libs.versions.toml`, `app/build.gradle.kts`
- **Validation:**
  - `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL in 1m 35s
  - Unit tests: 40 passed
  - APK: 63 MB
- **Known issues:** No MediaSessionService/background audio. No PiP/subtitles. Secondary tabs remain empty.
- **Rollback:** Restore the Phase 5 tree for the files listed above.

---

### 2026-10-22 — Phase 5: Full-screen photo viewer

- **Type:** Added / Media / UI
- **Summary:** Tapping a photo opens a full-screen viewer with pinch-zoom, pan, double-tap zoom, metadata sheet, and system share. Missing/error/decode-failed states are honest. Main chrome (top bar, bottom nav, camera FAB) hides while the viewer is open.
- **Files added:**
  - `ui/viewer/PhotoViewerLogic.kt`
  - `ui/viewer/PhotoViewerViewModel.kt`
  - `ui/viewer/PhotoViewerScreen.kt`
  - `app/src/test/java/com/example/universalgallery/ui/viewer/PhotoViewerLogicTest.kt`
- **Files modified:**
  - `GalleryNavHost.kt`, `NavDestination.kt`, `MainScreen.kt`, `MediaRepository.kt`, `MediaStoreMediaRepository.kt`, `strings.xml`, `AndroidManifest.xml`, `NavDestinationTest.kt`
- **Validation:**
  - `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL in 1m 59s
  - Unit tests: 29 passed
  - APK: 57 MB
- **Known issues:** No swipe-to-adjacent photo yet. Share requires a content image URI.
- **Rollback:** Restore the Phase 4 tree for the files listed above.

---

### 2026-10-22 — Phase 4: MediaStore repository and image discovery grid

- **Type:** Added / Media / Architecture
- **Summary:** Added real MediaStore image discovery (`MediaStoreMediaRepository` + ContentObserver), Coil thumbnail grid, album aggregation, `ImagesViewModel` loading/empty/success/error states. Photos and Album tabs are live; Folder and Private remain honest empty states. No fake media lists.
- **Files added:**
  - `data/model/{MediaType,MediaItem,MediaAlbum,MediaSortOrder,ImageCursorRow}.kt`
  - `data/repository/{MediaRepository,MediaStoreMediaRepository,ImageMediaMapper}.kt`
  - `ui/images/ImagesViewModel.kt`
  - `ui/components/{MediaGrid,AlbumGrid,MediaThumbnailItem}.kt`
  - `app/src/test/java/com/example/universalgallery/data/ImageMediaMapperTest.kt`
  - `app/src/main/res/values/plurals.xml`
- **Files modified:**
  - `ImagesScreen.kt`, `strings.xml`, `app/build.gradle.kts`, `gradle/libs.versions.toml`
- **Validation:**
  - `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL in 7m 41s
  - Unit tests: 20 passed
  - APK: 57 MB
- **Known issues:** Photo tap has no viewer yet. Video/Audio libraries not implemented.
- **Rollback:** Restore the Phase 3 tree for the files listed above.

---

### 2026-10-22 — Phase 3: Android 14+ MediaStore runtime permissions

- **Type:** Added / Security / UI
- **Summary:** Added typed visual/audio/camera permission evaluation (`PermissionHelper`), rationale UI that does not request until Allow access, partial-access banner with Photo Picker add and Manage access, permanently-denied settings fallback, and CAMERA request on the camera FAB. Wired Image/Video (visual) and Audio screens.
- **Files added:**
  - `app/src/main/java/com/example/universalgallery/permissions/MediaPermissionType.kt`
  - `app/src/main/java/com/example/universalgallery/permissions/MediaPermissionStatus.kt`
  - `app/src/main/java/com/example/universalgallery/permissions/PermissionHelper.kt`
  - `app/src/main/java/com/example/universalgallery/permissions/MediaPermissionGate.kt`
  - `app/src/main/java/com/example/universalgallery/ui/components/PermissionRationaleCard.kt`
  - `app/src/main/java/com/example/universalgallery/ui/components/PartialAccessBanner.kt`
  - `app/src/test/java/com/example/universalgallery/permissions/PermissionHelperTest.kt`
- **Files modified:**
  - `ImagesScreen.kt`, `VideosScreen.kt`, `AudioScreen.kt`, `MainScreen.kt`
  - `strings.xml`, `app/build.gradle.kts`, `gradle/libs.versions.toml`
- **Validation:**
  - `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL in 8m 11s
  - Unit tests: 14 passed (0 failures)
  - APK: 55 MB
- **Known issues:** Libraries still empty until Phase 4 MediaStore grid.
- **Rollback:** Restore the Phase 2 tree for the files listed above.

---

### 2026-10-22 — Phase 2: Bottom navigation scaffold and media tabs

- **Type:** Added / UI / Architecture
- **Summary:** Replaced the Phase 1 greeting scaffold with a 5-destination Material 3 bottom bar (Image, Video, Audio, Documents, Folder), media top tabs per `PROJECT_BRIEF.md`, honest empty section copy, and a camera FAB that launches `ACTION_IMAGE_CAPTURE` when a camera app exists (snackbar otherwise). Added `NavDestinationTest`.
- **Files added:**
  - `app/src/main/java/com/example/universalgallery/navigation/NavDestination.kt`
  - `app/src/main/java/com/example/universalgallery/navigation/GalleryBottomBar.kt`
  - `app/src/main/java/com/example/universalgallery/navigation/GalleryNavHost.kt`
  - `app/src/main/java/com/example/universalgallery/ui/main/MainScreen.kt`
  - `app/src/main/java/com/example/universalgallery/ui/components/MediaTopTabRow.kt`
  - `app/src/main/java/com/example/universalgallery/ui/components/UnavailableSection.kt`
  - `app/src/main/java/com/example/universalgallery/ui/images/ImagesScreen.kt`
  - `app/src/main/java/com/example/universalgallery/ui/videos/VideosScreen.kt`
  - `app/src/main/java/com/example/universalgallery/ui/audio/AudioScreen.kt`
  - `app/src/main/java/com/example/universalgallery/ui/documents/DocumentsScreen.kt`
  - `app/src/main/java/com/example/universalgallery/ui/folders/FoldersScreen.kt`
  - `app/src/test/java/com/example/universalgallery/navigation/NavDestinationTest.kt`
- **Files modified:**
  - `gradle/libs.versions.toml` (navigation-compose 2.8.5, material-icons-extended)
  - `app/build.gradle.kts`
  - `app/src/main/java/com/example/universalgallery/MainActivity.kt`
  - `app/src/main/AndroidManifest.xml` (`IMAGE_CAPTURE` queries)
  - `app/src/main/res/values/strings.xml`
- **Validation:**
  - `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL in 7m 11s
  - Unit tests: 5 passed (ScaffoldSanityTest 1 + NavDestinationTest 4)
  - APK: `app/build/outputs/apk/debug/app-debug.apk` (55 MB)
- **Known issues:** Camera capture does not insert into MediaStore yet. Tabs show honest empty states, not libraries.
- **Rollback:** Restore files listed above to the Phase 1 tree.

---

### 2026-10-22 — Phase 1 restart in this workspace — Android project scaffold

- **Type:** Build / Added
- **Summary:** The imported zip contained documentation only (no `app/` source). Recreated a working single-module Android scaffold in `/workspace`: Gradle 8.9, AGP 8.7.0, Kotlin 2.1.0, Compose BOM 2024.12.01, Material 3, minSdk 34 / targetSdk 35. Added Compose `MainActivity`, `UniversalGalleryApp`, theme, privacy-first backup/extraction rules, scoped media permission declarations, and `ScaffoldSanityTest`.
- **Files added:**
  - `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
  - `gradle/libs.versions.toml`, `gradle/wrapper/gradle-wrapper.*`, `gradlew`, `gradlew.bat`
  - `app/build.gradle.kts`, `app/proguard-rules.pro`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/java/com/example/universalgallery/UniversalGalleryApp.kt`
  - `app/src/main/java/com/example/universalgallery/MainActivity.kt`
  - `app/src/main/java/com/example/universalgallery/ui/theme/{Color,Type,Theme}.kt`
  - `app/src/main/res/values/strings.xml`, `themes.xml`
  - `app/src/main/res/drawable/ic_launcher_background.xml`, `ic_launcher_foreground.xml`
  - `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`, `ic_launcher_round.xml`
  - `app/src/main/res/xml/data_extraction_rules.xml`, `backup_rules.xml`
  - `app/src/test/java/com/example/universalgallery/ScaffoldSanityTest.kt`
  - `.gitignore`, `local.properties` (gitignored)
- **Validation:**
  - `/opt/gradle/gradle-8.9/bin/gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL in 4m 48s
  - Unit tests: 1 passed (`ScaffoldSanityTest`)
  - APK: `app/build/outputs/apk/debug/app-debug.apk` (23 MB)
- **Known issues:**
  - `applicationId` = `com.example.universalgallery` — placeholder
  - App display name = `YOUR_APP_NAME` — placeholder
  - Launcher icons are vector placeholders
  - Non-fatal: `Unable to strip libandroidx.graphics.path.so`
  - No emulator/device run
  - Original Phases 2–5 source from the Windows machine is not in this tree
- **Rollback:** Restore pre-scaffold Git state (`0c4f00d` plus untracked docs). After commit, `git revert` that commit.

---

### Historical entries below describe work on a different machine (`C:\Users\HEMANT\Desktop\Project\UniversalGallery`). That source is not in this workspace.

---

### 2026-10-22 — Phase 5: Full-Screen Photo Viewer & Metadata Sheet

- **Type:** Added / Viewer / UI
- **Summary:** Implemented full-screen immersive photo viewer (`PhotoViewerScreen`) with pinch-to-zoom (1×–5×), pan clamping, double-tap to toggle 2× zoom, single-tap overlay toggle, share action via `Intent.ACTION_SEND`, and `MediaMetadataSheet` bottom sheet (file name, resolution, file size, date added, MIME type). Added `PhotoViewerRoute` navigation object with typed `mediaId: Long` argument. Wired item clicks in `ImagesScreen`→`MediaGrid` through `GalleryNavHost` to navigate to viewer. Added `PhotoViewerViewModel` to load a single `MediaItem` by id. Added 4 unit tests in `PhotoViewerRouteTest`.
- **Files added:**
  - `app/src/main/java/com/example/universalgallery/ui/viewer/PhotoViewerScreen.kt`
  - `app/src/main/java/com/example/universalgallery/ui/viewer/PhotoViewerViewModel.kt`
  - `app/src/main/java/com/example/universalgallery/ui/viewer/MediaMetadataSheet.kt`
  - `app/src/test/java/com/example/universalgallery/navigation/PhotoViewerRouteTest.kt`
- **Files modified:**
  - `app/src/main/java/com/example/universalgallery/navigation/NavDestination.kt` (added `PhotoViewerRoute` object)
  - `app/src/main/java/com/example/universalgallery/navigation/GalleryNavHost.kt` (registered `PhotoViewerRoute` composable; wired `onItemClick` from `ImagesScreen`)
  - `app/src/main/java/com/example/universalgallery/ui/images/ImagesScreen.kt` (added `onItemClick` parameter, wired to `MediaGrid`)
  - `app/src/main/res/values/strings.xml` (added viewer_back, viewer_share, viewer_info, meta_* strings)
- **Validation:**
  - `.\\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` → BUILD SUCCESSFUL in 21s (all unit tests passed)
  - Unit test count: 16 (previous 12 + 4 new `PhotoViewerRouteTest`)
- **Commit:** `fd3c8bc`
- **Rollback:** `git revert fd3c8bc`

---

### 2026-10-22 — Phase 4: MediaStore Repository & Image Discovery Grid

- **Type:** Added / Media / Architecture
- **Summary:** Added Coil image loading, created immutable media domain models (`MediaItem`, `MediaType`, `MediaAlbum`, `MediaSortOrder`), implemented `MediaStoreMediaRepository` with safe `ContentResolver` queries and album aggregation, built `ImagesViewModel`, created responsive Compose grids (`MediaGrid`, `AlbumGrid`, `MediaThumbnailItem`), and added unit tests in `MediaRepositoryTest`.
- **Files added/modified:**
  - `gradle/libs.versions.toml` (added `coil = "2.7.0"`, `mockito = "5.14.2"`)
  - `app/build.gradle.kts` (added `coil.compose`, `mockito.core`, enabled `unitTests.isReturnDefaultValues = true`)
  - `app/src/main/java/com/example/universalgallery/data/model/MediaType.kt` (IMAGE, VIDEO, AUDIO)
  - `app/src/main/java/com/example/universalgallery/data/model/MediaItem.kt` (domain model)
  - `app/src/main/java/com/example/universalgallery/data/model/MediaAlbum.kt` (album model)
  - `app/src/main/java/com/example/universalgallery/data/model/MediaSortOrder.kt` (DATE_DESC, DATE_ASC, etc.)
  - `app/src/main/java/com/example/universalgallery/data/repository/MediaRepository.kt` (repository interface)
  - `app/src/main/java/com/example/universalgallery/data/repository/MediaStoreMediaRepository.kt` (MediaStore ContentResolver implementation)
  - `app/src/main/java/com/example/universalgallery/ui/images/ImagesViewModel.kt` (manages images & albums state)
  - `app/src/main/java/com/example/universalgallery/ui/components/MediaThumbnailItem.kt` (Coil image loader with video duration badge)
  - `app/src/main/java/com/example/universalgallery/ui/components/MediaGrid.kt` (adaptive grid with Loading, Empty, and Error states)
  - `app/src/main/java/com/example/universalgallery/ui/components/AlbumGrid.kt` (album grid with cover and item count)
  - `app/src/main/java/com/example/universalgallery/ui/images/ImagesScreen.kt` (integrated ViewModel and real grids)
  - `app/src/test/java/com/example/universalgallery/data/MediaRepositoryTest.kt` (unit tests for models, sort orders, and repository flows)
- **Validation:**
  - `.\gradlew.bat :app:testDebugUnitTest` → BUILD SUCCESSFUL (all 12 unit tests passed)
  - `.\gradlew.bat :app:assembleDebug` → BUILD SUCCESSFUL in 1m 10s, generated APK: `app/build/outputs/apk/debug/app-debug.apk` (58.83 MB)
- **Rollback:** `git restore .` if uncommitted or `git revert <commit>`.

---

### 2026-10-22 — Phase 3: Android 14+ MediaStore Runtime Permissions

- **Type:** Added / Security / UI
- **Summary:** Implemented Android 14+ granular media permissions flow (`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO`, and `READ_MEDIA_VISUAL_USER_SELECTED`), added `PermissionRationaleCard` with settings redirect, added `PartialAccessBanner` for selective photo/video access, wired permissions into `ImagesScreen`, `VideosScreen`, and `AudioScreen`, and added unit tests in `PermissionHelperTest`.
- **Files added/modified:**
  - `app/src/main/res/values/strings.xml` (permission rationale, partial banner, and settings strings)
  - `app/src/main/java/com/example/universalgallery/permissions/MediaPermissionType.kt` (Visual vs Audio permission sets)
  - `app/src/main/java/com/example/universalgallery/permissions/MediaPermissionStatus.kt` (GrantedFull, GrantedPartial, Denied states)
  - `app/src/main/java/com/example/universalgallery/permissions/PermissionHelper.kt` (status checking and result evaluation logic)
  - `app/src/main/java/com/example/universalgallery/ui/components/PermissionRationaleCard.kt` (accessible rationale card with Settings button)
  - `app/src/main/java/com/example/universalgallery/ui/components/PartialAccessBanner.kt` (banner for re-launching photo selector in partial mode)
  - `app/src/main/java/com/example/universalgallery/ui/images/ImagesScreen.kt` (wired visual media permissions)
  - `app/src/main/java/com/example/universalgallery/ui/videos/VideosScreen.kt` (wired visual media permissions)
  - `app/src/main/java/com/example/universalgallery/ui/audio/AudioScreen.kt` (wired audio media permissions)
  - `app/src/test/java/com/example/universalgallery/permissions/PermissionHelperTest.kt` (unit tests for full, partial, and denied evaluations)
- **Validation:**
  - `.\gradlew.bat :app:testDebugUnitTest` → BUILD SUCCESSFUL (all unit tests passed including `PermissionHelperTest`)
  - `.\gradlew.bat :app:assembleDebug` → BUILD SUCCESSFUL, generated APK: `app/build/outputs/apk/debug/app-debug.apk` (57.08 MB)
- **Rollback:** `git restore .` if uncommitted or `git revert <commit>`.

---

### 2026-10-22 — Phase 2: Bottom Navigation Scaffold & Media Tabs

- **Type:** Added / UI / Architecture
- **Summary:** Implemented the 5-destination bottom navigation architecture (`Images`, `Videos`, `Audio`, `Documents`, `Folders`) per `PROJECT_BRIEF.md`, added camera Floating Action Button with safe intent launch, added top tab rows for media categories, updated app display name to `Universal Gallery`, and added navigation unit test suite.
- **Files added/modified:**
  - `gradle/libs.versions.toml` (added `navigation-compose` 2.8.5 and `material-icons-extended`)
  - `app/build.gradle.kts` (integrated navigation and extended icons dependencies)
  - `app/src/main/res/values/strings.xml` (updated `app_name` to `Universal Gallery` and added navigation/tab string resources)
  - `app/src/main/java/com/example/universalgallery/navigation/NavDestination.kt` (defined 5 typed primary destinations)
  - `app/src/main/java/com/example/universalgallery/navigation/GalleryBottomBar.kt` (Material 3 bottom navigation bar)
  - `app/src/main/java/com/example/universalgallery/navigation/GalleryNavHost.kt` (NavHost routing to the 5 destinations)
  - `app/src/main/java/com/example/universalgallery/ui/components/MediaTopTabRow.kt` (reusable top tab row)
  - `app/src/main/java/com/example/universalgallery/ui/images/ImagesScreen.kt` (Photos | Album | Folder | Private)
  - `app/src/main/java/com/example/universalgallery/ui/videos/VideosScreen.kt` (Videos | Format | Folder | Private)
  - `app/src/main/java/com/example/universalgallery/ui/audio/AudioScreen.kt` (Audios | Genre | Folder | Private)
  - `app/src/main/java/com/example/universalgallery/ui/documents/DocumentsScreen.kt` (Documents | Format | Folder | Private)
  - `app/src/main/java/com/example/universalgallery/ui/folders/FoldersScreen.kt` (Folders root screen)
  - `app/src/main/java/com/example/universalgallery/ui/main/MainScreen.kt` (Scaffold with TopAppBar, BottomBar, Camera FAB, and NavHost)
  - `app/src/main/java/com/example/universalgallery/MainActivity.kt` (hosts `MainScreen` with edge-to-edge support)
  - `app/src/test/java/com/example/universalgallery/navigation/NavDestinationTest.kt` (verified 5 destinations, ordering, and unique routes)
- **Validation:**
  - `.\gradlew.bat :app:testDebugUnitTest` → BUILD SUCCESSFUL (2 unit tests passed: `ScaffoldSanityTest`, `NavDestinationTest`)
  - `.\gradlew.bat :app:assembleDebug` → BUILD SUCCESSFUL in 1m 3s, generated APK: `app/build/outputs/apk/debug/app-debug.apk` (56.65 MB)
- **Rollback:** `git restore .` if uncommitted or `git revert <commit>`.

---

### 2026-10-20 — Safe stop and session handoff checkpoint

- **Type:** Docs / Build
- **Summary:** Verified build and unit test artifacts, added `.kotlin/` exclusion to `.gitignore`, created comprehensive `SESSION_HANDOFF.md`, and updated durable project state for session transition.
- **Files modified/added:**
  - `.gitignore` (added `.kotlin/` to ignore compiler cache)
  - `PROJECT_STATE.md` (updated status, commit references, and verified metrics)
  - `CHANGELOG.md` (recorded safe stop checkpoint)
  - `SESSION_HANDOFF.md` (created comprehensive handoff guide)
- **Validation:**
  - `git status` inspected; working tree clean of untracked build artifacts.
  - Verification confirmed for `app/build/outputs/apk/debug/app-debug.apk` (22.96 MB) and unit test passing.
- **Rollback:** `git restore .` if uncommitted or `git revert HEAD`.

---

### 2026-10-20 — Android project scaffold — working build established

- **Type:** Build / Added
- **Summary:** Created complete Android project skeleton from scratch. Git initialised. Gradle 8.9 wrapper generated. Single `:app` module with AGP 8.7.0, Kotlin 2.1.0, Jetpack Compose BOM 2024.12.01, Material 3. Android SDK Platform 35 auto-installed by Gradle.
- **Files added:**
  - `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
  - `gradle/libs.versions.toml`, `gradle/wrapper/gradle-wrapper.*`, `gradlew`, `gradlew.bat`
  - `app/build.gradle.kts`, `app/proguard-rules.pro`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/java/com/example/universalgallery/UniversalGalleryApp.kt`
  - `app/src/main/java/com/example/universalgallery/MainActivity.kt`
  - `app/src/main/java/com/example/universalgallery/ui/theme/Theme.kt`
  - `app/src/main/res/values/strings.xml`, `themes.xml`
  - `app/src/main/res/drawable/ic_launcher_background.xml`, `ic_launcher_foreground.xml`
  - `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`, `ic_launcher_round.xml`
  - `app/src/main/res/xml/data_extraction_rules.xml`, `backup_rules.xml`
  - `app/src/test/java/com/example/universalgallery/ScaffoldSanityTest.kt`
  - `.gitignore`, `local.properties` (gitignored)
- **Validation:**
  - `.\gradlew.bat projects` → BUILD SUCCESSFUL — `:app` discovered, no `app/app/` nesting
  - `.\gradlew.bat :app:testDebugUnitTest` → BUILD SUCCESSFUL — 1 test passed (`ScaffoldSanityTest`)
  - `.\gradlew.bat :app:assembleDebug` → BUILD SUCCESSFUL in 1m 41s — APK: `app/build/outputs/apk/debug/app-debug.apk` (22.96 MB)
- **Known issues:**
  - `applicationId` = `com.example.universalgallery` — placeholder, must be set before release
  - App display name = `YOUR_APP_NAME` — placeholder
  - Launcher icons are vector placeholders
  - Non-fatal build warning: `Unable to strip libandroidx.graphics.path.so`
  - No emulator/device run performed
- **Rollback:** `git revert 59585d9` (scaffold commit — no user data)

---

## Entry template
### YYYY-MM-DD — Short change title
- **Type:** Added / Changed / Fixed / Security / Performance / Docs / Build
- **Summary:**
- **Files:**
- **Validation:** Exact commands and observed results.
- **Known issues:**
- **Rollback:** Commit hash or backup location.
