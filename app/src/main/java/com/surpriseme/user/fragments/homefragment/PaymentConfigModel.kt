package com.surpriseme.user.fragments.homefragment

data class PaymentConfigModel(
    val code: Int,
    val data: PaymentDataModel,
    val status: Boolean
)

data class PaymentDataModel(
    val client_id: String,
    val public_key: String,
    val seceret_key: String
)