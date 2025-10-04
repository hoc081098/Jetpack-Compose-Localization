# Jetpack Compose Localization

A production-ready Android application demonstrating advanced localization techniques including runtime language switching, locale-aware datetime formatting with ICU skeletons, and intelligent caching—all built with Jetpack Compose.

## Overview

This project showcases **best practices** for implementing localization in modern Android applications. Beyond basic language switching, it demonstrates production-ready patterns including:
- ⚡ **Zero-restart language switching** using AndroidX AppCompat's per-app language preferences API
- 🕐 **Locale-aware datetime formatting** with ICU skeleton patterns and intelligent caching
- 🎨 **Material 3** design with edge-to-edge display
- 🔄 **Follow system locale** option for seamless integration with device settings

## Features

- **🚀 Runtime Language Switching**: Change app language instantly without restarting
- **⚡ DateTimeFormatter Caching**: Production-ready cache system for optimal performance
- **🌍 ICU Skeleton Support**: Locale-aware date/time formatting using ICU skeleton patterns
- **📱 Modern Jetpack Compose UI**: Pure Compose implementation with Material 3
- **🔀 Follow System Option**: Seamlessly follow device locale settings
- **🎯 Per-App Language Settings**: Uses AndroidX AppCompat's `setApplicationLocales()` API (API 27+)
- **📊 Live Locale Information**: Real-time display of current locale details

## Supported Languages

- **English** (en, en-US)
- **Vietnamese** (vi, vi-VN)

The app dynamically displays available languages from `BuildConfig` and highlights the currently selected one.

## Quick Start

```bash
git clone https://github.com/hoc081098/Jetpack-Compose-Localization.git
cd Jetpack-Compose-Localization
./gradlew installDebug
```

Run the app and tap on a language to see instant language switching with locale-aware datetime formatting!

## Requirements & Tech Stack

### Core Requirements
- **Min SDK**: API 27 (Android 8.1)
- **Target/Compile SDK**: API 36 (Android 14)
- **Kotlin**: 2.0.21
- **Gradle**: 8.13.0
- **Java**: 11+

### Key Technologies
- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Material Design 3 components
- **AndroidX AppCompat** - Per-app language preferences API
- **AndroidX Lifecycle** - Lifecycle-aware components
- **Java Time API** - Modern date/time handling with ICU patterns

## Project Structure

```
app/src/main/
├── java/com/hoc081098/jetpackcomposelocalization/
│   ├── MainActivity.kt                          # Main activity with language switching
│   └── ui/
│       ├── locale/
│       │   └── currentLocale.kt                # Composable to get current locale
│       ├── text/
│       │   └── DateTimeFormatterCache.kt       # 🔥 Intelligent formatter caching
│       ├── time/
│       │   └── Instant.kt                      # Extension functions for time formatting
│       └── theme/
│           ├── Color.kt                        # Color definitions
│           ├── Theme.kt                        # Material Theme configuration
│           └── Type.kt                         # Typography definitions
└── res/
    ├── values/                                 # Default resources (English)
    │   └── strings.xml
    └── values-vi/                              # Vietnamese resources
        └── strings.xml
```

## Setup and Installation

### Prerequisites

- Android Studio (latest version)
- JDK 11 or higher
- Android emulator or physical device

### Build & Run

```bash
# Clone the repository
git clone https://github.com/hoc081098/Jetpack-Compose-Localization.git
cd Jetpack-Compose-Localization

# Build and install
./gradlew build
./gradlew installDebug
```

**Or** open in Android Studio → Sync → Run (Shift + F10)

## How It Works

### Language Switching

The app uses AndroidX AppCompat's per-app language preferences API with support for "Follow System" mode:

```kotlin
private fun changeLanguage(language: String) {
  if (language == FOLLOW_SYSTEM) {
    // Set empty locale list to follow system
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
  } else {
    val locale = Locale(language)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
  }
}
```

**Key benefits:**
- ✅ Works on Android 8.1+ (API 27)
- ✅ Persists preference across app restarts
- ✅ Integrates with Android 13+ system language settings
- ✅ No app restart required
- ✅ Seamlessly follows system locale when user prefers

### Getting Current Locale in Compose

Utility function to reactively observe locale changes in Compose:

```kotlin
@Composable
@ReadOnlyComposable
fun currentLocale(): Locale =
  ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    ?: LocaleListCompat.getAdjustedDefault()[0]!!
```

### 🔥 DateTimeFormatter Caching (Production-Ready)

One of the **coolest features** is the intelligent `DateTimeFormatterCache` that provides:
- **Thread-safe caching** of immutable DateTimeFormatter instances
- **ICU skeleton support** for locale-aware patterns (e.g., "yMd", "jm", "yMMMdjm")
- **Automatic 12h/24h normalization** based on user preference
- **Localized styles** (SHORT, MEDIUM, LONG, FULL)
- **Per-locale cache management** for optimal memory usage

#### Using ICU Skeletons

```kotlin
val formatter = DateTimeFormatterCache.getFormatterFromSkeleton(
  locale = locale,
  skeleton = "yMMMddHmss"  // Year, abbreviated month, day, hours, minutes, seconds
)

val formattedTime = formatter.formatInstant(Instant.now(), ZoneId.systemDefault())
// Example outputs:
// English: "Jan 15, 2024, 2:30:45 PM"
// Vietnamese: "15 thg 1, 2024, 14:30:45"
```

**Why ICU skeletons?**
- 🌍 Automatically adapt to locale conventions
- 🎯 More flexible than rigid patterns
- 🔒 Safer than `DateTimeFormatter.ofPattern()` for user-facing text
- ⚡ Cached for optimal performance

