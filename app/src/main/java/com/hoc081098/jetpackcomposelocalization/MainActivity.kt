package com.hoc081098.jetpackcomposelocalization

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.eventFlow
import androidx.lifecycle.lifecycleScope
import com.hoc081098.jetpackcomposelocalization.ui.locale.currentLocale
import com.hoc081098.jetpackcomposelocalization.ui.theme.JetpackComposeLocalizationTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      JetpackComposeLocalizationTheme(dynamicColor = false) {
        MainScreen(modifier = Modifier.fillMaxSize())
      }
    }

    lifecycle.eventFlow
      .onEach { Log.d("MainActivity", ">>> $this -> lifecycle event: $it") }
      .launchIn(lifecycleScope)
  }
}

private const val FOLLOW_SYSTEM = "Language#FollowSystem"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(modifier: Modifier = Modifier) {
  val locale = currentLocale()

  // Check if we're following the system locale
  val isFollowingSystem = AppCompatDelegate.getApplicationLocales().isEmpty

  val supportedLanguages = BuildConfig.SUPPORTED_LANGUAGE_CODES
    .split(",")
    .sorted()

  Scaffold(
    modifier = modifier,
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .consumeWindowInsets(innerPadding),
      contentAlignment = Alignment.Center,
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Header(locale = locale, isFollowingSystem = isFollowingSystem)

        Spacer(modifier = Modifier.height(32.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          LanguageOption(
            language = FOLLOW_SYSTEM,
            isCurrent = isFollowingSystem,
          )
          supportedLanguages.fastForEach { language ->
            LanguageOption(
              language = language,
              isCurrent = !isFollowingSystem && locale.language == language,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LanguageOption(language: String, isCurrent: Boolean, modifier: Modifier = Modifier) {
  Text(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clip(MaterialTheme.shapes.medium)
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outline,
        shape = MaterialTheme.shapes.medium,
      )
      .clickable(onClick = { changeLanguage(language) })
      .padding(16.dp),
    text = buildString {
      append(if (language == FOLLOW_SYSTEM) stringResource(R.string.follow_system) else language)
      append(if (isCurrent) " (current language)" else "")
    },
    style = if (isCurrent) {
      MaterialTheme.typography.titleLarge.copy(
        color = MaterialTheme.colorScheme.primary,
      )
    } else {
      MaterialTheme.typography.titleMedium
    },
  )
}

@Composable
private fun Header(locale: Locale, isFollowingSystem: Boolean, modifier: Modifier = Modifier) {
  Text(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    text = buildString {
      append(
        stringResource(
          R.string.current_locale_language_country,
          locale,
          locale.language,
          locale.country,
          locale.toLanguageTag(),
        )
      )
      append(if (isFollowingSystem) " (following system)" else "")
    },
    style = MaterialTheme.typography.titleLarge,
    textAlign = TextAlign.Center,
  )
}

private fun changeLanguage(language: String) {
  if (language == FOLLOW_SYSTEM) {
    Log.d("MainActivity", ">>> setApplicationLocales: to system default (empty)")

    // Set empty locale list to follow system
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
  } else {
    val locale = Locale(language)
      .also { Log.d("MainActivity", ">>> setApplicationLocales: to $it") }

    AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
  }

  Log.d("MainActivity", ">>> getApplicationLocales: ${AppCompatDelegate.getApplicationLocales()}")
}
