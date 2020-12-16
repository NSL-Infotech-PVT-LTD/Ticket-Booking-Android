package com.surpriseme.user.activity.signuptype

data class RegisterWithFbModel(
    val code: Int,
    val `data`: FbDataModel,
    val status: Boolean
)

data class FbDataModel(
    val message: String,
    val token: String,
    val user: FbUserModel
)

data class FbUserModel(
    val apple_id: Any,
    val card_brand: Any,
    val card_last_four: Any,
    val category_id: List<Any>,
    val converted_currency: String,
    val converted_digital_price: Boolean,
    val converted_live_price: Boolean,
    val created_at: String,
    val currency: String,
    val currency_msp: String,
    val deleted_at: Any,
    val description: Any,
    val digital_price_per_hr: Any,
    val digital_show_status: String,
    val email: String,
    val fb_id: String,
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
    val role: FbRoleModel,
    val shows_image: List<Any>,
    val shows_image_1: Any,
    val shows_image_2: Any,
    val shows_image_3: Any,
    val shows_image_4: Any,
    val shows_video: Any,
    val social_link_insta: Any,
    val social_link_youtube: Any,
    val status: String,
    val stripe_account_id: Any,
    val stripe_id: Any,
    val trial_ends_at: Any,
    val updated_at: String
)

data class FbRoleModel(
    val id: Int,
    val name: String,
    val permission: List<Any>
)