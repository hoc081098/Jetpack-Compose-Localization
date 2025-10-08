package com.hoc081098.jetpackcomposelocalization.data

import android.app.Application
import androidx.core.app.LocaleManagerCompat
import com.hoc081098.jetpackcomposelocalization.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object NetworkServiceLocator {
  private const val BASE_URL = "https://httpbin.org/"

  private var _application: Application? = null

  fun init(app: Application) {
    _application = app
  }

  private val application: Application
    get() = _application
      ?: throw IllegalStateException("NetworkServiceLocator is not initialized. Call init(app) before using it.")

  private val moshi: Moshi by lazy {
    Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()
  }

  private val localeProvider: AcceptedLanguageInterceptor.LocaleProvider
    get() = AcceptedLanguageInterceptor.LocaleProvider {
      LocaleManagerCompat.getApplicationLocales(application)
        .takeIf { it.size() > 0 }
        ?: LocaleManagerCompat.getSystemLocales(application)
    }

  private val acceptedLanguageInterceptor: AcceptedLanguageInterceptor
    get() = AcceptedLanguageInterceptor(localeProvider)

  private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .connectTimeout(30, TimeUnit.SECONDS)
      .addInterceptor(acceptedLanguageInterceptor)
      .addNetworkInterceptor(
        HttpLoggingInterceptor().apply {
          level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
          } else {
            HttpLoggingInterceptor.Level.NONE
          }
        }
      )
      .build()
  }

  private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }

  val apiService: ApiService by lazy { retrofit.create() }
}
