package com.surpriseme.user.activity.splashwalkthrough

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import com.surpriseme.user.R

class ImageAdapter(val context: Context, val images: ArrayList<Int>,val textList:ArrayList<String>) : PagerAdapter() {

    private var layoutInflater:LayoutInflater? = null
//    private var inflater = LayoutInflater.from(context);

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    override fun getCount(): Int {

        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = layoutInflater!!.inflate(R.layout.image_slider_layout, container, false)
        val imageSlide = view.findViewById<ImageView>(R.id.imageSlide)
        val text = view.findViewById<TextView>(R.id.title_text)

        imageSlide.setImageResource(images[position])
        text.text = textList[position].toString()

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}