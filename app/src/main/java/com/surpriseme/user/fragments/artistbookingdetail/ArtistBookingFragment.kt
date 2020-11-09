package com.surpriseme.user.fragments.artistbookingdetail

import android.content.Context
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
import com.surpriseme.user.fragments.reviewfragment.ReviewFragment
import com.surpriseme.user.fragments.wayofbookingfragment.WayOfBookingFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.chat_list_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.senab.photoview.PhotoViewAttacher


class ArtistBookingFragment : Fragment(), View.OnClickListener,
    YouTubePlayer.OnInitializedListener {

    private lateinit var binding: FragmentArtistBookingBinding
    private var artistID = ""
    private lateinit var shared: PrefrenceShared
    private lateinit var ctx: Context
    private lateinit var artistModel: ArtistDataModel
    var imagePath = ""

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
        init()

        val videoKey = "bSMZknDI6bg"
        val thumbnail = "https://img.youtube.com/vi/$videoKey/mqdefault.jpg"

        Picasso.get().load(thumbnail).into(binding.thumbnail)


        return view
    }

    private fun init() {

        binding.bookBtn.setOnClickListener(this)
        binding.youtubePlayIcon.setOnClickListener(this)
        binding.profileImg.setOnClickListener(this)
        binding.seeReviewTxt.setOnClickListener(this)
        binding.backpress.setOnClickListener(this)
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
                fragmentManager?.popBackStack()
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
                                            artistModel.category_id_details[i],
                                            binding.chipGroup
                                        )
                                }

                                imagePath =
                                    Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image
                                Picasso.get()
                                    .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image)
                                    .resize(4000, 1500)
                                    .onlyScaleDown()
                                    .into(binding.profileImg)
                                binding.profileName.text = artistModel.name
                                binding.livePriceText.text = artistModel.live_price_per_hr
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
        lChip.textSize = 13F
// lChip.chipStrokeColor = resources.getColorStateList(R.color.colorAccent)
// lChip.chipStrokeWidth = 1F
        lChip.setEnsureMinTouchTargetSize(false)
        lChip.setTextColor(ContextCompat.getColor(ctx, R.color.white))
        lChip.chipBackgroundColor = ContextCompat.getColorStateList(ctx, R.color.grey_color)
// following lines are for the demo
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
            player.cueVideo("W4hTJybfU7s")
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        result: YouTubeInitializationResult?
    ) {
        Log.d("Youtube Player", "Failed to initialize")

    }

}