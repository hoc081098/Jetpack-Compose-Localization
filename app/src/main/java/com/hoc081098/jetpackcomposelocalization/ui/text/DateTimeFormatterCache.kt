package com.hoc081098.jetpackcomposelocalization.ui.text

import android.text.format.DateFormat
import androidx.annotation.AnyThread
import androidx.annotation.VisibleForTesting
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Global cache of DateTimeFormatter instances.
 *
 * - Keys are formed from locale + descriptor (skeleton, localized style, explicit pattern) + optional flags.
 * - DateTimeFormatter is immutable and thread-safe -> safe to cache and reuse.
 * - Call clear() when the effective app locale changes (e.g. AppCompatDelegate.setApplicationLocales).
 *
 * Descriptors:
 * - `"SKELETON:<skeleton>"` e.g. `"SKELETON:yMd", "SKELETON:jm", "SKELETON:yMMMd"`
 * - `"LOCALIZED:DATE:<FormatStyle>"` e.g. `"LOCALIZED:DATE:SHORT"`
 * - `"LOCALIZED:TIME:<FormatStyle>"` e.g. `"LOCALIZED:TIME:SHORT"`
 * - `"LOCALIZED:DATETIME:<DateStyle>_<TimeStyle>"` e.g. `"LOCALIZED:DATETIME:SHORT_SHORT"`
 * - `"PATTERN:<pattern>"` e.g. `"PATTERN:dd/MM/uuuu"`
 *
 * Optional flags:
 * - `is24Hour=true/false/null` (null=unspecified)
 *
 * Example key: `"en-US|SKELETON:yMd|true"`
 */
@AnyThread
object DateTimeFormatterCache {
  private val cache = ConcurrentHashMap<String, DateTimeFormatter>()

  // Key and descriptor constants
  private const val KEY_SEPARATOR = "|"
  private const val DESC_SKELETON = "SKELETON:"
  private const val DESC_LOCALIZED_DATE = "LOCALIZED:DATE:"
  private const val DESC_LOCALIZED_TIME = "LOCALIZED:TIME:"
  private const val DESC_LOCALIZED_DATETIME = "LOCALIZED:DATETIME:"
  private const val DESC_PATTERN = "PATTERN:"

  @VisibleForTesting
  internal val size get() = cache.size

  override fun toString() = "DateTimeFormatterCache(size=${cache.size})"

  /**
   * Create a unique cache key from locale + descriptor + optional flags.
   */
  private fun getKey(locale: Locale, descriptor: String, is24Hour: Boolean? = null): String = buildString {
    append(locale.toLanguageTag())
    append(KEY_SEPARATOR)
    append(descriptor)
    append(KEY_SEPARATOR)
    append(is24Hour)
  }

  // ----------------------
  // Public convenience APIs
  // ----------------------

  /**
   * Returns a DateTimeFormatter derived from an ICU skeleton (e.g., "yMd", "jm", "yMMMdjm").
   * Honors [is24Hour] preference by normalizing skeleton time fields when provided.
   *
   * @param locale target Locale for formatting.
   * @param skeleton ICU skeleton describing the fields to include.
   * @param is24Hour optional 24-hour preference to influence time symbols.
   * @return an immutable, thread-safe DateTimeFormatter.
   */
  fun getFormatterFromSkeleton(locale: Locale, skeleton: String, is24Hour: Boolean? = null): DateTimeFormatter {
    val key = getKey(
      locale = locale,
      descriptor = "$DESC_SKELETON$skeleton",
      is24Hour = is24Hour,
    )
    return cache.computeIfAbsent(key) {
      val effectiveSkeleton = normalizeSkeletonFor24hPreference(skeleton, is24Hour)
      val bestPattern = DateFormat.getBestDateTimePattern(locale, effectiveSkeleton)
      DateTimeFormatter.ofPattern(bestPattern, locale)
    }
  }

