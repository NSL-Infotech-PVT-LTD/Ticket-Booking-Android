package com.surpriseme.user.data.model

data class PaymentIntent(
    val code: Int,
    val `data`: PaidData,
    val status: Boolean
)

data class PaidData(
    val amount: Int,
    val amount_capturable: Int,
    val amount_received: Int,
    val application: Any,
    val application_fee_amount: Any,
    val canceled_at: Any,
    val cancellation_reason: Any,
    val capture_method: String,
    val charges: Charges,
    val client_secret: String,
    val confirmation_method: String,
    val created: Int,
    val currency: String,
    val customer: Any,
    val description: Any,
    val id: String,
    val invoice: Any,
    val last_payment_error: Any,
    val livemode: Boolean,
    val metadata: Metadata,
    val next_action: Any,
    val `object`: String,
    val on_behalf_of: Any,
    val payment_method: Any,
    val payment_method_options: List<Any>,
    val payment_method_types: List<String>,
    val receipt_email: Any,
    val review: Any,
    val setup_future_usage: Any,
    val shipping: Any,
    val source: Any,
    val statement_descriptor: Any,
    val statement_descriptor_suffix: Any,
    val status: String,
    val transfer_data: Any,
    val transfer_group: Any
)

data class Charges(
    val `data`: List<Any>,
    val has_more: Boolean,
    val `object`: String,
    val total_count: Int,
    val url: String
)

data class Metadata(
    val booking_id: String
)