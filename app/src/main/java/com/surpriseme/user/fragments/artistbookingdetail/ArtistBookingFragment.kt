package com.surpriseme.user.fragments.artistbookingdetail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.VideoActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.FragmentArtistBookingBinding
import com.surpriseme.user.fragments.chatFragment.ChatFragment
import com.surpriseme.user.fragments.reviewfragment.ReviewFragment
import com.surpriseme.user.fragments.selectdateofbookingfragment.SelectDateFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.regex.Matcher
import java.util.regex.Pattern


class ArtistBookingFragment : Fragment(), View.OnClickListener,
    YouTubePlayer.OnInitializedListener {

    private var adapterimage: ImageViewAdapter? = null
    private var largeImge: RelativeLayout?=null
    private var video_url = ""
    private var img_url: String = ""
    private var img_url1: String = ""
    private lateinit var binding: FragmentArtistBookingBinding
    private var artistID = ""
    private lateinit var shared: PrefrenceShared
    private lateinit var ctx: Context
    private lateinit var artistModel: ArtistDataModel
    private var imagePath :String?=null
    private var backpress:MaterialTextView?=null
    private var artistImage = ""
    private var artistName = ""
    private var showimage:ArrayList<String> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        ((ctx as MainActivity)).artistBookingContext(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_booking, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)
        largeImge = view.findViewById(R.id.largeImageView)
        ((ctx as MainActivity)).hideBottomNavigation()
        init(view)

        if (Constants.SHOW_TYPE == "digital") {
            binding.virtualShowLayout.visibility = View.VISIBLE
            binding.inPersonShowLayout.visibility = GONE
        } else {
            binding.inPersonShowLayout.visibility = View.VISIBLE
            binding.virtualShowLayout.visibility = GONE
        }
        binding.aboutText.movementMethod=LinkMovementMethod.getInstance()
        binding.aboutText.setOnTouchListener { view, event ->
            if (view.id == R.id.aboutText) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (event.getAction() and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> view.parent
                        .requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        return view
    }

    private fun init(view: View) {

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(ctx)

        // initializing clicks for views....
        binding.bookBtn.setOnClickListener(this)
        binding.profileImg.setOnClickListener(this)
        binding.seeReviewTxt.setOnClickListener(this)
        binding.youtubePlayIcon.setOnClickListener(this)
        binding.chatStartIcon.setOnClickListener(this)
        binding.instaViewProfileTxt.setOnClickListener(this)
        binding.youtubeViewProfileTxt.setOnClickListener(this)
        binding.close.setOnClickListener(this)
        binding.thumbnail.setOnClickListener(this)
        binding.playIcon.setOnClickListener(this)

        // attaching views....
        backpress = view.findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
//        binding.youtubeView.initialize(resources.getString(R.string.youtube_api_key), this)

        // if artist id not null, get artistbyid api will trigger....
        artistID = arguments?.getString("artistID")!!
        if (artistID != null) {

            getArtistByIdApi()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bookBtn -> {
                val fragment = SelectDateFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameContainer, fragment)
                transaction?.addToBackStack("wayOfBookingFragment")
                transaction?.commit()
            }
            R.id.youtubePlayIcon -> {

                // initializing youtube play video.....
                binding.youtubePlayIcon.visibility = GONE
                binding.thumbnail.visibility = GONE
                val youTubePlayerFragment =
                    childFragmentManager.findFragmentById(R.id.youtubeView) as YouTubePlayerSupportFragment?
                youTubePlayerFragment?.initialize(ctx.resources.getString(R.string.youtube_api_key), this)
            }
            R.id.profileImg -> {
                // open popup window for image zoom....
                /**
                 * @author pardeep.sharma@netscapelabs.com
                 * @param handle the null check
                 */
                if (imagePath!=null && imagePath?.isNotEmpty()!!) {
//                    imageZoomPopup(imagePath!!)
                    largeImge?.visibility=View.VISIBLE
                    binding.imageView.visibility=View.VISIBLE
                    binding.pager.visibility= GONE
                    binding.indicator.visibility= GONE

                    Picasso.get()
                        .load(imagePath)
                        .placeholder(R.drawable.profile_pholder)
                        .into(binding.imageView)

                    binding.toolbarCard.visibility = GONE
                }   else {
                    Toast.makeText(ctx, "Image not found", Toast.LENGTH_SHORT).show()
                }
            }
             R.id.seeReviewTxt -> {
                 val fragment = ReviewFragment()
                 val transaction = fragmentManager?.beginTransaction()
                 transaction?.replace(R.id.frameContainer,fragment)
                 transaction?.addToBackStack("artistFragment")
                 transaction?.commit()
             }
            R.id.backpress -> {

                if (Constants.IS_CHAT_SESSION) {
                    requireActivity().finish()
                }
               else if(Constants.IS_SEARCH_ACTIVITY) {
                    requireActivity().finish()
                }
                else if (Constants.IS_COMING_FROM_BOOKING_DETAIL) {
                    requireActivity().finish()
                }
                else
                    fragmentManager?.popBackStack()

            }
            R.id.chatStartIcon -> {

                Constants.IS_CHAT_SESSION = false
                val intent = Intent(ctx , ChatFragment::class.java)
                intent.putExtra("chatId",artistID)
                intent.putExtra("receiverImage",artistImage)
                intent.putExtra("receiverName",artistName)
                startActivity(intent)
            }
            R.id.instaViewProfileTxt -> {
                if (artistModel.social_link_insta !="" && artistModel.social_link_insta !=null) {

                    val instaurl = artistModel.social_link_insta
                    val uri =
                        Uri.parse("http://instagram.com/_u/${instaurl}")


                    val i =
                        Intent(Intent.ACTION_VIEW, uri)

                    i.setPackage("com.instagram.android")

                    try {
                        startActivity(i)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://www.instagram.com/${instaurl}")
                            )
                        )
                    }


                }
            }
            R.id.youtubeViewProfileTxt -> {
                if (artistModel.social_link_youtube !="" && artistModel.social_link_youtube !=null) {

                    val youtubeimage = artistModel.social_link_youtube
                    var intent: Intent? = null
                    try {
                        intent = Intent(Intent.ACTION_VIEW)
                        intent.setPackage("com.google.android.youtube")
                        intent.data = Uri.parse("https://www.youtube.com/channel/${youtubeimage}")
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://www.youtube.com/channel/${youtubeimage}")
                        startActivity(intent)
                    }
                }
            }
            R.id.close -> {
                largeImge?.visibility= GONE
                binding.toolbarCard.visibility=View.VISIBLE

            }
            R.id.thumbnail -> {
                val intent = Intent(ctx, VideoActivity::class.java)
                intent.putExtra("videourl", video_url)
                startActivity(intent)
            }

            R.id.playIcon -> {
                val intent = Intent(ctx, VideoActivity::class.java)
                intent.putExtra("videourl", video_url)
                startActivity(intent)
            }
        }
    }

