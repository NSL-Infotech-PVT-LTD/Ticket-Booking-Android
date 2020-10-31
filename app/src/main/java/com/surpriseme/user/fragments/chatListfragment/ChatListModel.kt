package com.surpriseme.user.fragments.chatListfragment

data class ChatListModel(
    val code: Int,
    val `data`: ChatListDataModel,
    val status: Boolean
)

data class ChatListDataModel(
    val list: ArrayList<ChatDetailModel>
)
 class ChatDetailModel {
    val deleted_at: Any?=null
    val status: String?=null
    val updated_at: String?=null
    var id = 0
    var local_message_id: Long? = null
    var message_id: String? = null
    var reply_id = 0
    var sender_id = 0
    var receiver_id = 0
    var request_id = 0
    var attachment: String? = null
    var thumbnail: String? = null
    var message: String? = null
    var sender_role: String? = null
    var word_average = 0
    var type: String? = null
    var is_read: String? = null
    var created_at: String = ""
    var sender_name: String? = null
    var sender_image: Any? = null
    var receiver_name: String? = null
    var receiver_image: Any? = null
    var reply_count = 0
    var messageCount = 0
}

