package com.surpriseme.user.fragments.artistbookingdetail

import android.content.Context
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants

class ImageViewAdapter(var context: Context,var list:ArrayList<String>): PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null
  //  private var shared: PreferencesShared? = null


    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view == `object`
    }

    override fun getCount(): Int {

        return list.size
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        shared = PreferencesShared(context)
        val view = layoutInflater!!.inflate(R.layout.item_banner, container, false)
        val projectname: ImageView = view.findViewById(R.id.ic_banner)

        Picasso.get().load(Constants.ImageUrl.BASE_URL+Constants.ImageUrl.ARTIST_IMAGE_URL+list[position]).into(projectname)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as View)
    }
}