//    private fun imageZoomPopup(imgPath: String) {
//
//        val layoutInflater: LayoutInflater =
//            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//        val popUp: View = layoutInflater.inflate(R.layout.image_zoom_layout, null)
//        val popUpWindow = PopupWindow(
//            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
//            ConstraintLayout.LayoutParams.MATCH_PARENT, true
//        )
//        popUpWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            popUpWindow.elevation = 10f
//        }
//        popUpWindow.isTouchable = false
//        popUpWindow.isOutsideTouchable = false
//
//        val imageView: ImageView = popUp.findViewById(R.id.zoomImageView)
//        val close: ImageView = popUp.findViewById(R.id.close)
//        /**
//         * @author pardeep.sharma@netscapelabs.com
//         * @param close the opened Image on touch
//         */
////        val layout = popUp.findViewById<ConstraintLayout>(R.id.zoomlayout)
////        layout?.setOnClickListener {
////            popUpWindow.dismiss()
////        }
//        close.setOnClickListener {
//            popUpWindow.dismiss()
//            binding.toolbarCard.visibility = View.VISIBLE
//        }
//        /**
//         * @author pardeep.sharma@netscapelabs.com
//         * @param handle the null check
//         */
//        if (imagePath!=null&& imagePath?.isNotEmpty()!!) {
//            Picasso.get()
//                .load(imgPath)
//                .resize(4000, 1500)
//                .onlyScaleDown()
//                .into(imageView)
//        }
//
//        // apply zoom in zoom out function here to image view....
////        imageView.setOnClickListener {
////            val pAttacher: PhotoViewAttacher
////            pAttacher = PhotoViewAttacher(imageView)
////            pAttacher.update()
////        }
//
//
//    }

    private fun getArtistByIdApi() {
        binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.artistDetailApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, artistID
        )
            .enqueue(object : Callback<ArtistDetailModel> {
                override fun onResponse(
                    call: Call<ArtistDetailModel>,
                    response: Response<ArtistDetailModel>
                ) {
                    binding.loaderLayout.visibility = GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {


                            /**
                             * @author pardeep.sharma@netscapelabs.com
                             * @param handle the null check
                             */
                            if (response.body()?.data != null) {
                                artistModel = response.body()?.data!!
                                if (artistModel.category_id_details != null) {
                                    for (i in 0 until artistModel.category_id_details.size)
                                        addChip(
                                            artistModel.category_id_details[i].category_name,
                                            binding.chipGroup
                                        )
                                }

                                if(artistModel.shows_image!=null){
                                    showimage = artistModel.shows_image!!
                                    adapterimage = ImageViewAdapter(ctx, showimage)
                                    binding.pager.adapter = adapterimage
                                    binding.indicator.setViewPager(binding.pager)

                                }

                                if (artistModel.shows_video !=null
                                    && !artistModel.shows_video!!.contains(".mp4")) {
                                    val videoUrl = artistModel.shows_video
                                    binding.youtubePlayIcon.visibility = View.VISIBLE
                                    binding.thumbnail.visibility = View.VISIBLE
                                    binding.playIcon.visibility = GONE
                                    extractYoutubeId(videoUrl!!)

                                    binding.noVideoUploadedTxt.visibility = GONE
                                } else if (artistModel.shows_video !=null && artistModel.shows_video!!.contains(".mp4")) {
                                    Picasso.get().load("https://dev.netscapelabs.com/surpriseme/public/uploads/artist/videos/thumbnail/"+artistModel.shows_video_thumbnail!!)
                                        .into(binding.thumbnail)
                                    video_url ="https://dev.netscapelabs.com/surpriseme/public/uploads/artist/videos/"+artistModel.shows_video
                                    binding.noVideoUploadedTxt.visibility = GONE
                                    binding.youtubePlayIcon.visibility = GONE
                                    binding.playIcon.visibility = View.VISIBLE
                                   // binding.thumbnail.visibility = View.GONE
                                } else {
                                    binding.noVideoUploadedTxt.visibility = View.VISIBLE
                                    binding.youtubePlayIcon.visibility = GONE
                                    binding.thumbnail.visibility = GONE
                                    binding.playIcon.visibility = GONE
                                }



                                imagePath =
                                    Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image
                                Picasso.get()
                                    .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image)
                                    .into(binding.profileImg)
                                binding.profileName.text = artistModel.name
                                binding.livePriceText.text = artistModel.converted_currency + " " + DecimalFormat("#.##").format(artistModel.converted_live_price)
                                if (artistModel.converted_digital_price != null)
                                    binding.digitalPriceText.text = artistModel.converted_currency + " " + DecimalFormat("#.##").format(artistModel.converted_digital_price)
                                /**
                                 * @author pardeep.sharma@netscapelabs.com
                                 * @param Set the zoom method to open the images
                                 */
                                if (artistModel.shows_image_1 != null && artistModel.shows_image_1!="") {

                                        binding.img1.visibility = View.VISIBLE
                                        Picasso.get()
                                            .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_1)
                                            .placeholder(R.drawable.profile_pholder)
                                            .into(binding.img1)
                                        binding.img1.setOnClickListener{
                                            largeImge?.visibility=View.VISIBLE
                                            binding.imageView.visibility= GONE
                                            binding.pager.visibility=View.VISIBLE
                                            binding.indicator.visibility=View.VISIBLE
                                            binding.toolbarCard.visibility= GONE
                                            binding.pager.currentItem = 0

//                                            imageZoomPopup(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_1)
                                        }
                                    }
                                if(artistModel.shows_image_2 != null && artistModel.shows_image_2!="") {
                                        binding.img2.visibility = View.VISIBLE
                                        Picasso.get()
                                            .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_2)

                                            .placeholder(R.drawable.profile_pholder)
                                            .into(binding.img2)
                                        binding.img2.setOnClickListener{
                                            largeImge?.visibility=View.VISIBLE
                                            binding.imageView.visibility= GONE
                                            binding.pager.visibility=View.VISIBLE
                                            binding.indicator.visibility=View.VISIBLE
                                            binding.toolbarCard.visibility= GONE
                                            binding.pager.currentItem = 1

//                                            imageZoomPopup(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_2)
                                        }
                                    }
                                if(artistModel.shows_image_3 != null && artistModel.shows_image_3!=""){
                                        binding.img3.visibility = View.VISIBLE
                                        Picasso.get()
                                            .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_3)

                                            .placeholder(R.drawable.profile_pholder)
                                            .into(binding.img3)

                                        binding.img3.setOnClickListener{
                                            largeImge?.visibility=View.VISIBLE
                                            binding.imageView.visibility= GONE
                                            binding.pager.visibility=View.VISIBLE
                                            binding.indicator.visibility=View.VISIBLE
                                            binding.toolbarCard.visibility= GONE
                                            binding.pager.currentItem = 2


//                                            imageZoomPopup(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_3)
                                        }
                                    }
                                if(artistModel.shows_image_4 != null && artistModel.shows_image_4!="") {
                                        binding.img4.visibility = View.VISIBLE
                                        Picasso.get()
                                            .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_4)

                                            .placeholder(R.drawable.profile_pholder)
                                            .into(binding.img4)

                                        binding.img4.setOnClickListener{
                                            largeImge?.visibility=View.VISIBLE
                                            binding.imageView.visibility= GONE
                                            binding.pager.visibility=View.VISIBLE
                                            binding.indicator.visibility=View.VISIBLE
                                            binding.toolbarCard.visibility= GONE
                                            binding.pager.currentItem = 3


//                                            imageZoomPopup(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_4)
                                        }

                                } // End of loop where checking for gallery images.....

                                if (artistModel.shows_image_1 == null && artistModel.shows_image_2 == null
                                    && artistModel.shows_image_3 == null && artistModel.shows_image_4 == null) {
                                    binding.galleryTxt.visibility = GONE
                                    binding.dottedLineImg2.visibility = GONE
                                    binding.horizontalView.visibility = GONE
                                }
