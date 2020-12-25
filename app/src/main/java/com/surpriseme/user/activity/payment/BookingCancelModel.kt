package com.surpriseme.user.activity.payment

data class BookingCancelModel(
    val code: Int,
    val `data`: Data,
    val status: Boolean
)

data class Data(
    val message: String
)