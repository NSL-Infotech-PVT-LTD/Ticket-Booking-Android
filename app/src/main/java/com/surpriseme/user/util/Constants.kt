package com.surpriseme.user.util

object Constants {
    const val LOGIN = "customer/login"
    const val BEARER = "Bearer "
    const val ERROR = "error"
    const val ERRORS = "errors"
    const val SOMETHING_WENT_WRONG = "Something went wrong"
    const val REGISTER = "customer/register"
    const val CUSTOMER_ARTIST_LIST = "customer/artist/list" // Artist List for Customer....
    const val CATEGORY_LIST = "category/list"
    const val CUSTOMER_BOOKING_LIST = "customer/booking/list"
    const val BOOKING_DETAIL = "customer/booking"
    const val CUSTOMER_ADDRESS_STORE = "customer/address/store"
    const val CUSTOMER_ADDRESS_LIST = "customer/address/list"
    const val DELETE_ADDRESS = "customer/address/delete"
    const val RESET_PASSWORD = "reset-password"
    const val UPDATE_PROFILE = "customer/update"    // Updating the Customer Profile....
    const val GET_PROFILE = "get-profile"
    const val UPDATE_ADDRESS = "customer/address/update"
    const val ARTIST_DETAIL = "customer/artist"
    const val BOOKING_CREATE = "customer/booking/store"
    const val CHANGE_PASSWORD = "customer/change-password"
    const val NOTIFICATION_STATUS = "notification/status"
    const val NOTIFICATION_LIST = "notification/list"
    const val NOTIFICATION_READ = "notification/read"
    const val CHAT_LIST = "chat/getItemByReceiverId"
    const val CHAT = "chat/getItemsByReceiverId"
    const val BOOKING_STATUS = "customer/change-booking-status"
    const val BOOKING_SLOT_LIST = "customer/bookslot/list"


    const val PLEASE_CLICK_BACK_AGAIN_To_EXIT = "Please click BACK again to exit"
    const val TIME_OUT = "Time Out"
    const val HOME_ADDRESS = "home address"
    const val HOME = "Home"
    const val WORK_ADDRESS = "work address"
    const val WORK = "Work"
    const val OTHER_ADDRESS = "other address"
    const val OTHER = "other"
    var ADDRESS = "ADDRESS"
    var LATITUDE = "LATITUDE"
    var LONGITUDE = "LONGITUDE"
    var WantToUpdateAddress = false
    var WantOtherLocation = false
    const val RECOVERY_REQUEST = 1
    const val LIVE_SHOW_BOOKING = "Live Show Booking"
    const val DIGITAL_SHOW_BOOKING = "Digital Show Booking"
    const val LIVE = "live"
    const val DIGITAL = "digital"
    var BOOKING_TYPE = ""
    var SAVED_LOCATION = false
    const val COMPLETE_REVIEW = "completed_review"
    var ARTIST_ID = ""
    var SWITCH_STATUS = ""
    var NOTIFICATION = false
    var BOOKING = false
    const val TARGET_MODEL_MESSAGE = "Message"
    const val TARGET_MODEL_BOOKING = "Booking"
    const val ACCEPTED = "accepted"
    const val PENDING = "pending"
    const val REJECTED = "rejected"
    const val CONFIRMED = "confirmed"
    const val PROCESSING = "processing"
    const val CANCEL = "cancel"
    var CHAT_ID =""
    var OTP = 0
    const val SELECT_REASON =  "Select Reason"
    const val ARTIST_DENIED_DUTY =  "Artist Denied Duty"
    const val ARTIST_IS_UNREACHABLE =  "Artist is unreachable"
    const val ARTIST_NOT_PICKING_CALL =  "Artist not picking call"
    const val ASENDING ="asc"
    const val DESENDING ="dsc"
    var FROM_TIME = ""
    var TO_TIME = ""


    object ApiKey {
        const val AUTHORIZATION = "Authorization"
        const val CONTENT_TYPE = "Content-Type"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val DEVICE_TOKEN = "device_token"
        const val DEVICE_TYPE = "device_type"
        const val NAME = "name"
        const val IMAGE = "image"
        const val LIMIT = "limit"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val STREET_ADDRESS = "street_address"
        const val CITY = "city"
        const val STATE = "state"
        const val ZIP = "zip"
        const val COUNTRY = "country"
        const val ID = "id"
        const val TYPE = "type"
        const val DATE= "date"
        const val FROM_TIME = "from_time"
        const val TO_TIME = "to_time"
        const val ARTIST_ID = "artist_id"
        const val OLD_PASSWORD  = "old_password"
        const val CONFIRM_PASSWORD = "confirm_password"
        const val SEARCH = "search"
        const val ADDRESS = "address"
        const val CATEGORY_IDS = "category_ids"
        const val IS_NOTIFY = "is_notify"
        const val RECEIVER_ID = "receiver_id"
        const val BOOKING_ID = "booking_id"
        const val STATUS = "status"
        const val REPORT = "report"
        const val FROM_DATE = "from_date"
        const val TO_DATE = "to_date"
        const val SORT_BY = "sort_by"
        const val SHOW_TYPE = "show_type"




    }

    object DataKey {
        const val AUTH_VALUE = "AUTH_VALUE"
        const val CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded"
        const val DEVICE_TOKEN_VALUE = "1254"
        const val DEVICE_TYPE_VALUE = "android"
        val USER_IMAGE = "USER_IMAGE"
        var ARTIST_ID_VALUE = "ARTIST_ID_VALUE"
        var USER_ID = "USER_ID"
        var OLD_PASS_VALUE = "OLD_PASS_VALUE"
    }

    object ImageUrl {
        const val BASE_URL = "https://dev.netscapelabs.com/surpriseme/public/"
        const val ARTIST_IMAGE_URL = "uploads/users/artist/"
        const val USER_IMAGE_URL = "uploads/users/customer/"

//        "https://dev.netscapelabs.com/surpriseme/public/uploads/users/artist/"
//        https://dev.netscapelabs.com/surpriseme/public/uploads/users/artist/
    }

}