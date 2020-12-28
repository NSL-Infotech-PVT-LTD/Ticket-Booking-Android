package com.surpriseme.user.fragments.bookingdetailfragment

data class BookingDetailModel(
    val code: Int,
    val `data`: BookingDataModel,
    val status: Boolean
)

data class BookingDataModel(
    val address:String?=null,
    val artist_currency: String,
    val artist_detail: ArtistDetail,
    val artist_id: Int,
    val created_at: String,
    val customer_currency: String,
    val date: String,
    val from_time: String,
    val id: Int,
    val otp: Int,
    val params: Report,
    val payment_mode: String,
    val payment_params: PaymentParams,
    val price: Int,
    val rate_artist: Any,
    val rate_detail: RateDetail?,
    val status: String,
    val to_time: String,
    val type: String
)

data class Report(
    val report: String = ""
)

data class ArtistDetail(
    val converted_currency: String,
    val currency: String,
    val currency_msp: String,
    val id: Int,
    val image: String,
    val name: String,
    val role: Role,
    val shows_image: List<Any>,
    val shows_video_thumbnail: String
)

data class PaymentParams(
    val amount: Int,
    val id: String
)

data class RateDetail(
    val artist_id: Int,
    val avg_rate: Int,
    val created_at: String,
    val created_by: Int,
    val id: Int,
    val rate: String,
    val review: String
)

data class Role(
    val id: Int,
    val name: String,
    val permission: List<Any>
)