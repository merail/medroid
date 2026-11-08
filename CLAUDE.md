# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Medroid (`merail.tools.medroid`) is a single-activity Android app of developer/device tools built with Jetpack Compose, Hilt and type-safe Navigation Compose. Kotlin 2.0, AGP 8.7, Gradle 8.9, JDK 17, `minSdk 26`, `compileSdk`/`targetSdk 36`. UI strings and code comments are in Russian.

The target audience is Android developers: quick access to technical device info plus dev utilities missing from stock Settings. Planned tools, priorities and known tech debt are tracked in `ROADMAP.md` (in Russian); check it before starting a new feature and tick items off when done.

## Commands

The project is developed on Windows. Use `./gradlew` from Git Bash or `.\gradlew.bat` from PowerShell.

```bash
./gradlew assembleDebug                 # build debug APK
./gradlew installDebug                  # build + install on connected device
./gradlew :packages:impl:assembleDebug  # build a single module
./gradlew lint                          # Android lint for all modules
```

There are currently no unit or instrumented tests (no `src/test` / `src/androidTest` dirs). Test dependencies (`androidx-junit`, `espresso`, `ui-test-junit4`) exist in the version catalog but are not wired into any module. If tests are added: `./gradlew :<module>:testDebugUnitTest --tests "fully.qualified.ClassTest"`.

## Architecture

Multi-module, feature-per-module with an **api/impl split**:

```
app             → MainActivity, @HiltAndroidApp, nav graph (navigation/MedroidNavGraph.kt); depends on every module
design          → MedroidTheme (custom green Material3 scheme, dynamic color off by default), typography
home            → start screen with the tools list (UI only, so a single module without api/impl)
<feature>/api   → public interfaces + models only (no Compose, no Hilt)
<feature>/impl  → screen, ViewModel, repository implementations, Hilt module, navigation entry
```

Current features: `info` (device/OS/display/network information) and `packages` (installed apps list with search). The parent `info` / `packages` directories are included in `settings.gradle.kts` but have no build file of their own. A UI-only feature with no public API can be a single module like `home`.

Conventions that span several files when adding a feature or screen:

- **api module**: interfaces are named `I*` (`IOsDataInformer`, `IPackagesRepository`); models live in `api/model`.
- **impl module**: implementations are `internal class ... @Inject constructor(...)` in `impl/repository`, bound to the api interface by an `internal interface <Feature>Module` (`@Module @InstallIn(SingletonComponent::class)`, `@Binds @Singleton`) in `impl/di`.
- **Navigation** (same scheme as the sibling WordMe project): each feature's `<Feature>Navigation.kt` holds the `@Serializable data object <Feature>Route`, `fun NavController.navigateTo<Feature>(navOptions: NavOptionsBuilder.() -> Unit = {})` (non-start destinations set `launchSingleTop = true` inside it) and `fun NavGraphBuilder.<feature>Screen(<callbacks>)`. Screens never get a `NavController`: they take lambdas (`onInfoClick`), and `MedroidNavHost` wires them with references like `navigateToInfo = navController::navigateToInfo`, so `home` doesn't depend on feature modules. The start destination is `HomeRoute`; returning is via system back (screens have no top app bar yet). The screen composable and ViewModel are `internal`; ViewModels are `@HiltViewModel` and obtained with `hiltViewModel()`.
- **Screens** apply `systemBarsPadding()` themselves. `MainActivity` hosts the NavHost in a `Surface` with `colorScheme.background`: the default `surface` color equals the card color `surfaceContainer` in the dark scheme.
- **New module checklist**: add `include(...)` in `settings.gradle.kts`, copy the plugin/`android {}` block from an existing module of the same kind (`impl` modules need `kotlin.compose`, `kotlin.serialization`, `ksp`, `dagger.hilt`), add `implementation(projects.<feature>.api)` / `.impl` to `app/build.gradle.kts`, register `<feature>Screen()` in `MedroidNavHost`, then add a `ToolItem` to `HomeScreen` with a new `navigateTo<Feature>` callback in `homeScreen(...)` (strings `home_*` live in `home`). Modules are referenced through type-safe project accessors (`projects.info.api`, `TYPESAFE_PROJECT_ACCESSORS` is enabled).
- **Dependencies** go through `gradle/libs.versions.toml` (Compose versions come from the BOM). Exception: Coil (`io.coil-kt:coil-compose:2.7.0`) is hardcoded in `packages/impl/build.gradle.kts`.
- Android permissions are declared in the manifest of the impl module that needs them (e.g. `ACCESS_NETWORK_STATE` in `info/impl`), not in `app`.
- String resources are per-module and prefixed with the feature name (`info_*`).

## Repository notes

- Copying the project from a macOS volume creates AppleDouble `._*` files next to every file. `._*` is not gitignored (`.DS_Store` is), so stage files explicitly instead of `git add .` / `git add -A`, and delete `._*` files if they reappear.
- `core.autocrlf=true` is set; `gradlew` may show a spurious mode change (755 → 644) on Windows. Don't commit that.
