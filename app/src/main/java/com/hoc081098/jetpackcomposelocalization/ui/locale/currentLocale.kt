package com.hoc081098.jetpackcomposelocalization.ui.locale

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.hoc081098.jetpackcomposelocalization.BuildConfig
import java.util.Locale

private const val LOG_TAG = "currentLocale"

/**
 * Returns the current effective [Locale] for Compose content.
 *
 * This reads [LocalConfiguration] and returns the first locale from it. If unavailable,
 * it falls back to [LocaleListCompat.getAdjustedDefault]'s first locale.
 *
 * - Marked as [ReadOnlyComposable] because it only reads ambient state.
 * - Use for localized formatting, resources, and layout direction decisions.
 */
@Composable
@ReadOnlyComposable
fun currentLocale(): Locale {
  if (BuildConfig.DEBUG) {
    // Log the current locales for debugging purposes
    val localesFromConfig = ConfigurationCompat.getLocales(LocalConfiguration.current).toLanguageTags()
    val systemLocales = LocaleManagerCompat.getSystemLocales(LocalContext.current).toLanguageTags()
    val defaultLocales = LocaleListCompat.getAdjustedDefault().toLanguageTags()
    val default = Locale.getDefault().toLanguageTag()

    Log.d(LOG_TAG, ">>>     currentLocale [1]: LocalConfiguration  = $localesFromConfig")
    Log.d(LOG_TAG, "    >>> currentLocale [2]: getSystemLocales    = $systemLocales")
    Log.d(LOG_TAG, "    >>> currentLocale [3]: getAdjustedDefault  = $defaultLocales")
    Log.d(LOG_TAG, "    >>> currentLocale [4]: Locale.getDefault() = $default")
  }

  return ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    ?: LocaleListCompat.getAdjustedDefault()[0]!!
}