#### Cache Management

```kotlin
// Clear cache when locale changes (optional, for memory management)
DateTimeFormatterCache.clear()

// Remove formatters for specific locale
DateTimeFormatterCache.removeLocale(locale)
```

#### Additional Formatter Options

```kotlin
// Localized date formatter
val dateFormatter = DateTimeFormatterCache.getLocalizedDateFormatter(
  locale = locale,
  dateStyle = FormatStyle.MEDIUM
)

// Localized time formatter
val timeFormatter = DateTimeFormatterCache.getLocalizedTimeFormatter(
  locale = locale,
  timeStyle = FormatStyle.SHORT
)

// Localized date-time formatter
val dateTimeFormatter = DateTimeFormatterCache.getLocalizedDateTimeFormatter(
  locale = locale,
  dateStyle = FormatStyle.MEDIUM,
  timeStyle = FormatStyle.SHORT
)
```

### Time Formatting Extensions

Convenient extension functions for working with `Instant`:

```kotlin
// Format an Instant with a specific zone
val formatted = formatter.formatInstant(instant, zoneId)

// Convert Instant to ZonedDateTime
val zonedDateTime = instant.toZonedDateTime(ZoneId.systemDefault())
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

## Advanced Features

### 🔥 Why DateTimeFormatterCache is Production-Ready

The `DateTimeFormatterCache` implementation demonstrates enterprise-grade patterns:

1. **Thread-Safety**: Uses `ConcurrentHashMap` for safe concurrent access
2. **Immutability**: DateTimeFormatter instances are immutable and thread-safe
3. **Smart Key Generation**: Combines locale + descriptor + flags for precise caching
4. **Memory Management**: Per-locale removal for granular cache control
5. **ICU Skeleton Normalization**: Automatically handles 12h/24h preferences

**Performance benefits:**
- Avoids repeated expensive pattern compilation
- Reduces garbage collection pressure
- Ideal for apps with frequent datetime formatting

**When to clear the cache:**
```kotlin
// Optional: Clear when locale changes
AppCompatDelegate.setApplicationLocales(newLocaleList)
DateTimeFormatterCache.clear()  // Free up memory if needed
```

### Follow System Locale

The app provides a "Follow System" option that:
- Sets empty locale list: `AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())`
- Automatically adopts system locale changes
- Integrates seamlessly with Android 13+ per-app language settings

### BuildConfig Integration

Supported locales are automatically exposed via BuildConfig:

```kotlin
val SUPPORTED_LOCALES = setOf("en", "en-rUS", "vi", "vi-rVN")
// Available at runtime as: BuildConfig.SUPPORTED_LANGUAGE_CODES
```

This enables dynamic UI generation without hardcoding language options.

## Adding New Languages

Adding a new language is straightforward:

**1. Update build configuration** (`app/build.gradle.kts`):
```kotlin
val SUPPORTED_LOCALES = setOf(
  "en", "en-rUS",
  "vi", "vi-rVN",
  "fr", "fr-rFR",  // ← Add new locale
)
```

**2. Create resource directory** `app/src/main/res/values-{lang}/`

**3. Add `strings.xml`** with translated strings:
```xml
<resources>
    <string name="app_name">Votre Nom d\'App</string>
    <string name="current_locale_language_country">Locale actuelle: %1$s, langue: %2$s, pays: %3$s, languageTag: %4$s</string>
    <string name="follow_system">Suivre le système</string>
    <string name="demo_datetime_formatter">Maintenant c\'est %1s</string>
</resources>
```

**4. Rebuild** → Language appears automatically in the app! ✨

## Key Features Implementation

### Live DateTime Demo

The app includes a live demonstration of locale-aware datetime formatting:

```kotlin
@Composable
private fun DemoDateTimeFormatter(
  locale: Locale,
  modifier: Modifier = Modifier,
  clock: Clock = Clock.systemDefaultZone(),
) {
  val now: Instant = remember(clock) { Instant.now(clock) }
  val timeFormatter = DateTimeFormatterCache.getFormatterFromSkeleton(
    locale = locale,
    skeleton = "yMMMddHmss"
  )

  Text(
    text = stringResource(
      R.string.demo_datetime_formatter,
      timeFormatter.formatInstant(now, clock.zone),
    ),
    style = MaterialTheme.typography.bodyLarge,
  )
}
```

This demonstrates how date/time formatting automatically adapts to the selected locale without any manual formatting logic.

### Edge-to-Edge Display

Modern edge-to-edge display with proper window insets handling:

```kotlin
enableEdgeToEdge()
```

### Material 3 Theming

Implements Material You design (dynamic color disabled for consistency):

```kotlin
JetpackComposeLocalizationTheme(dynamicColor = false) {
  // Content
}
```

### Lifecycle Awareness

The app logs lifecycle events for debugging:

```kotlin
lifecycle.eventFlow
  .onEach { Log.d("MainActivity", ">>> lifecycle event: $it") }
  .launchIn(lifecycleScope)
```

## Testing

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
```

## Building for Release

```bash
./gradlew assembleRelease
# APK output: app/build/outputs/apk/release/
```

## Contributing

Contributions are welcome! Please:
1. Open an issue first for major changes
2. Follow Kotlin conventions
3. Add tests for new features
4. Update documentation
5. Ensure tests pass before submitting PR

## License

Available for educational and demonstration purposes. See repository for license details.

## Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [AndroidX AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat) for per-app language preferences
- [Material Design 3](https://m3.material.io/) guidelines
- [ICU](https://developer.android.com/guide/topics/resources/internationalization) for locale-aware patterns

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
