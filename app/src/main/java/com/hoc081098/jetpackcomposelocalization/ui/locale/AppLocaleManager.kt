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
  val supportedLocales: List<AppLocale>,
) {
  @Immutable
  sealed interface AppLocale {
    data object FollowSystem : AppLocale

    data class Language(val locale: Locale) : AppLocale
  }

  @Stable
  fun isCurrent(locale: AppLocale): Boolean =
    when (locale) {
      AppLocale.FollowSystem -> isFollowingSystem
      is AppLocale.Language -> !isFollowingSystem && currentLocale == locale.locale
    }
}

@Stable
class AppLocaleManager {
  private val supportedLanguages = buildList {
    add(AppLocaleState.AppLocale.FollowSystem)

    addAll(
      BuildConfig.SUPPORTED_LOCALES
        .split(',')
        .sorted()
        .distinct()
        .map { AppLocaleState.AppLocale.Language(locale = Locale.forLanguageTag(it)) }
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
        supportedLocales = supportedLanguages,
      )
    }
  }

  fun changeLanguage(locale: AppLocaleState.AppLocale) {
    Log.d(LOG_TAG, ">>> setApplicationLocales: to $locale")

    when (locale) {
      AppLocaleState.AppLocale.FollowSystem -> {
        // Set empty locale list to follow system
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
      }

      is AppLocaleState.AppLocale.Language -> {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale.locale))
      }
    }

    Log.d(LOG_TAG, ">>> getApplicationLocales: ${AppCompatDelegate.getApplicationLocales()}")
  }

  companion object {
    private val LOG_TAG = AppLocaleManager::class.java.simpleName
  }
}

val Locale.localizedDisplayName: String
  inline get() = getDisplayName(this)
    .replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(this)
      else it.toString()
    }