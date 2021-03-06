package com.surpriseme.user.util

import com.google.android.gms.maps.model.LatLng
import com.surpriseme.user.data.model.CardGetDetailModel
import com.surpriseme.user.fragments.bookingfragment.BookingArtistDetailModel
import com.surpriseme.user.fragments.locationfragment.LocationDataList
import com.surpriseme.user.fragments.viewprofile.ProfileFragment

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
    const val CUSTOMER_REVIEW = "customer/getartistrating"
    const val CARD_LIST = "customer/cards/list"
    const val UPDATE_LANGUAGE = "customer/update/lang"
    const val REGISTER_WITH_FB = "customer/register/fb"
    const val CURRENCIES = "currencies"
    const val CALENDAR_DATE_LIST = "customer/bookslot/list-date"
    const val LOGOUT = "logout"
    const val BOOKING_CANCEL = "customer/change-booking-status"
    const val RATE_BOOKING = "customer/rate-booking"
    const val TERMS_AND_CONDITIONS = "config/terms_and_conditions"
    const val PRIVACY_POLICY = "config/privacy_policy"
    const val ABOUT_US = "config/about_us"
    const val PAYMENT_CONFIG = "payment-config"

    var PUBLIC_KEY = ""
    var SECRET_KEY = ""
    var CLIENT_ID = ""


    const val ISREMEMBER = "ISREMEMBER"
    const val TIME_OUT = "Time Out"
    const val HOME_ADDRESS = "Home"
    const val WORK_ADDRESS = "Work"
    const val OTHER = "other"
    var ADDRESS = "ADDRESS"
    var LATITUDE = "LATITUDE"
    var LONGITUDE = "LONGITUDE"
    var NAME = ""
    var WantToUpdateAddress = false
    var WantToAddLocation = false
    var SAVED_LOCATION = false
    const val COMPLETE_REVIEW = "completed_review"
    var ARTIST_ID = ""
    var SWITCH_STATUS = ""
    var NOTIFICATION = false
    var BOOKING = false
    const val TARGET_MODEL_MESSAGE = "Message"
    const val TARGET_MODEL_BOOKING = "Booking"
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
    const val DESENDING ="desc"
    const val PRICE_LOW_TO_HIGH = "Price Low to High"
    var REPORT = "report"
    var COMING_FROM_DETAIL = false
    var SAVE_BOOKING_LIST:ArrayList<BookingArtistDetailModel> = ArrayList()
    var SHOW_TYPE = ""
    var IS_SWITCH_TO_VIRTUAL = true
    var IS_ADDED_TO_BACKSTACK =false
    var LATLNG:LatLng?=null
    var IDEAL_PAYMENT = false

    // by main activity backpress....

    var FB_ID = ""
    var FB_NAME = ""
    var FB_EMAIL = ""
    var FB_IMAGE = ""
    var FB_TOKEN = "FBTOKEN"
    var PAYMENT_FAILED = "payment_failed"
    var COMPLETED = "completed"
    var IS_CHAT_SESSION = false
    var IS_BOOKING_DONE = false
    var IS_SEARCH_ACTIVITY = false
    var IS_COMING_FROM_BOOKING_DETAIL = false
    var PROFILE_FRAGMENT = false  // Used to handle backpress, while user on profile fragment then popup will display
    var LOCATION_FRAGMENT = false // used to handle backpress, while Location fragment will be active


    var DEFAULT_LATITUDE = "DEFAULT_LATITUDE"
    var DEFAULT_LONGITUDE = "DEFAULT_LONGITUDE"
    var ADDRESS_ID = "ADDRESS_ID"
    var locationList: ArrayList<LocationDataList> = ArrayList()
    var IS_CARD_SELECTED = false
    var BOOKING_ID = ""
    var cardList: ArrayList<CardGetDetailModel> = ArrayList()
    var IS_CARD_DELETED = false
    var IS_TERM_AND_CONDITION = false
    var IS_PRIVACY_POLICY = false
    var IS_ABOUT_US = false



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
        const val PAGE = "page"
        const val LANG = "lang"
        const val RATING = "rating"
        const val RADIUS = "radius"
        const val FB_ID = "fb_id"
        const val CURRENCY = "currency"
        const val RATE = "rate"
        const val REVIEW = "review"

    }

    object DataKey {
        const val AUTH_VALUE = "AUTH_VALUE"
        const val CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded"
        const val DEVICE_TOKEN_VALUE = "1254"
        const val DEVICE_TYPE_VALUE = "android"
        val USER_IMAGE = "USER_IMAGE"  // is used to store Customer Profile Image....
        var USER_NAME = "USER_NAME" // is used to store Customer User name....
        var USER_EMAIL = "USER_EMAIL" // is used to store customer User email....
        var ARTIST_ID_VALUE = "ARTIST_ID_VALUE"
        var USER_ID = "USER_ID"
        var OLD_PASS_VALUE = "OLD_PASS_VALUE"
        var CURRENCY = "CURRENCY"

    }

    object ImageUrl {
        const val BASE_URL = "https://surprise-me.co/"
        const val BASE_URL_LIVE = "https://dev.netscapelabs.com/surpriseme/public/"
        const val ARTIST_IMAGE_URL = "uploads/users/artist/"
        const val USER_IMAGE_URL = "uploads/users/customer/"

//        "https://dev.netscapelabs.com/surpriseme/public/uploads/users/artist/"
//        https://dev.netscapelabs.com/surpriseme/public/uploads/users/artist/
    }

}