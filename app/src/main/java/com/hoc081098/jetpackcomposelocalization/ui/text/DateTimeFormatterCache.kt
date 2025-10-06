package com.hoc081098.jetpackcomposelocalization.ui.text

import android.text.format.DateFormat
import androidx.annotation.AnyThread
import androidx.annotation.VisibleForTesting
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache key formats used internally:
 * - For skeleton patterns: `"SKELETON:<pattern>"` such as `"SKELETON:yMd"`, `"SKELETON:jm"`
 * - For date formatting: `"LOCALIZED:DATE:<style>"` such as `"LOCALIZED:DATE:SHORT"`
 * - For time formatting: `"LOCALIZED:TIME:<style>"` such as `"LOCALIZED:TIME:MEDIUM"`
 * - For datetime formatting: `"LOCALIZED:DATETIME:<date_style>_<time_style>"`
 * - For custom patterns: `"PATTERN:<custom_pattern>"` such as `"PATTERN:yyyy-MM-dd"`
 *
 * Additional parameters:
 * - Hour format preference: `is24Hour=true/false/null` (null means system default)
 *
 * Complete key example: `"en-US|SKELETON:yMd|true"`
 */
private object Keys {
  // Constants for building cache keys
  const val KEY_SEPARATOR = "|"
  const val DESC_SKELETON = "SKELETON:"
  const val DESC_LOCALIZED_DATE = "LOCALIZED:DATE:"
  const val DESC_LOCALIZED_TIME = "LOCALIZED:TIME:"
  const val DESC_LOCALIZED_DATETIME = "LOCALIZED:DATETIME:"
  const val DESC_PATTERN = "PATTERN:"

  /**
   * Constructs a unique identifier for caching formatters based on locale and formatting options.
   */
  fun buildKey(locale: Locale, descriptor: String, is24Hour: Boolean? = null): String =
    buildString {
      append(locale.toLanguageTag())
      append(KEY_SEPARATOR)
      append(descriptor)
      append(KEY_SEPARATOR)
      append(is24Hour)
    }
}

/**
 * Thread-safe caching system for DateTimeFormatter instances to optimize performance.
 *
 * This cache stores formatters using composite identifiers that combine locale information
 * with formatting specifications. Since DateTimeFormatter objects are immutable and
 * thread-safe, they can be safely cached and reused across multiple threads.
 *
 * Important: Call clear() when the application's effective locale changes to ensure
 * cached formatters reflect the new locale settings.
 */
@AnyThread
object DateTimeFormatterCache {
  private val cache = ConcurrentHashMap<String, DateTimeFormatter>()

  /**
   * Creates a DateTimeFormatter using ICU skeleton pattern notation for flexible date/time formatting.
   * Automatically adjusts time format symbols based on the 24-hour preference when specified.
   *
   * @param locale The target locale for localized formatting
   * @param skeleton ICU skeleton pattern defining which date/time fields to include
   * @param is24Hour Optional preference for 24-hour time display format
   * @return Thread-safe DateTimeFormatter instance optimized for the specified parameters
   */
  fun getFormatterFromSkeleton(
    locale: Locale,
    skeleton: String,
    is24Hour: Boolean? = null
  ): DateTimeFormatter {
    val key = Keys.buildKey(
      locale = locale,
      descriptor = "${Keys.DESC_SKELETON}$skeleton",
      is24Hour = is24Hour,
    )
    return cache.computeIfAbsent(key) {
      val effectiveSkeleton = normalizeSkeletonFor24hPreference(skeleton, is24Hour)
      val bestPattern = DateFormat.getBestDateTimePattern(locale, effectiveSkeleton)
      DateTimeFormatter.ofPattern(bestPattern, locale)
    }
  }

