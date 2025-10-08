plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
}

kotlin {
  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.AZUL
  }
}

object Locales {
  val localeFilters = listOf(
    "en",
    "vi-rVN",
  )

  val supportedLocales: String =
    localeFilters.joinToString(
      separator = ",",
      prefix = "\"",
      postfix = "\""
    ) {
      it.replace(
        oldValue = "-r",
        newValue = "-"
      )
    }
}

android {
  namespace = "com.hoc081098.jetpackcomposelocalization"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.hoc081098.jetpackcomposelocalization"
    minSdk = 27
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  androidResources {
    ignoreAssetsPatterns += listOf(
      "!PublicSuffixDatabase.list", // OkHttp
      "!composepreference.preference.generated.resources",
    )
    generateLocaleConfig = true
    localeFilters += Locales.localeFilters
  }

  buildTypes {
    debug {
      buildConfigField(
        type = "String",
        name = "SUPPORTED_LOCALES",
        value = Locales.supportedLocales,
      )
    }

    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      buildConfigField(
        type = "String",
        name = "SUPPORTED_LOCALES",
        value = Locales.supportedLocales,
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  // compose lifecycle viewmodel
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)

  // Retrofit
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.moshi)

  // Moshi
  implementation(libs.moshi)
  implementation(libs.moshi.kotlin)

  // OkHttp BOM and artifacts
  implementation(platform(libs.okhttp.bom))
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging.interceptor)
}