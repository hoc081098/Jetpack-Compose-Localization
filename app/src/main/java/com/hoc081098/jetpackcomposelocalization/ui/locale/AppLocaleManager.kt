package com.hoc081098.jetpackcomposelocalization.ui.locale

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.hoc081098.jetpackcomposelocalization.BuildConfig
import com.hoc081098.jetpackcomposelocalization.R
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

    data class Language(val locale: Locale) : AppLocale {
      fun isTheSameLanguage(other: Locale): Boolean =
        locale.language == other.language
    }
  }

  @Stable
  fun isCurrentLanguage(locale: AppLocale): Boolean =
    when (locale) {
      AppLocale.FollowSystem -> isFollowingSystem
      is AppLocale.Language -> !isFollowingSystem && locale.isTheSameLanguage(currentLocale)
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
        .map {
          AppLocaleState.AppLocale.Language(
            locale = Locale
              .forLanguageTag(it)
              .stripExtensions()
          )
        }
        .distinctBy { it.locale.language }
    )
  }

  /**
   * Recomposition trigger tied to application locales.
   *
   * Why this exists:
   * - Calling [AppCompatDelegate.setApplicationLocales] may be a no-op (e.g., target equals current)
   *   or its effects may not be observable immediately via [currentLocale]/[AppCompatDelegate.getApplicationLocales].
   * - When that happens, Compose wouldn't naturally recompose, so the UI wouldn't reflect a (potential)
   *   locale change.
   *
   * How it works:
   * - We read this state in [rememberAppLocaleState] to create a dependency.
   * - After any [changeLanguage] call, we write to this state using [neverEqualPolicy], which
   *   always invalidates and forces recomposition regardless of the written value.
   *
   * Notes:
   * - This is not a source of truth for the locale; it's just an invalidation signal.
   * - Actual locale data still comes from [currentLocale].
   */
  private val applicationLocalesSignalState = mutableStateOf(
    value = Unit,
    policy = neverEqualPolicy()
  )

  @Composable
  fun rememberAppLocaleState(): AppLocaleState {
    check(LocalActivity.current is AppCompatActivity) { "Must be called from an AppCompatActivity" }

    val locale = currentLocale()

    applicationLocalesSignalState.value // <-- value read to getApplicationLocales if needed
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

    val target = when (locale) {
      AppLocaleState.AppLocale.FollowSystem ->
        // Set empty locale list to follow system
        LocaleListCompat.getEmptyLocaleList()

      is AppLocaleState.AppLocale.Language ->
        LocaleListCompat.create(locale.locale)
    }
    AppCompatDelegate.setApplicationLocales(target)
    applicationLocalesSignalState.value = Unit // <-- trigger recomposition

    Log.d(LOG_TAG, ">>> getApplicationLocales: ${AppCompatDelegate.getApplicationLocales()}")
  }

  companion object {
    private val LOG_TAG = AppLocaleManager::class.java.simpleName
  }
}

private val Locale.localizedDisplayName: String
  inline get() = getDisplayName(this)
    .replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(this)
      else it.toString()
    }

@Composable
fun AppLocaleState.AppLocale.localizedDisplayName(): String =
  when (this) {
    AppLocaleState.AppLocale.FollowSystem -> stringResource(R.string.follow_system)
    is AppLocaleState.AppLocale.Language -> remember(locale) { locale.localizedDisplayName }
  }