package com.surpriseme.user.fragments.notificationfragment

import java.io.Serializable

data class NotificationListModel(
    val code: Int,
    val `data`: NotificationListPageModel,
    val status: Boolean
)

data class NotificationListPageModel(
    val current_page: Int,
    val `data`: ArrayList<NotificationListDataModel>,
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

data class NotificationListDataModel(
    val body: String,
    val booking_detail: NotiListBookingDetail,
    val created_at: String,
    val created_by: Int,
    val customer_detail: NotiListCustomerDetail,
    val deleted_at: Any,
    val id: Int,
    val is_read: String,
    val message: String,
    val params: Any,
    val status: String,
    val target_id: Int,
    val title: String,
    val updated_at: String
) : Serializable

data class NotiListBookingDetail(
    val booking_id: Int,
    val data_type: String,
    val date: String,
    val from_time: String,
    val status: String,
    val target_id: Int,
    val target_model: String,
    val to_time: String
) : Serializable

data class NotiListCustomerDetail(
    val category_id_details: ArrayList<Any>,
    val id: Int,
    val image: Any,
    val name: String,
    val role: NotiListRole,
    val shows_image: ArrayList<Any>
) : Serializable

data class NotiListRole(
    val id: Int,
    val name: String,
    val permission: ArrayList<Any>
) : Serializable