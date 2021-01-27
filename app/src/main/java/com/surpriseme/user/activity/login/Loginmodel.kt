package com.surpriseme.user.activity.login

data class Loginmodel(
    val code: Int,
    val `data`: LoginDataModel,
    val status: Boolean
)

data class LoginDataModel(
    val message: String,
    val token: String,
    val user: LoginUserModel
)

data class LoginUserModel(
    val apple_id: Any,
    val card_brand: Any,
    val card_last_four: Any,
    val category_id: List<Any>,
    val converted_currency: String,
    val created_at: String,
    val currency: String,
    val currency_msp: String,
    val deleted_at: Any,
    val description: Any,
    val digital_price_per_hr: Any,
    val digital_show_status: String,
    val email: String,
    val fb_id: Any,
    val id: Int,
    val image: String,
    val is_notify: String,
    val lang: String,
    val latitude: Any,
    val live_price_per_hr: Any,
    val live_show_status: String,
    val location: Any,
    val longitude: Any,
    val mobile: Any,
    val name: String,
    val params: Any,
    val paypal_details: Any,
    val paypal_email: Any,
    val role: LoginRoleModel,
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
    val stripe_account_id: Any,
    val stripe_id: String,
    val trial_ends_at: Any,
    val updated_at: String
)

data class LoginRoleModel(
    val id: Int,
    val name: String,
    val permission: List<Any>
)