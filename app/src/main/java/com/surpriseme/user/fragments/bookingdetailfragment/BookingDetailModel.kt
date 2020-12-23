package com.surpriseme.user.fragments.bookingdetailfragment

data class BookingDetailModel(
    val code: Int,
    val `data`: BookingDataModel,
    val status: Boolean
)

data class BookingDataModel(
    val address: String,
    val artist_detail: ArtistDetailBookingDetail,
    val artist_id: Int,
    val created_at: String,
    val date: String,
    val from_time: String,
    val id: Int,
    val price:Int,
    var artist_currency:String,
    val customer_currency:String,
    val otp: Int,
    val params:Report,
    val payment_mode: Any,
    val payment_params: Any,
    val rate_artist: Any,
    val rate_detail: RateDetail,
    val status: String,
    val to_time: String,
    val type: String
)
data class RateDetail(
    val id:Int,
    val artist_id:Int,
    val rate:Int,
    val review:String,
    val created_by:Int,
    val created_at:String,
    val avg_rate: Float
)
data class Report(
    val report: String = ""
)

data class ArtistDetailBookingDetail(
    val category_id_details: ArrayList<Any>,
    val currency: String,
    var converted_digital_price:String,
    val converted_live_price:String,
    val converted_currency:String,
    val currency_msp:String,
    val shows_video_thumbnail:String,
    val id: Int,
    val image: String,
    val name: String,
    val role: RoleBookingDetail,
    val shows_image: ArrayList<Any>
)

data class RoleBookingDetail(
    val id: Int,
    val name: String,
    val permission: ArrayList<Any>
)