//                                binding.mediaText.text = artistModel.social_link_youtube
//                                binding.mediaTextInsta.text = artistModel.social_link_insta

                                if ((artistModel.social_link_insta =="" || artistModel.social_link_insta ==null) &&
                                    (artistModel.social_link_youtube =="" || artistModel.social_link_youtube ==null)) {
                                    binding.socialMediaLayout.visibility = GONE
                                } else if (artistModel.social_link_insta =="" || artistModel.social_link_insta ==null) {
                                    binding.instaLayout.visibility = GONE
                                } else if (artistModel.social_link_youtube == "" || artistModel.social_link_youtube==null) {
                                    binding.youtubeLayout.visibility = GONE
                                }
                                binding.aboutText.text = artistModel.description

                                if (artistModel.rating == 0f || artistModel.rating ==null) {
                                    binding.ratingbar.visibility = GONE
                                    binding.seeReviewTxt.visibility = GONE
                                    binding.brandNewArtistTv.visibility=View.VISIBLE
                                } else {
                                    binding.ratingbar.rating = artistModel.rating
                                }


                                /**
                                 * @author pardeep.sharma@netscapelabs.com
                                 * @param handle the null check
                                 */
                           if (!imagePath.isNullOrEmpty())
                                artistImage = imagePath!!
                                artistName = artistModel.name


                            } else {
                                Toast.makeText(ctx, "No data found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ArtistDetailModel>, t: Throwable) {
                    binding.loaderLayout.visibility = GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })

    }


    private fun addChip(pItem: String, pChipGroup: ChipGroup) {
        val lChip = Chip(ctx)
        lChip.text = pItem
        lChip.isClickable = false
        lChip.textSize = 10f
//        lChip.textEndPadding = 2f
//        lChip.textStartPadding = 2f
// lChip.chipStrokeColor = resources.getColorStateList(R.color.colorAccent)
// lChip.chipStrokeWidth = 1F
        lChip.setTextColor(ContextCompat.getColor(ctx, R.color.black))
//        lChip.compoundDrawablePadding = 2
        lChip.chipBackgroundColor = ContextCompat.getColorStateList(ctx, R.color.greyLighter)
        pChipGroup.addView(lChip as View)

    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        player: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        if (player == null) return
        if (wasRestored) {
            player.play()
        } else {
            player.cueVideo(img_url)
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        result: YouTubeInitializationResult?
    ) {
        Log.d("Youtube Player", "Failed to initialize")

    }

    private fun extractYoutubeId(url: String): String? {
        var videoid: String? = url
        val pattern =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"

        val compiledPattern: Pattern = Pattern.compile(pattern)
        val matcher: Matcher =
            compiledPattern.matcher(url) //url is youtube url for which you want to extract the id.

        if (matcher.find()) {
            videoid = matcher.group()
        }

// val query: String = URL(url).query
// val param = query.split("&".toRegex()).toTypedArray()
// var id: String? = null
// for (row in param) {
// val param1 = row.split("=".toRegex()).toTypedArray()
// if (param1[0] == "v") {
// id = param1[1]
// }
// }
        img_url =videoid!!
        img_url1 =
            "https://img.youtube.com/vi/$videoid/0.jpg"

        Picasso.get().load(img_url1).into(binding.thumbnail)
// binding.upload.visibility = View.GONE
        return videoid
    }

}