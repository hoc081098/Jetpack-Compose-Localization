package com.hoc081098.jetpackcomposelocalization.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

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
fun currentLocale(): Locale =
  ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    ?: LocaleListCompat.getAdjustedDefault()[0]!!
