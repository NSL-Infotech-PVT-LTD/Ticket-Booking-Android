package com.surpriseme.user.fragments.artistbookingdetail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.FragmentArtistBookingBinding
import com.surpriseme.user.fragments.chatFragment.ChatFragment
import com.surpriseme.user.fragments.reviewfragment.ReviewFragment
import com.surpriseme.user.fragments.wayofbookingfragment.WayOfBookingFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.senab.photoview.PhotoViewAttacher
import java.util.regex.Matcher
import java.util.regex.Pattern


class ArtistBookingFragment : Fragment(), View.OnClickListener,
    YouTubePlayer.OnInitializedListener {

    private var video_url = ""
    private var img_url: String = ""
    private var img_url1: String = ""
    private lateinit var binding: FragmentArtistBookingBinding
    private var artistID = ""
    private lateinit var shared: PrefrenceShared
    private lateinit var ctx: Context
    private lateinit var artistModel: ArtistDataModel
    private var imagePath = ""
    private var backpress:MaterialTextView?=null
    private var artistImage = ""
    private var artistName = ""

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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_artist_booking,
            container,
            false
        )
        val view = binding.root
        shared = PrefrenceShared(ctx)
        ((ctx as MainActivity)).hideBottomNavigation()
        init(view)


//        val thumbnail = "https://img.youtube.com/vi/$img_url/mqdefault.jpg"

//        Picasso.get().load(thumbnail).into(binding.thumbnail)



        return view
    }

    private fun init(view: View) {

        binding.bookBtn.setOnClickListener(this)

        binding.profileImg.setOnClickListener(this)
        binding.seeReviewTxt.setOnClickListener(this)
        binding.youtubePlayIcon.setOnClickListener(this)
        binding.chatStartIcon.setOnClickListener(this)
        backpress = view.findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
//        binding.youtubeView.initialize(resources.getString(R.string.youtube_api_key), this)

        artistID = arguments?.getString("artistID")!!
        if (artistID != null) {

            getArtistByIdApi()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bookBtn -> {
                val fragment = WayOfBookingFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameContainer, fragment)
                transaction?.addToBackStack("wayOfBookingFragment")
                transaction?.commit()
            }
            R.id.youtubePlayIcon -> {

                // initializing youtube play video.....
                binding.youtubePlayIcon.visibility = View.GONE
                binding.thumbnail.visibility = View.GONE
                val youTubePlayerFragment =
                    childFragmentManager.findFragmentById(R.id.youtubeView) as YouTubePlayerSupportFragment?
                youTubePlayerFragment?.initialize(
                    ctx.resources.getString(R.string.youtube_api_key),
                    this
                )


            }
            R.id.profileImg -> {
                // open popup window for image zoom....
                imageZoomPopup(imagePath)
            }
             R.id.seeReviewTxt -> {
                 val fragment = ReviewFragment()
                 val transaction = fragmentManager?.beginTransaction()
                 transaction?.replace(R.id.frameContainer,fragment)
                 transaction?.addToBackStack("artistFragment")
                 transaction?.commit()
             }
            R.id.backpress -> {
                if (Constants.IS_ADDED_TO_BACKSTACK) fragmentManager?.popBackStack() else requireActivity().finish()

            }
            R.id.chatStartIcon -> {

                val intent = Intent(ctx , ChatFragment::class.java)
                intent.putExtra("chatId",artistID)
                intent.putExtra("receiverImage",artistImage)
                intent.putExtra("receiverName",artistName)
                startActivity(intent)
            }
        }
    }

    private fun imageZoomPopup(imgPath: String) {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.image_zoom_layout, null)
        val popUpWindowReport = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindowReport.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popUpWindowReport.elevation = 10f
        }
        popUpWindowReport.isTouchable = true
        popUpWindowReport.isOutsideTouchable = true

        val imageView: ImageView = popUp.findViewById(R.id.zoomImageView)
        Picasso.get()
            .load(imagePath)
            .resize(4000, 1500)
            .onlyScaleDown()
            .into(imageView)

        // apply zoom in zoom out function here to image view....
