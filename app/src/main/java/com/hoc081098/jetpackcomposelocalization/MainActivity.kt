package com.hoc081098.jetpackcomposelocalization

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.eventFlow
import androidx.lifecycle.lifecycleScope
import com.hoc081098.jetpackcomposelocalization.ui.locale.AppLocaleManager
import com.hoc081098.jetpackcomposelocalization.ui.locale.localizedDisplayName
import com.hoc081098.jetpackcomposelocalization.ui.text.DateTimeFormatterCache
import com.hoc081098.jetpackcomposelocalization.ui.theme.JetpackComposeLocalizationTheme
import com.hoc081098.jetpackcomposelocalization.ui.time.formatInstant
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Clock
import java.time.Instant
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(modifier: Modifier = Modifier) {
  val appLocaleManager = remember { AppLocaleManager() }
  val appLocaleState = appLocaleManager.rememberAppLocaleState()

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
        Header(
          locale = appLocaleState.currentLocale,
          isFollowingSystem = appLocaleState.isFollowingSystem
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          appLocaleState.supportedLanguages.fastForEach { language ->
            LanguageOption(
              language = language,
              isCurrent = appLocaleState.isCurrentLanguage(language),
              changeLanguage = appLocaleManager::changeLanguage,
            )
          }
          DemoDateTimeFormatter(locale = appLocaleState.currentLocale)
        }
      }
    }
  }
}

@Composable
private fun LanguageOption(
  language: String,
  isCurrent: Boolean,
  changeLanguage: (language: String) -> Unit,
  modifier: Modifier = Modifier
) {

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
      append(
        if (language == AppLocaleManager.FOLLOW_SYSTEM) stringResource(R.string.follow_system)
        else remember(language) { Locale(language).localizedDisplayName }
      )
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
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    text = stringResource(
      R.string.demo_datetime_formatter,
      timeFormatter.formatInstant(now, clock.zone),
    ),
    style = MaterialTheme.typography.bodyLarge,
    textAlign = TextAlign.Center,
  )
}
