package com.surpriseme.user.fragments.bookingfragment

import java.io.Serializable

data class CustomerBookingListModel(
    val code: Int,
    val `data`: BookingListPagination,
    val status: Boolean
)

data class BookingListPagination(
    val current_page: Int,
    val `data`: ArrayList<BookingArtistDetailModel>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

 class BookingArtistDetailModel: Serializable {
    val artist_detail: ArtistDetailBooking? = null
    val artist_id: Int = 0
    val created_at: String = ""
    val date: String = ""
    val from_time: String = ""
    val id: Int=0
    val payment_mode: Any?=null
    val payment_params: Any?=null
    val rate_detail: RateArtistDetail?=null
    val rate_artist: Any?=null
    val to_time: String=""
    val type: String=""
    val address: String=""
    val status: String=""
}

data class RateArtistDetail(
    val artist_id: Int,
    val avg_rate: Double,
    val created_at: String,
    val created_by: Int,
    val id: Int,
    val rate: String,
    val review: String
) : Serializable

data class ArtistDetailBooking(
    val category_id_details: List<Any>,
    val currency: String,
    val id: Int,
    val image: String,
    val name: String,
    val role: RoleBookingList,
    val shows_image: List<Any>
) : Serializable

data class RoleBookingList(
    val id: Int,
    val name: String,
    val permission: List<Any>
) : Serializable