# Medroid — ideas and development plan

Medroid is conceived as an app for Android developers: quickly find technical information about the device and have useful utilities at hand that stock "Settings" lacks.

Each utility is a separate pair of `<feature>/api` + `<feature>/impl` modules (see CLAUDE.md); `app` only wires them into navigation.

## Proposed order

1. ~~Home screen with navigation~~ — done
2. Developer-mode toggles
3. App details (signatures, versions, permissions)

## 1. Home screen and navigation

- [x] Home screen with the tools list (`home` module, start `HomeRoute`)
- [x] Navigation to "Device information" and "App search"
- [ ] "Back" button in the tools screen header (currently only system back)

## 2. Developer-mode toggles (new module)

Require `android.permission.WRITE_SECURE_SETTINGS`. A normal app can't be granted it, but a developer has adb, and the permission is granted once (it survives reboots and updates, and is reset when the app is uninstalled):

```bash
adb shell pm grant merail.tools.medroid android.permission.WRITE_SECURE_SETTINGS
```

The app checks `checkSelfPermission` and, if the permission is missing, shows this command with a "Copy" button.

### Wireless debugging without extra steps

Goal: the phone connects to the home Wi-Fi → Medroid enables wireless debugging by itself → Android Studio connects by itself (via mDNS, no need to know the port) → you can press Run.

One thing gets in the way: the system turns off wireless debugging by itself after a reboot and when Wi-Fi disconnects or the network changes. This is what the app addresses.

One-time manual setup (cannot be automated):

1. Pair with the computer: Android Studio → Device Manager → Pair devices using Wi-Fi (QR or code). Needed once per computer.
2. `pm grant` from the command above. No cable needed: the command can be run over Wi-Fi right after pairing.
3. On Xiaomi/MIUI allow Medroid "Autostart", otherwise `BOOT_COMPLETED` and background events may not arrive.

- [ ] Toggle in the app: `Settings.Global` `adb_wifi_enabled` (Android 11+, requires Wi-Fi connected)
- [ ] Auto-enable after boot: `BOOT_COMPLETED` receiver
- [ ] Auto-enable when Wi-Fi connects: `ConnectivityManager.registerNetworkCallback(request, PendingIntent)`, fires without the app running. Re-register after boot and after app update (`MY_PACKAGE_REPLACED`)
- [ ] Network awareness: on a network marked "Always allow on this network" debugging turns on silently; on a new network the system shows a confirmation prompt. To avoid the prompt on foreign networks, you can enable only on selected networks, but reading the network name (SSID) requires the location permission and enabled location
- [ ] Home-screen widget (Jetpack Glance, `androidx.glance:glance-appwidget`): status, IP, current network; "On/Off" and "Copy IP" buttons work without opening the app. It updates from the same events (Wi-Fi, boot) and after taps; in between, the status may lag

We don't make our own quick-settings tile: the system already has an equivalent ("Developer options" → "Quick settings developer tiles" → "Wireless debugging").

Limitations:

- On Android 8–10 there is no wireless debugging, only `adb tcpip 5555`, which needs a cable after every reboot (or root)
- If the router blocks mDNS (client isolation), Studio won't find the phone by itself: you're left with `adb connect ip:port`, and the port is visible on the wireless-debugging screen
- After uninstalling the app, `WRITE_SECURE_SETTINGS` is lost; running from Studio is an update, so the permission is kept

### Other toggles

- [ ] Animation scale, all three at once: `WINDOW_ANIMATION_SCALE`, `TRANSITION_ANIMATION_SCALE`, `ANIMATOR_DURATION_SCALE` (turn off for UI tests, slow down for debugging transitions)
- [ ] "Keep screen on while charging": `STAY_ON_WHILE_PLUGGED_IN`

## 3. App details (evolution of `packages`)

- [ ] SHA-1 / SHA-256 of the signing certificate (for Firebase, Google Sign-In, Maps). `GET_SIGNING_CERTIFICATES` from API 28; with `minSdk 26`, API 26–27 need a fallback to `GET_SIGNATURES`
- [ ] `versionName` / `versionCode`, `minSdk` / `targetSdk`, debuggable flag
- [ ] Install source (`getInstallSourceInfo` from API 30, earlier `getInstallerPackageName`), install and update dates
- [ ] Permission list with a mark for which are granted
- [ ] "Open app settings", "Share APK" buttons
- [ ] Filters: user / system / debuggable-only

## 4. Device information (evolution of `info`)

- [ ] "Copy all as text" — for pasting into a bug report
- [ ] Current window size class, insets, display cutout (for adaptive layout)
- [ ] WebView and Google Play Services versions
- [ ] Hardware features (`FEATURE_*`), sensors, media codecs, OpenGL ES / Vulkan versions

## Technical debt

- [ ] Remove the debug `Log.d("AAAAAAAAAAAA", ...)` from `PackagesViewModel.search()` — it fires for every app on every search
- [ ] Add `._*` to `.gitignore` (files appear when copying the project from macOS)
- [x] `Scaffold` in `MainActivity` returned an unused `innerPadding`: replaced with `Surface`, as in WordMe
