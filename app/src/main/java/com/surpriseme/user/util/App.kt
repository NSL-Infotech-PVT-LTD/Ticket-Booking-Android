package com.surpriseme.user.util

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.stripe.android.PaymentConfiguration

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ConnectionManger.instantiateWebSocket()

        // Below Two lines initialize the facebook sdk and through this, user can log in with fb app also....
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
        PaymentConfiguration.init(
            applicationContext,"pk_test_51HcYaaDVPC7KpoaUBqxarUUagXrI14GRCicyaZt8NztibJ4G9Y7KMtunrcWTg5PDm3PzcuBe1zkFFJiJRt1mXs8s009njabz8l"
        )
    }

}