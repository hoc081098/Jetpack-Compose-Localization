package com.hoc081098.jetpackcomposelocalization.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

@Composable
@ReadOnlyComposable
fun currentLocale(): Locale =
  ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    ?: LocaleListCompat.getAdjustedDefault()[0]!!
