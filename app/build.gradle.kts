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
  val supportedLocales: Set<String> = setOf(
    "en",
    "en-rUS",
    "vi",
    "vi-rVN",
  )

  val supportedLanguageCodes: String = supportedLocales
    .mapTo(LinkedHashSet()) { it.substringBefore("-r") }
    .joinToString(separator = ",", prefix = "\"", postfix = "\"")
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
    localeFilters += Locales.supportedLocales
  }

  buildTypes {
    debug {
      buildConfigField(
        type = "String",
        name = "SUPPORTED_LANGUAGE_CODES",
        value = Locales.supportedLanguageCodes,
      )
    }

    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      buildConfigField(
        type = "String",
        name = "SUPPORTED_LANGUAGE_CODES",
        value = Locales.supportedLanguageCodes,
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
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
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
  val retrofitVersion = "2.11.0"
  implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
  implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

  // Moshi
  val moshiVersion = "1.15.1"
  implementation("com.squareup.moshi:moshi:$moshiVersion")
  implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")

  // define a BOM and its version
  implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
  // define any required OkHttp artifacts without version
  implementation("com.squareup.okhttp3:okhttp")
  implementation("com.squareup.okhttp3:logging-interceptor")
}