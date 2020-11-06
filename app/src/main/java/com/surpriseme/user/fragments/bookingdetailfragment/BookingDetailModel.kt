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
    val otp: Int,
    val params:Report,
    val payment_mode: Any,
    val payment_params: Any,
    val rate_artist: Any,
    val rate_detail: Any,
    val status: String,
    val to_time: String,
    val type: String
)
data class Report(
    val report: String = ""
)

data class ArtistDetailBookingDetail(
    val category_id_details: ArrayList<Any>,
    val currency: String,
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