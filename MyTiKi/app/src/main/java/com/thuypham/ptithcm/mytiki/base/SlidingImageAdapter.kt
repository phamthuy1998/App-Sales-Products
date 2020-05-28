package com.thuypham.ptithcm.mytiki.base

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.thuypham.ptithcm.mytiki.data.Slide
import java.util.*


class SlidingImageAdapter(
    private val context: Context,
    private val arrAdv: ArrayList<Slide>,
    private var onSlideClick: ((cateId: String?, cateName: String?) -> Unit)?
) : PagerAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return arrAdv.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(
            com.thuypham.ptithcm.mytiki.R.layout.slidingimages_layout_home,
            view,
            false
        )!!

        val imageView = imageLayout
            .findViewById(com.thuypham.ptithcm.mytiki.R.id.image) as ImageView


        Glide.with(context)
            .load(arrAdv[position].image)
            .into(imageView)

        imageView.setOnClickListener {
            onSlideClick?.let { it1 ->
                it1(
                    arrAdv[position].id_category,
                    arrAdv[position].name
                )
            }
        }

        view.addView(imageLayout, 0)

        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

}