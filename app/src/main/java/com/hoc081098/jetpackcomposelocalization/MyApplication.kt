package com.hoc081098.jetpackcomposelocalization

import android.app.Application
import com.hoc081098.jetpackcomposelocalization.data.NetworkServiceLocator

class MyApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    NetworkServiceLocator.init(this)
  }
}
