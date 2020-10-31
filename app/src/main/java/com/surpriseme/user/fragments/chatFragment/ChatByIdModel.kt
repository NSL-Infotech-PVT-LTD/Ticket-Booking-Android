package com.surpriseme.user.fragments.chatFragment

data class ChatByIdModel(
    val code: Int,
    val `data`: ChatReceiverModel,
    val status: Boolean
)

data class ChatReceiverModel(
    val chat: ChatPagingModel,
    val receiver_detail: ReceiverDetail
)

data class ChatPagingModel(
    val current_page: Int,
    val `data`: ArrayList<ChatDataModel>,
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

data class ReceiverDetail(
    val category_id_details: List<Any>,
    val id: Int,
    val image: String,
    val name: String,
    val role: ChatRoleModel,
    val shows_image: List<Any>
)

 class ChatDataModel{
    var attachment: Any?=null
     var created_at=""
     var id: Int=0
     var is_read: String?=null
     var local_message_id: String?=null
     var message: String?=null
     var message_id: String?=null
     var receiver_id: Int=0
     var receiver_image: String?=null
     var receiver_name: String?=null
     var reply_count: Int=0
     var reply_id: Int=0
     var sender_id = 0
     var sender_image: String?=null
     var sender_name: String?=null
     var type: String?=null
}

data class ChatRoleModel(
    val id: Int,
    val name: String,
    val permission: List<Any>
)