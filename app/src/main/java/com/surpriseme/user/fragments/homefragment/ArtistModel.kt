package com.surpriseme.user.fragments.homefragment

data class ArtistModel(
    val code: Int,
    val `data`: Data,
    val status: Boolean
)

data class Data(
    val current_page: Int,
    val `data`: ArrayList<DataUserArtistList>,
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

 class DataUserArtistList {
    val category_id_details: ArrayList<CategoryIdDetail>? = null
    val currency: String = ""
    val description: String = ""
    val digital_price_per_hr: Double=0.0
    val id: Int=0
    val image: String=""
    val live_price_per_hr: Int=0
    val name: String=""
    val rating: Float=0f
    val role: Role?=null
    val shows_image: List<Any>?=null
}

data class CategoryIdDetail(
    val artist_id: Int,
    val category_id: Int,
    val category_name: String,
    val id: Int
)

data class Role(
    val id: Int,
    val name: String,
    val permission: List<Any>
)