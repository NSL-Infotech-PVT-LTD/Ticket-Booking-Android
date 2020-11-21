package com.surpriseme.user.data.model

data class PaymentModel(
    val code: Int,
    val `data`: PayData,
    val status: Boolean
)

data class PayData(
    val card: Card,
    val message: String
)

data class Card(
    val `data`: List<PayDataX>,
    val has_more: Boolean,
    val `object`: String,
    val url: String
)

data class PayDataX(
    val address_city: Any,
    val address_country: Any,
    val address_line1: Any,
    val address_line1_check: Any,
    val address_line2: Any,
    val address_state: Any,
    val address_zip: Any,
    val address_zip_check: Any,
    val brand: String,
    val country: String,
    val customer: String,
    val cvc_check: String,
    val dynamic_last4: Any,
    val exp_month: Int,
    val exp_year: Int,
    val fingerprint: String,
    val funding: String,
    val id: String,
    val last4: String,
    val metadata: List<Any>,
    val name: String,
    val `object`: String,
    val tokenization_method: Any
)