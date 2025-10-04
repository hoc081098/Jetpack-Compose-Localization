# Jetpack Compose Localization

A demonstration Android application showcasing how to implement runtime language switching in Jetpack Compose using AndroidX AppCompat's per-app language preferences.

## Overview

This project demonstrates best practices for implementing localization and runtime language switching in a modern Android application built with Jetpack Compose. The app allows users to dynamically change the application language at runtime without restarting, using the Android 13+ per-app language preferences API (backwards compatible to API 27).

## Features

- **Runtime Language Switching**: Change the app language dynamically without restarting
- **Modern UI with Jetpack Compose**: Built entirely with Jetpack Compose and Material 3
- **Multiple Language Support**: Currently supports English (en) and Vietnamese (vi)
- **Per-App Language Settings**: Uses AndroidX AppCompat's `setApplicationLocales()` API
- **Locale Configuration**: Displays current locale information including language, country, and language tag
- **Material Design 3**: Implements Material You design system

## Supported Languages

- **English** (en, en-US)
- **Vietnamese** (vi, vi-VN)

The app automatically filters and displays available language options, highlighting the currently selected language.

## Requirements

- **Minimum SDK**: API 27 (Android 8.1)
- **Target SDK**: API 36 (Android 14)
- **Compile SDK**: API 36
- **Kotlin**: 2.0.21
- **Gradle**: 8.13.0
- **Java**: 11

## Tech Stack

### Core Libraries
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Material Design 3 components for Compose
- **AndroidX Core KTX**: Kotlin extensions for Android APIs
- **AndroidX AppCompat**: Compatibility library for per-app language preferences
- **AndroidX Lifecycle**: Lifecycle-aware components

### Build Tools
- **Kotlin Gradle Plugin**: 2.0.21
- **Android Gradle Plugin**: 8.13.0
- **Gradle**: 8.x

## Project Structure

```
app/src/main/
├── java/com/hoc081098/jetpackcomposelocalization/
│   ├── MainActivity.kt                 # Main activity with language switching logic
│   └── ui/
│       ├── locale/
│       │   └── currentLocale.kt       # Composable function to get current locale
│       └── theme/
│           ├── Color.kt               # Color definitions
│           ├── Theme.kt               # Material Theme configuration
│           └── Type.kt                # Typography definitions
└── res/
    ├── values/                        # Default resources (English)
    │   └── strings.xml
    └── values-vi/                     # Vietnamese resources
        └── strings.xml
```

## Setup and Installation

### Prerequisites

1. Install [Android Studio](https://developer.android.com/studio) (latest version recommended)
2. Ensure you have JDK 11 or higher installed
3. Set up an Android emulator or connect a physical Android device

### Clone and Build

```bash
# Clone the repository
git clone https://github.com/hoc081098/Jetpack-Compose-Localization.git

# Navigate to the project directory
cd Jetpack-Compose-Localization

# Build the project
./gradlew build

# Install on connected device/emulator
./gradlew installDebug
```

### Running the App

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select your target device/emulator
4. Click the "Run" button or press `Shift + F10`

Alternatively, use the command line:

```bash
./gradlew installDebug
```

## How It Works

### Language Switching

The app uses the AndroidX AppCompat library's per-app language preferences API:

```kotlin
private fun changeLanguage(language: String) {
  val locale = Locale(language)
  AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
}
```

This approach:
- Works on Android 8.1+ (API 27)
- Persists language preference across app restarts
- Integrates with Android 13+ system language settings
- Doesn't require app restart to take effect

### Getting Current Locale in Compose

The project includes a utility function to get the current locale in Compose:

```kotlin
@Composable
@ReadOnlyComposable
fun currentLocale(): Locale =
  ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    ?: LocaleListCompat.getAdjustedDefault()[0]!!
```

### Locale Configuration

The build configuration defines supported locales:

```kotlin
val SUPPORTED_LOCALES = setOf(
  "en",
  "en-rUS",
  "vi",
  "vi-rVN",
)
```

These are automatically filtered during the build process and available via `BuildConfig.SUPPORTED_LANGUAGE_CODES`.

## Adding New Languages

To add support for a new language:

1. **Add locale to build configuration** in `app/build.gradle.kts`:
   ```kotlin
   val SUPPORTED_LOCALES = setOf(
     "en",
     "en-rUS",
     "vi",
     "vi-rVN",
     "fr",        // Add new locale
     "fr-rFR",
   )
   ```

2. **Create resource directory**: `app/src/main/res/values-{language}/`
   - For French: `values-fr`
   - For French (France): `values-fr-rFR`

3. **Add translated strings** in `strings.xml`:
   ```xml
   <resources>
       <string name="app_name">Your Translated App Name</string>
       <string name="current_locale_language_country">...</string>
   </resources>
   ```

4. **Rebuild the project** to generate updated locale configuration

## Key Features Implementation

### Edge-to-Edge Display

The app uses modern edge-to-edge display with proper window insets handling:

```kotlin
enableEdgeToEdge()
```

### Material 3 Theming

Implements Material You design with dynamic color support (disabled for consistency):

```kotlin
JetpackComposeLocalizationTheme(dynamicColor = false) {
  // Content
}
```

### Lifecycle Awareness

The app logs lifecycle events for debugging and monitoring:

```kotlin
lifecycle.eventFlow
  .onEach { Log.d("MainActivity", ">>> lifecycle event: $it") }
  .launchIn(lifecycleScope)
```

## Testing

### Running Unit Tests

```bash
./gradlew test
```

### Running Instrumentation Tests

```bash
./gradlew connectedAndroidTest
```

## Building for Release

```bash
./gradlew assembleRelease
```

The release APK will be generated in `app/build/outputs/apk/release/`

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Guidelines

1. Follow Kotlin coding conventions
2. Maintain existing code style
3. Add tests for new features
4. Update documentation as needed
5. Ensure all tests pass before submitting PR

## License

This project is available for educational and demonstration purposes. Please check the repository for specific license terms.

## Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Uses [AndroidX AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat) for language preferences
- Follows [Material Design 3](https://m3.material.io/) guidelines

## Resources

- [Android Localization Guide](https://developer.android.com/guide/topics/resources/localization)
- [Per-app language preferences](https://developer.android.com/guide/topics/resources/app-languages)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Material Design 3](https://m3.material.io/)

## Author

**hoc081098**

- GitHub: [@hoc081098](https://github.com/hoc081098)

## Support

If you find this project helpful, please consider giving it a ⭐️ on GitHub!

For issues, questions, or suggestions, please open an issue on the [GitHub repository](https://github.com/hoc081098/Jetpack-Compose-Localization/issues).
