package com.hoc081098.jetpackcomposelocalization.data

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 * An OkHttp interceptor that adds the Accept-Language header to HTTP requests.
 *
 * This interceptor uses the provided locale to set the Accept-Language header,
 * allowing servers to respond with content in the appropriate language.
 *
 * @param localeProvider A function that provides the current locale.
 *                       This allows the locale to be updated dynamically.
 */
class AcceptedLanguageInterceptor(
  private val localeProvider: LocaleProvider,
) : Interceptor {

  fun interface LocaleProvider {
    fun provide(): Locale
  }

  /**
   * Intercepts the HTTP request and adds the Accept-Language header.
   *
   * @param chain The interceptor chain.
   * @return The response from the next interceptor or the network.
   */
  override fun intercept(chain: Interceptor.Chain): Response {
    val locale = localeProvider.provide()
    val request = chain.request().newBuilder()
      .addHeader("Accept-Language", locale.toLanguageTag())
      .build()
    return chain.proceed(request)
  }
}
