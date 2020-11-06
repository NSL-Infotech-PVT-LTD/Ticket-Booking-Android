package com.surpriseme.user.fragments.homefragment

data class ArtistModel(
    val code: Int,
    val `data`: DataArtistList,
    val status: Boolean
)

data class DataArtistList(
    val current_page: Int,
    val `data`: ArrayList<DataUserArtistList>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

 class DataUserArtistList {
     val category_id: ArrayList<Any>? = null
     val category_id_details: ArrayList<String>?=null
     val created_at: String=""
     val currency: String=""
     val description: String=""
     val digital_price_per_hr: Any?=null
     val distance: Double=0.0
     val email: String=""
     val id: Int=0
     val image: String=""
     val latitude: String=""
     val live_price_per_hr: String=""
     val location: String=""
     val longitude: String=""
     val mobile: String=""
     val name: String=""
     val role: RoleArtistList?=null
     val shows_image: List<Any>?=null
     val shows_image_1: Any?=null
     val shows_image_2: Any?=null
     val shows_image_3: Any?= null
     val shows_image_4: Any?=null
     val shows_video: Any?=null
     val social_link_insta: String=""
     val social_link_youtube: String=""
     val status: String=""
     val updated_at: String=""
     val rating: Float=0f
 }

data class RoleArtistList(
    val id: Int,
    val name: String,
    val permission: List<Any>
)