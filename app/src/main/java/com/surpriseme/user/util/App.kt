package com.surpriseme.user.util

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
      ConnectionManger.instantiateWebSocket()
    }

}