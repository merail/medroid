# Medroid — ideas and development plan

Medroid is conceived as an app for Android developers: quickly find technical information about the device and have useful utilities at hand that stock "Settings" lacks.

Each utility is a separate pair of `<feature>/api` + `<feature>/impl` modules (see CLAUDE.md); `app` only wires them into navigation.

## Proposed order

1. ~~Home screen with navigation~~ — done
2. App details (evolution of `packages`)
3. Dev toolbox (new module)
4. Network diagnostics (new module)
5. Device information (evolution of `info`)
6. Wireless debugging (dev build only — not publishable in RuStore)

## 1. Home screen and navigation

- [x] Home screen with the tools list (`home` module, start `HomeRoute`)
- [x] Navigation to "Device information" and "App search"
- [ ] "Back" button in the tools screen header (currently only system back)

## 2. App details (evolution of `packages`)

- [x] SHA-1 / SHA-256 of the signing certificate. `GET_SIGNING_CERTIFICATES` from API 28; with `minSdk 26`, API 26–27 need a fallback to `GET_SIGNATURES`
- [x] `versionName` / `versionCode`, `minSdk` / `targetSdk`, debuggable flag
- [x] Install source (`getInstallSourceInfo` from API 30, earlier `getInstallerPackageName`), install and update dates
- [x] Permission list with a mark for which are granted
- [x] "Open app settings", "Share APK" buttons
- [x] ADB command generator (`pm path`, `pm clear`, `pm uninstall -k`, `am start`, `dumpsys package`) with a "copy" button
- [ ] Filters: user / system / debuggable-only

## 3. Dev toolbox (new module)

Offline utilities with no permissions (clipboard only); gives the RuStore build standalone value:

- [ ] base64 / MD5 / SHA-1 / SHA-256 encode + decode
- [ ] JSON formatter / validator, YAML ↔ JSON, JWT decoder
- [ ] regex tester with match highlighting
- [ ] UUID / random string generator
- [ ] color converter (HEX ↔ RGB ↔ HSL)
- [ ] epoch ↔ date, unit converters (px↔dp, bytes)

## 4. Network diagnostics (new module)

Only `INTERNET` (Safe). Full SSID/BSSID would need location (`ACCESS_FINE_LOCATION`, Sensitive) — skip it.

- [ ] ping (ICMP or TCP ping), traceroute
- [ ] port scan (TCP connect), DNS lookup (A/AAAA/MX/TXT), whois
- [ ] current network: IP, gateway, DNS servers, DHCP server, link speed, Wi-Fi band/channel, signal level

## 5. Device information (evolution of `info`)

- [ ] "Copy all as text" — for pasting into a bug report
- [ ] Current window size class, insets, display cutout (for adaptive layout)
- [ ] WebView and Google Play Services versions
- [ ] Hardware features (`FEATURE_*`), sensors, media codecs, OpenGL ES / Vulkan versions

## 6. Wireless debugging (new module, dev build only)

> **Distribution (RuStore moderation, 2026-11-25):** wireless debugging writes `Settings.Global` and needs `WRITE_SECURE_SETTINGS`, which RuStore lists as **Prohibited** and auto-rejects. So this feature cannot ship in the RuStore build: the RuStore version keeps only the publishable features, and wireless debugging ships in a separate direct-APK build (e.g. GitHub Releases) for developers who already use adb.

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

## Technical debt

- [x] Remove the debug `Log.d("AAAAAAAAAAAA", ...)` from `PackagesViewModel.search()` — it fires for every app on every search
- [ ] Add `._*` to `.gitignore` (files appear when copying the project from macOS)
- [x] `Scaffold` in `MainActivity` returned an unused `innerPadding`: replaced with `Surface`, as in WordMe
