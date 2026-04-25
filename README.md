# AnimeKai Android

An unofficial Android wrapper for [AnimeKai](https://animekai.to), built with a native WebView shell. Provides a clean, fullscreen-capable mobile experience for the site with proper notch and system bar handling.

---

## Disclaimer

This project is an **unofficial, community-built client** and is in no way affiliated with, endorsed by, or associated with AnimeKai or its operators. All content, trademarks, and intellectual property belong to their respective owners.

This app exists solely to improve the mobile browsing experience for AnimeKai's existing website. It does not host, scrape, redistribute, or modify any content from the site. It is simply a WebView wrapper — equivalent to opening the site in a mobile browser — with quality-of-life improvements such as fullscreen video support and proper display cutout handling.

Full credit and recognition goes to the team behind [animekai.to](https://animekai.to) for building and maintaining the platform. Without their work, this project would have no purpose. If you enjoy the content, please support the original site directly.

---

## Features

- Loads AnimeKai in a native Android WebView shell
- Fullscreen video playback with landscape auto-rotation
- Proper handling of display cutouts (notch) and system bars
- Progress indicator during page loads
- Offline error screen with retry option
- Navigation restricted to AnimeKai domains only — no external redirects
- Hardware-accelerated rendering
- Keeps screen on during playback

---

## Requirements

- Android 5.0 (API 21) or higher
- Android Studio Hedgehog or newer
- JDK 17
- Gradle 8.14

---

## Building

Clone the repository and open it in Android Studio, or build from the command line:

```bash
git clone https://github.com/dakshhpanchal/animekai.git
cd animekai
./gradlew assembleRelease
```

The signed APK (using the debug signing config) will be output to:

```
app/build/outputs/apk/release/animekai-v1.0.apk
```

---

## Project Structure

```
animekai/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/example/animekai/
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── values/styles.xml
│   │   │   └── values-night/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/wrapper/
    └── gradle-wrapper.properties
```

---

## Allowed Domains

The WebView restricts navigation to the following domains only. Any external link is blocked from loading inside the app:

- `anikai.to`
- `animekai.fi`
- `animekai.fo`
- `animekai.gs`
- `animekai.la`
- `animekai.to`

---

## Notes

- The app uses a standard mobile Chrome user agent string to ensure site compatibility.
- Mixed content is allowed to support the site's media delivery infrastructure.
- Console logging is suppressed after page load to reduce noise.
- Release builds use the debug signing config for convenience. For distribution, replace with a proper keystore.

---

## License

This project is released under the MIT License. See `LICENSE` for details.

This project makes no claim over any content served by AnimeKai. Use responsibly and in accordance with the terms of service of [animekai.to](https://animekai.to).