# UniversalGallery — Dedicated `app/` Folder Organization

## Objective
Keep the Android application module, application-specific source, resources, manifest, tests and module configuration inside the root `app/` directory. Keep root-level build configuration, Gradle wrapper, shared tooling and project-wide documentation outside it.

## Target layout (adapt to actual repository)

```text
UniversalGallery/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/<existing-package>/
│       │   └── res/
│       ├── test/
│       └── androidTest/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
├── PROJECT_BRIEF.md
├── PROJECT_STATE.md
├── CHANGELOG.md
├── CHANGE_CONTROL.md
├── TOKEN_SAFETY_AND_RESUME.md
├── AI_START_HERE_PROMPT.md
├── AGENTS.md
└── agents/
```

The Kotlin source directory may use `java/` or `kotlin/` according to the existing project. Preserve legitimate additional modules. Do not create `app/app/`.

## Permanent rule for all AI agents

The `app/` directory is the primary UniversalGallery Android application module. Before editing, inspect the actual repository and Git status. Keep application source, resources, manifests, tests, and module-specific configuration in `app/`. Keep only project-wide configuration, wrapper files, shared tooling, and documentation at the root. Do not guess paths, blindly move files, alter the application ID or namespace, overwrite unrelated work, or restructure an already-correct project. After structural changes, verify Gradle project discovery, tests and a debug build. Update `PROJECT_STATE.md` and `CHANGELOG.md` with actual results.

## Safe migration procedure

1. Inspect the existing tree, Gradle settings, module names, package names, and uncommitted changes.
2. Report the current structure and propose exact moves. Do not move anything during inspection.
3. Establish a safe Git checkpoint or verified backup, without including unrelated user changes.
4. Move only necessary files, preserving existing architecture, resources, assets, tests, dependencies, namespace and application ID.
5. Ensure `settings.gradle.kts` includes the correct module, typically `include(":app")`; adjust only if the existing project needs it.
6. Verify Gradle sync/project discovery and run appropriate tests and debug build.
7. Review the full diff for missing files, unintended deletion and unrelated modifications.
8. Record changes, validation results, unresolved issues, and rollback reference in project documentation.

Do not run `git reset --hard`, `git clean -fd`, or broad deletion commands as routine migration or recovery. Never claim verification succeeded unless it actually ran and passed.

## Suggested validation commands

From the project root on Windows:

```powershell
.\gradlew.bat projects
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

On macOS/Linux, use `./gradlew` instead of `.\gradlew.bat`. Adapt test tasks to the project's actual build variants and module setup. If checks cannot run, record `Not run` and why.

## Copy-paste Antigravity instruction

> Read `APP_FOLDER_ORGANIZATION.md`, `AI_START_HERE_PROMPT.md`, `PROJECT_STATE.md`, `CHANGE_CONTROL.md`, and `AGENTS.md` if present. Inspect the actual project tree, Gradle settings and Git status. I want the Android application module organized inside a dedicated root `app/` folder, with project-wide documentation and Gradle wrapper at the root. First report the current structure and propose a safe, exact migration plan. Do not move files until you have explained the changes and risks. Preserve my existing code, package names, application ID, resources, tests and unrelated changes. After approval, perform minimal moves, verify Gradle discovery, run tests and `:app:assembleDebug`, inspect the diff, and update `PROJECT_STATE.md` and `CHANGELOG.md` with verified results. Do not create `app/app/` or claim tests passed unless executed.
