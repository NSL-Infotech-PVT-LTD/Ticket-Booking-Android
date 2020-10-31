package com.surpriseme.user.fragments.paymentfragment

data class BookingStatusModel(
    val code: Int,
    val `data`: StatusDataModel,
    val status: Boolean
)

data class StatusDataModel(
    val message: String
)