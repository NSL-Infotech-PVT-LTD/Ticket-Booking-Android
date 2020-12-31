package com.surpriseme.user.fragments.artistbookingdetail

data class ArtistDetailModel(
    val code: Int,
    val data: ArtistDataModel,
    val status: Boolean
)

data class ArtistDataModel(
    val card_brand: Any,
    val card_last_four: Any,
    val category_id: List<Any>,
    val category_id_details: List<CategoryIdDetail>,
    val created_at: String,
    val currency: String,
    val deleted_at: Any,
    val description: String,
    val digital_price_per_hr: Int,
    val digital_show_status: String,
    val email: String,
    val id: Int,
    val image: String,
    val is_notify: String,
    val lang: String,
    val latitude: String,
    val live_price_per_hr: Int,
    val live_show_status: String,
    val location: String,
    val longitude: String,
    val mobile: String,
    val name: String,
    val converted_currency: String,
    val params: Any,
    val rating: Float,
    val converted_live_price:Double,
    val converted_digital_price:Double,
    val role: Role,
    val shows_image: ArrayList<String>?=null,
    val shows_image_1: String?,
    val shows_image_2: String?,
    val shows_image_3: String?,
    val shows_image_4: String?,
    val shows_video: String?=null,
    val shows_video_thumbnail: String?=null,
    val social_link_insta: String?=null,
    val social_link_youtube: String?=null,
    val status: String,
    val stripe_account_id: String,
    val stripe_id: Any,
    val trial_ends_at: Any,
    val updated_at: String
)

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