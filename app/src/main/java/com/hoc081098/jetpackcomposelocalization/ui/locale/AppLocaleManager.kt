package com.hoc081098.jetpackcomposelocalization.ui.locale

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.core.os.LocaleListCompat
import com.hoc081098.jetpackcomposelocalization.BuildConfig
import java.util.Locale

@Immutable
data class AppLocaleState(
  val currentLocale: Locale,
  val isFollowingSystem: Boolean,
  val supportedLanguages: List<String>,
) {
  @Stable
  fun isCurrentLanguage(language: String): Boolean =
    if (language == AppLocaleManager.FOLLOW_SYSTEM) {
      isFollowingSystem
    } else {
      !isFollowingSystem && currentLocale.language == language
    }
}

@Stable
class AppLocaleManager {
  private val supportedLanguages = buildList {
    add(FOLLOW_SYSTEM)

    // Split the comma-separated tags, trim whitespace, drop empties, and sort.
    addAll(
      BuildConfig.SUPPORTED_LANGUAGE_CODES
        .split(',')
        .mapNotNull { v -> v.trim().takeIf { it.isNotEmpty() } }
        .sorted()
    )
  }

  @Composable
  fun rememberAppLocaleState(): AppLocaleState {
    val locale = currentLocale()

    // Set empty locale list to follow system
    val isFollowingSystem = AppCompatDelegate.getApplicationLocales().isEmpty

    return remember(locale, isFollowingSystem) {
      AppLocaleState(
        currentLocale = locale,
        isFollowingSystem = isFollowingSystem,
        supportedLanguages = supportedLanguages,
      )
    }
  }

  fun changeLanguage(language: String) {
    Log.d(LOG_TAG, ">>> setApplicationLocales: to $language")

    if (language == FOLLOW_SYSTEM) {
      // Set empty locale list to follow system
      AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    } else {
      AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale(language)))
    }

    Log.d(LOG_TAG, ">>> getApplicationLocales: ${AppCompatDelegate.getApplicationLocales()}")
  }

  companion object {
    private val LOG_TAG = AppLocaleManager::class.java.simpleName

    const val FOLLOW_SYSTEM = "Language#FollowSystem"
  }
}

val Locale.localizedDisplayName: String
  inline get() = getDisplayName(this)
    .replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(this)
      else it.toString()
    }