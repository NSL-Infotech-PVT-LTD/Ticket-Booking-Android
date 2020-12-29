package com.surpriseme.user.activity.settings

data class UpdateLanguageModel(
    val code: Int,
    val `data`: LanguageDataModel,
    val status: Boolean
)

data class LanguageDataModel(
    val message: String,
    val user: LanguageUserModel
)

data class LanguageUserModel(
    val category_id: List<Any>,
    val category_id_details: List<Any>,
    val converted_currency: String,
    val converted_digital_price: Any,
    val converted_live_price: Any,
    val currency: String,
    val currency_msp: String,
    val description: Any,
    val digital_price_per_hr: Any,
    val digital_show_status: String,
    val email: String,
    val id: Int,
    val image: Any,
    val is_notify: String,
    val lang: String,
    val live_price_per_hr: Any,
    val live_show_status: String,
    val location: Any,
    val name: String,
    val paypal_email: Any,
    val rating: Int,
    val role: LanguageRoleModel,
    val shows_image: List<Any>,
    val shows_image_1: Any,
    val shows_image_2: Any,
    val shows_image_3: Any,
    val shows_image_4: Any,
    val shows_video: Any,
    val shows_video_thumbnail: String,
    val social_link_insta: Any,
    val social_link_youtube: Any,
    val status: String,
    val stripe_account_id: Any
)

data class LanguageRoleModel(
    val id: Int,
    val name: String,
    val permission: List<Any>
)