//        imageView.setOnClickListener {
//            val pAttacher: PhotoViewAttacher
//            pAttacher = PhotoViewAttacher(imageView)
//            pAttacher.update()
//        }


    }

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
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            artistModel = response.body()?.data!!

                            if (artistModel != null) {

                                if (artistModel.category_id_details != null) {
                                    for (i in 0 until artistModel.category_id_details.size)
                                        addChip(
                                            artistModel.category_id_details[i].category_name,
                                            binding.chipGroup
                                        )
                                }
                                if (artistModel.shows_video !=null
                                    && !artistModel.shows_video!!.contains(".mp4")) {
                                    val videoUrl = artistModel.shows_video
                                    binding.youtubePlayIcon.visibility = View.VISIBLE
                                    binding.thumbnail.visibility = View.VISIBLE
                                    extractYoutubeId(videoUrl!!)
                                    binding.noVideoUploadedTxt.visibility = View.GONE
                                } else if (artistModel.shows_video !=null && artistModel.shows_video!!.contains(".mp4")) {
                                    video_url ="https://dev.netscapelabs.com/surpriseme/public/uploads/customer/videos/"+artistModel.shows_video
                                    binding.noVideoUploadedTxt.visibility = View.GONE
                                    binding.youtubePlayIcon.visibility = View.GONE
                                    binding.thumbnail.visibility = View.GONE
                                } else {
                                    binding.noVideoUploadedTxt.visibility = View.VISIBLE
                                    binding.youtubePlayIcon.visibility = View.GONE
                                    binding.thumbnail.visibility = View.GONE
                                }



                                imagePath =
                                    Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image
                                Picasso.get()
                                    .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image)
                                    .resize(4000, 1500)
                                    .onlyScaleDown()
                                    .into(binding.profileImg)
                                binding.profileName.text = artistModel.name
                                binding.livePriceText.text = artistModel.live_price_per_hr.toString()
                                if (artistModel.digital_price_per_hr != null)
                                    binding.digitalPriceText.text =
                                        artistModel.digital_price_per_hr.toString()

                                if (artistModel.shows_image_1 != null) {
                                    binding.img1.visibility = View.VISIBLE
                                    Picasso.get()
                                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_1)
                                        .resize(4000, 1500)
                                        .onlyScaleDown()
                                        .into(binding.img1)
                                } else if (artistModel.shows_image_2 != null) {
                                    binding.img2.visibility = View.VISIBLE
                                    Picasso.get()
                                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_2)
                                        .resize(4000, 1500)
                                        .onlyScaleDown()
                                        .into(binding.img2)
                                } else if (artistModel.shows_image_3 != null) {
                                    binding.img3.visibility = View.VISIBLE
                                    Picasso.get()
                                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_3)
                                        .resize(4000, 1500)
                                        .onlyScaleDown()
                                        .into(binding.img3)
                                } else if (artistModel.shows_image_4 != null) {
                                    binding.img4.visibility = View.VISIBLE
                                    Picasso.get()
                                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.shows_image_4)
                                        .resize(4000, 1500)
                                        .onlyScaleDown()
                                        .into(binding.img4)
                                } // End of loop where checking for gallery images.....
                                binding.mediaText.text = artistModel.social_link_youtube
                                binding.mediaTextInsta.text = artistModel.social_link_insta
                                binding.aboutText.text = artistModel.description

                                artistImage = imagePath
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
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })

    }


    private fun addChip(pItem: String, pChipGroup: ChipGroup) {
        val lChip = Chip(ctx)
        lChip.text = pItem
        lChip.isClickable = false
        lChip.textSize = 10f
        lChip.textEndPadding = 8f
        lChip.textStartPadding = 8f
// lChip.chipStrokeColor = resources.getColorStateList(R.color.colorAccent)
// lChip.chipStrokeWidth = 1F
        lChip.setTextColor(ContextCompat.getColor(ctx, R.color.black))
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

        Picasso.get().load(img_url1)

            .resize(4000, 1500)
            .onlyScaleDown().into(binding.thumbnail)
// binding.upload.visibility = View.GONE
        return videoid
    }

}