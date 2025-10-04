plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
}

kotlin {
  jvmToolchain {
    version = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.AZUL
  }
}

val SUPPORTED_LOCALES = setOf(
  "en",
  "en-rUS",
  "vi",
  "vi-rVN",
)

val SUPPORTED_LANGUAGE_CODES = SUPPORTED_LOCALES
  .mapTo(LinkedHashSet()) { it.substringBefore("-r") }
  .joinToString(separator = ",", prefix = "\"", postfix = "\"")

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
    localeFilters += SUPPORTED_LOCALES
  }

  buildTypes {
    debug {
      buildConfigField(
        type = "String",
        name = "SUPPORTED_LANGUAGE_CODES",
        value = SUPPORTED_LANGUAGE_CODES,
      )
    }

    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      buildConfigField(
        type = "String",
        name = "SUPPORTED_LANGUAGE_CODES",
        value = SUPPORTED_LANGUAGE_CODES,
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
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}