  /**
   * Provides a localized formatter specifically for date display using predefined styles.
   *
   * @param locale Target locale for cultural date formatting preferences
   * @param dateStyle Predefined formatting style (SHORT, MEDIUM, LONG, FULL)
   * @return DateTimeFormatter configured for date-only output in the specified style
   */
  fun getLocalizedDateFormatter(locale: Locale, dateStyle: FormatStyle): DateTimeFormatter {
    val key = Keys.buildKey(
      locale = locale,
      descriptor = "${Keys.DESC_LOCALIZED_DATE}$dateStyle",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofLocalizedDate(dateStyle)
        .withLocale(locale)
    }
  }

  /**
   * Provides a localized formatter specifically for time display using predefined styles.
   *
   * @param locale Target locale for cultural time formatting preferences
   * @param timeStyle Predefined formatting style (SHORT, MEDIUM, LONG, FULL)
   * @return DateTimeFormatter configured for time-only output in the specified style
   */
  fun getLocalizedTimeFormatter(locale: Locale, timeStyle: FormatStyle): DateTimeFormatter {
    val key = Keys.buildKey(
      locale = locale,
      descriptor = "${Keys.DESC_LOCALIZED_TIME}$timeStyle",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofLocalizedTime(timeStyle)
        .withLocale(locale)
    }
  }

  /**
   * Provides a localized formatter for combined date and time display using predefined styles.
   *
   * @param locale Target locale for cultural formatting preferences
   * @param dateStyle Predefined date formatting style (SHORT, MEDIUM, LONG, FULL)
   * @param timeStyle Predefined time formatting style (SHORT, MEDIUM, LONG, FULL)
   * @return DateTimeFormatter configured for combined date-time output
   */
  fun getLocalizedDateTimeFormatter(
    locale: Locale,
    dateStyle: FormatStyle,
    timeStyle: FormatStyle
  ): DateTimeFormatter {
    val key = Keys.buildKey(
      locale = locale,
      descriptor = "${Keys.DESC_LOCALIZED_DATETIME}${dateStyle.name}_${timeStyle.name}",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle)
        .withLocale(locale)
    }
  }

  /**
   * Creates a formatter from a custom date-time pattern string.
   *
   * For user-facing content, prefer skeleton-based or localized formatters for better
   * internationalization. Custom patterns are most suitable for API communications
   * or technical formats (often used with Locale.ROOT for consistency).
   *
   * @param locale Target locale for formatting context
   * @param pattern Custom date-time pattern following Java DateTimeFormatter syntax
   * @return DateTimeFormatter based on the specified pattern and locale
   */
  fun getPatternFormatter(locale: Locale, pattern: String): DateTimeFormatter {
    val key = Keys.buildKey(
      locale = locale,
      descriptor = "${Keys.DESC_PATTERN}$pattern",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofPattern(pattern, locale)
    }
  }

  // ----------------------
  // Cache maintenance operations
  // ----------------------

  /**
   * Removes all cached formatter instances.
   *
   * Execute this when the application's locale configuration changes to ensure
   * all subsequent formatting operations use the updated locale settings.
   */
  fun clear() = cache.clear()

  /**
   * Removes cached formatters associated with a specific locale.
   *
   * @param locale The locale whose cached formatters should be invalidated
   */
  fun removeLocale(locale: Locale) {
    val tag = locale.toLanguageTag() + Keys.KEY_SEPARATOR
    synchronized(cache) {
      cache.keys
        .filter { it.startsWith(tag) }
        .forEach { cache.remove(it) }
    }
  }

  /**
   * Adjusts skeleton patterns to enforce specific hour format preferences.
   */
  private fun normalizeSkeletonFor24hPreference(skeleton: String, is24Hour: Boolean?): String {
    // When hour format is explicitly specified, replace flexible 'j' with appropriate symbol
    if (is24Hour == null) return skeleton

    // Convert to 24-hour format (H) or 12-hour format (h) based on preference
    return if (is24Hour) {
      skeleton.replace('j', 'H')
    } else {
      skeleton.replace('j', 'h')
    }
  }

  @VisibleForTesting
  internal val size get() = cache.size

  override fun toString() = "DateTimeFormatterCache(size=${cache.size})"
}
