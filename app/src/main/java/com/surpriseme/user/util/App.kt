package com.surpriseme.user.util

import android.app.Application
import com.stripe.android.PaymentConfiguration

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ConnectionManger.instantiateWebSocket()

        PaymentConfiguration.init(
            applicationContext,"pk_test_51HcYaaDVPC7KpoaUBqxarUUagXrI14GRCicyaZt8NztibJ4G9Y7KMtunrcWTg5PDm3PzcuBe1zkFFJiJRt1mXs8s009njabz8l"
        )
    }

}