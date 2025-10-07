package com.hoc081098.jetpackcomposelocalization.data

import retrofit2.http.GET

interface ApiService {
  @GET("get")
  suspend fun getLocalizedData(): Map<String, Any>
}