  /**
   * Returns a localized date-only formatter for the given style.
   *
   * @param locale target Locale for formatting.
   * @param dateStyle localized date style (SHORT, MEDIUM, LONG, FULL).
   * @return a DateTimeFormatter configured for localized date output.
   */
  fun getLocalizedDateFormatter(locale: Locale, dateStyle: FormatStyle): DateTimeFormatter {
    val key = getKey(
      locale = locale,
      descriptor = "$DESC_LOCALIZED_DATE$dateStyle",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofLocalizedDate(dateStyle)
        .withLocale(locale)
    }
  }

  /**
   * Returns a localized time-only formatter for the given style.
   *
   * @param locale target Locale for formatting.
   * @param timeStyle localized time style (SHORT, MEDIUM, LONG, FULL).
   * @return a DateTimeFormatter configured for localized time output.
   */
  fun getLocalizedTimeFormatter(locale: Locale, timeStyle: FormatStyle): DateTimeFormatter {
    val key = getKey(
      locale = locale,
      descriptor = "$DESC_LOCALIZED_TIME$timeStyle",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofLocalizedTime(timeStyle)
        .withLocale(locale)
    }
  }

  /**
   * Returns a localized date-time formatter for the given date and time styles.
   *
   * @param locale target Locale for formatting.
   * @param dateStyle localized date style (SHORT, MEDIUM, LONG, FULL).
   * @param timeStyle localized time style (SHORT, MEDIUM, LONG, FULL).
   * @return a DateTimeFormatter configured for localized date-time output.
   */
  fun getLocalizedDateTimeFormatter(locale: Locale, dateStyle: FormatStyle, timeStyle: FormatStyle): DateTimeFormatter {
    val key = getKey(
      locale = locale,
      descriptor = "$DESC_LOCALIZED_DATETIME${dateStyle.name}_${timeStyle.name}",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle)
        .withLocale(locale)
    }
  }

  /**
   * Returns a formatter from an explicit pattern.
   *
   * Prefer skeletons or localized styles for UI-facing text. Use explicit patterns mainly for
   * protocol/contract formats (often with Locale.ROOT).
   *
   * @param locale target Locale for formatting.
   * @param pattern date-time pattern (java.time format syntax).
   * @return a DateTimeFormatter for the provided pattern and locale.
   */
  // TODO(Localization): Remove all `DateTimeFormatter.ofPattern`s
  @Deprecated("Localization: prefer skeletons or localized styles for UI")
  fun getPatternFormatter(locale: Locale, pattern: String): DateTimeFormatter {
    val key = getKey(
      locale = locale,
      descriptor = "$DESC_PATTERN$pattern",
    )
    return cache.computeIfAbsent(key) {
      DateTimeFormatter.ofPattern(pattern, locale)
    }
  }

  // ----------------------
  // Cache management
  // ----------------------

  /**
   * Clears the entire formatter cache.
   *
   * Call when the effective app locale changes (e.g., after setting application locales).
   */
  fun clear() = cache.clear()

  /**
   * Removes all cached formatters for the given locale.
   *
   * @param locale the locale whose cached entries should be removed.
   */
  fun removeLocale(locale: Locale) {
    val tag = locale.toLanguageTag() + KEY_SEPARATOR
    synchronized(cache) {
      cache.keys
        .filter { it.startsWith(tag) }
        .forEach { cache.remove(it) }
    }
  }

  // ----------------------
  // Internal helpers
  // ----------------------

  private fun normalizeSkeletonFor24hPreference(skeleton: String, is24Hour: Boolean?): String {
    // If developer asked to enforce 24h or 12h, prefer skeleton letters H/h or use 'j' otherwise.
    if (is24Hour == null) return skeleton

    // Replace 'j' with 'H' for forced 24h; replace 'j' with 'h' for forced 12h if present.
    return if (is24Hour) {
      skeleton.replace('j', 'H')
    } else {
      skeleton.replace('j', 'h')
    }
  }
}
