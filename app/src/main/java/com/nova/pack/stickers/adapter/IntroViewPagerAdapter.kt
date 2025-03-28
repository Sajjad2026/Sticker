package com.nova.pack.stickers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.nova.pack.stickers.R
import com.nova.pack.stickers.model.IntroScreenItem

class IntroViewPagerAdapter(private var mContext: Context?, private var listScreen: List<IntroScreenItem>?) : PagerAdapter() {

    override fun getCount(): Int {
        return listScreen?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater =
            mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.intro_screen_item, null)
        val imgSlide = layoutScreen.findViewById<ImageView>(R.id.image_intro)
        val title = layoutScreen.findViewById<TextView>(R.id.title_intro)
        val description = layoutScreen.findViewById<TextView>(R.id.description_intro)
        title.text = listScreen?.get(position)?.title ?: ""
        description.text = listScreen?.get(position)?.description ?: ""
        Glide.with(mContext!!).load(listScreen?.get(position)?.image).into(imgSlide)
        container.addView(layoutScreen)
        return layoutScreen
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}