package com.surpriseme.user.retrofit


import android.widget.ImageView
import com.surpriseme.user.activity.login.Loginmodel
import com.surpriseme.user.activity.signup.RegisterModel
import com.surpriseme.user.activity.forgotpassword.ResetPasswordModel
import com.surpriseme.user.fragments.artistbookingdetail.ArtistDetailModel
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailModel
import com.surpriseme.user.fragments.bookingfragment.CustomerBookingListModel
import com.surpriseme.user.fragments.bookingslotfragment.BookingCreateModel
import com.surpriseme.user.fragments.changepasswordfragment.ChangePasswordModel
import com.surpriseme.user.fragments.homefragment.ArtistModel
import com.surpriseme.user.fragments.googlemapfragment.CreateLocationModel
import com.surpriseme.user.fragments.locationfragment.DeleteAddressModel
import com.surpriseme.user.fragments.locationfragment.LocationListModel
import com.surpriseme.user.fragments.locationfragment.UpdateAddressModel
import com.surpriseme.user.fragments.viewprofile.ViewProfileModel
import com.surpriseme.user.fragments.notificationfragment.NotificationListModel
import com.surpriseme.user.fragments.notificationfragment.NotificationStatusModel
import com.surpriseme.user.activity.searchactivity.CategoryModel
import com.surpriseme.user.activity.signuptype.RegisterWithFbModel
import com.surpriseme.user.data.model.*
import com.surpriseme.user.fragments.bookingslotfragment.SlotModel
import com.surpriseme.user.fragments.chatFragment.ChatByIdModel
import com.surpriseme.user.fragments.chatListfragment.ChatListModel
import com.surpriseme.user.fragments.homefragment.CurrencyListModel
import com.surpriseme.user.fragments.notificationfragment.NotificationReadModel
import com.surpriseme.user.fragments.paymentfragment.BookingStatusModel
import com.surpriseme.user.fragments.reviewfragment.ReviewModel
import com.surpriseme.user.fragments.selectdateofbookingfragment.CalendarDateListModel
import com.surpriseme.user.util.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @POST(Constants.LOGIN)
    fun loginApi(
        @Query(Constants.ApiKey.EMAIL) email: String,
        @Query(Constants.ApiKey.PASSWORD) password: String,
        @Query(Constants.ApiKey.DEVICE_TOKEN) device_Token: String,
        @Query(Constants.ApiKey.DEVICE_TYPE) device_Type: String
    ): Call<Loginmodel>

    @POST(Constants.LOGOUT)
    fun logout(
        @Header(Constants.ApiKey.AUTHORIZATION ) authorization:String,
        @Query(Constants.ApiKey.DEVICE_TOKEN) device_Token: String,
        @Query(Constants.ApiKey.DEVICE_TYPE) device_Type: String
    ): Call<Loginmodel>

    @POST(Constants.REGISTER)
    fun registerApi(
        @Query(Constants.ApiKey.NAME) name: String,
        @Query(Constants.ApiKey.EMAIL) email: String,
        @Query(Constants.ApiKey.PASSWORD) password: String,
        @Query(Constants.ApiKey.DEVICE_TYPE) device_Type: String,
        @Query(Constants.ApiKey.DEVICE_TOKEN) device_Token: String,
        @Query(Constants.ApiKey.LANG) lang: String
    ): Call<RegisterModel>

    @POST(Constants.REGISTER_WITH_FB)
    fun registerWithFB(
        @Query(Constants.ApiKey.NAME) name: String,
        @Query(Constants.ApiKey.EMAIL) email: String,
        @Query(Constants.ApiKey.FB_ID) fb_id: String,
        @Query(Constants.ApiKey.DEVICE_TYPE) device_Type: String,
        @Query(Constants.ApiKey.DEVICE_TOKEN) device_Token: String,
        @Query(Constants.ApiKey.LANG) lang: String,
        @Query(Constants.ApiKey.IMAGE) imageView: String
    ): Call<RegisterWithFbModel>

    @POST(Constants.RESET_PASSWORD)
    fun resetPasswordApi(@Query(Constants.ApiKey.EMAIL) email: String): Call<ResetPasswordModel>

    @POST(Constants.GET_PROFILE)
    fun getProfileApi(@Header(Constants.ApiKey.AUTHORIZATION) authorization: String): Call<ViewProfileModel>

    @Multipart
    @POST(Constants.UPDATE_PROFILE)
    fun updateProfileApi(
        @Header(Constants.ApiKey.AUTHORIZATION) auth: String,
        @PartMap fields: HashMap<String, RequestBody>,
        @Part files: MultipartBody.Part?
    ): Call<UpdateProfileModel>

    @POST(Constants.UPDATE_PROFILE)
    fun updateProfileApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.CURRENCY) currency: String
    ): Call<UpdateProfileModel>


    // Artist list for home screen with lat, long....
    @POST(Constants.CUSTOMER_ARTIST_LIST)
    fun artistListApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.LIMIT) limit: String,
        @Query(Constants.ApiKey.LATITUDE) latitude: String,
        @Query(Constants.ApiKey.LONGITUDE) longitude: String,
        @Query(Constants.ApiKey.SEARCH) search: String,
        @Query(Constants.ApiKey.PAGE) page: String
    ): Call<ArtistModel>

    // Artist list for home screen without lag, long....
    @POST(Constants.CUSTOMER_ARTIST_LIST)
    fun artistListApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.LIMIT) limit: String,
        @Query(Constants.ApiKey.SEARCH) search: String,
        @Query(Constants.ApiKey.PAGE) page: String
    ): Call<ArtistModel>

    // Artist list for Search activity with lat, long....
    @POST(Constants.CUSTOMER_ARTIST_LIST)
    fun artistListApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.LIMIT) limit: String,
        @Query(Constants.ApiKey.PAGE) page: String,
        @Query(Constants.ApiKey.LATITUDE) latitude: String,
        @Query(Constants.ApiKey.LONGITUDE) longitude: String,
        @Query(Constants.ApiKey.SEARCH) search: String,
        @Query(Constants.ApiKey.CATEGORY_IDS) categoryList: String,
        @Query(Constants.ApiKey.FROM_DATE) from_date: String,
        @Query(Constants.ApiKey.TO_DATE) to_date: String,
        @Query(Constants.ApiKey.SORT_BY) sort_by: String,
        @Query(Constants.ApiKey.SHOW_TYPE) show_type: String,
        @Query(Constants.ApiKey.RATING) rating: String,
        @Query(Constants.ApiKey.RADIUS) radius: String
    ): Call<ArtistModel>

    // Artist list for Search activity without lat, long....
    @POST(Constants.CUSTOMER_ARTIST_LIST)
    fun artistListApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.LIMIT) limit: String,
        @Query(Constants.ApiKey.SEARCH) search: String,
        @Query(Constants.ApiKey.PAGE) page: String,
        @Query(Constants.ApiKey.CATEGORY_IDS) categoryList: String,
        @Query(Constants.ApiKey.FROM_DATE) from_date: String,
        @Query(Constants.ApiKey.TO_DATE) to_date: String,
        @Query(Constants.ApiKey.SORT_BY) sort_by: String,
        @Query(Constants.ApiKey.SHOW_TYPE) show_type: String,
        @Query(Constants.ApiKey.RATING) rating: String,
        @Query(Constants.ApiKey.RADIUS) radius: String
    ): Call<ArtistModel>

    //Customer Booking List...
    @POST(Constants.CUSTOMER_BOOKING_LIST)
    fun customerBookingListApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.LIMIT) limit: String,
        @Query(Constants.ApiKey.PAGE) page: String
    ): Call<CustomerBookingListModel>


    // Booking Detail Api....
    @POST(Constants.BOOKING_DETAIL)
    fun bookingDetailApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.ID) bookingID: String
    ): Call<BookingDetailModel>


    //Create Address
    @POST(Constants.CUSTOMER_ADDRESS_STORE)
    fun createAddress(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.NAME) name: String,
        @Query(Constants.ApiKey.STREET_ADDRESS) street_address: String,
        @Query(Constants.ApiKey.CITY) city: String,
        @Query(Constants.ApiKey.STATE) state: String,
        @Query(Constants.ApiKey.ZIP) zip: String,
        @Query(Constants.ApiKey.COUNTRY) country: String,
        @Query(Constants.ApiKey.LATITUDE) latitude: String,
        @Query(Constants.ApiKey.LONGITUDE) longitude: String
    ): Call<CreateLocationModel> // Create Location model Use to While create address....

    @GET(Constants.CUSTOMER_ADDRESS_LIST)
    fun addressListApi(@Header(Constants.ApiKey.AUTHORIZATION) authorization: String): Call<LocationListModel>

    @POST(Constants.DELETE_ADDRESS)
    fun addressDeleteApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.ID) id: String
    ): Call<DeleteAddressModel>

    @POST(Constants.UPDATE_ADDRESS)
    fun updateAddressApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.ID) id: String,
        @Query(Constants.ApiKey.NAME) name: String,
        @Query(Constants.ApiKey.STREET_ADDRESS) street_address: String,
        @Query(Constants.ApiKey.CITY) city: String,
        @Query(Constants.ApiKey.STATE) state: String,
        @Query(Constants.ApiKey.ZIP) zip: String,
        @Query(Constants.ApiKey.COUNTRY) country: String,
        @Query(Constants.ApiKey.LATITUDE) latitude: String,
        @Query(Constants.ApiKey.LONGITUDE) longitude: String
    ): Call<UpdateAddressModel>


    @POST(Constants.ARTIST_DETAIL)
    fun artistDetailApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.ID) id: String
    ): Call<ArtistDetailModel>

    @POST(Constants.BOOKING_CREATE)
    fun bookingCreateApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Header(Constants.ApiKey.CONTENT_TYPE) content_type: String,
        @Query(Constants.ApiKey.TYPE) type: String,
        @Query(Constants.ApiKey.DATE) date: String,
        @Query(Constants.ApiKey.FROM_TIME) fromTime: String,
        @Query(Constants.ApiKey.TO_TIME) toTIME: String,
        @Query(Constants.ApiKey.ARTIST_ID) artistID: String,
        @Query(Constants.ApiKey.ADDRESS) address: String,
    ): Call<BookingCreateModel>

    @POST(Constants.CHANGE_PASSWORD)
    fun changePasswordApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.OLD_PASSWORD) oldPassword: String,
        @Query(Constants.ApiKey.PASSWORD) password: String,
        @Query(Constants.ApiKey.CONFIRM_PASSWORD) confirm_Password: String
    ): Call<ChangePasswordModel>

    @POST(Constants.CATEGORY_LIST)
    fun categoryListApi(@Header(Constants.ApiKey.CONTENT_TYPE) content_type: String): Call<CategoryModel>

    @POST(Constants.NOTIFICATION_STATUS)
    fun notificationStatusApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.IS_NOTIFY) is_notify: String
    ): Call<NotificationStatusModel>

    @POST(Constants.NOTIFICATION_LIST)
    fun notificationListApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.LIMIT) limit: String
    ): Call<NotificationListModel>

    @POST(Constants.NOTIFICATION_READ)
    fun notificationReadApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.ID) notificationID: String
    ): Call<NotificationReadModel>

    @POST(Constants.CHAT_LIST)
    fun chatListApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.LIMIT) limit: String
    ): Call<ChatListModel>

    @POST(Constants.CHAT)
    fun chatApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.RECEIVER_ID) receiver_id: String
    ): Call<ChatByIdModel>

    @POST(Constants.BOOKING_STATUS)
    fun bookingStatusApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.BOOKING_ID) booking_id: String,
        @Query(Constants.ApiKey.STATUS) status: String
    ): Call<BookingStatusModel>

    @POST(Constants.BOOKING_STATUS)
    fun bookingStatusApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.BOOKING_ID) booking_id: String,
        @Query(Constants.ApiKey.STATUS) status: String,
        @Query(Constants.ApiKey.REPORT) report: String
    ): Call<BookingStatusModel>


    @POST(Constants.BOOKING_SLOT_LIST)
    fun bookingSlotApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.DATE) date: String,
        @Query(Constants.ApiKey.ARTIST_ID) artistID: String
    ): Call<SlotModel>

    @POST(Constants.CUSTOMER_REVIEW)
    fun customerReviewApi(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.ARTIST_ID) artistID: String
    ): Call<ReviewModel>


    @POST(Constants.CARD_LIST)
    fun cardlist(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
    ): Call<CardGetModel>


    @POST("customer/cards/store")
    fun cardadd(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query("token") artistID: String
    ): Call<CardAddModel>

    @POST(Constants.BOOKING_STATUS)
    fun paynow(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query("card_id") artistID: String,
        @Query("booking_id") bookingid: String,
        @Query("status") status: String,
        @Query("payment_method") paymentmethod: String
    ): Call<PaymentModel>

    @POST("customer/create-payment-intent")
    fun payintent(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query("id") artistID: String

    ): Call<PaymentIntent>


    @POST("customer/cards/delete")
    fun deletecard(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query("card_id") artistID: String
    ): Call<DeleteCard>

    @GET(Constants.CURRENCIES)
    fun currencyList(@Header(Constants.ApiKey.AUTHORIZATION) authorization: String): Call<CurrencyListModel>

    @POST(Constants.CALENDAR_DATE_LIST)
    fun calendarDateList(
        @Header(Constants.ApiKey.AUTHORIZATION) authorization: String,
        @Query(Constants.ApiKey.ARTIST_ID) artistID: String
    ): Call<CalendarDateListModel>


}