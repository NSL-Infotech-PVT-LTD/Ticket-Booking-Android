package com.surpriseme.user.fragments.notificationfragment

data class NotificationReadModel(
    val code: Int,
    val `data`: NotificationReadDataModel,
    val status: Boolean
)

data class NotificationReadDataModel(
    val message: String
)