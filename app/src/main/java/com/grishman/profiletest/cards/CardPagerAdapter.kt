package com.grishman.profiletest.cards

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.grishman.profiletest.R
import com.grishman.profiletest.model.Card
import com.squareup.picasso.Picasso

import java.util.ArrayList

class CardPagerAdapter : PagerAdapter() {

    private var cards: List<Card>? = null

    init {
        cards = ArrayList()
    }

    fun swapItems(newData: List<Card>?) {
        this.cards = newData
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
        return cards!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.card_item_layout, container, false)
        container.addView(view)
        val imageView = view.findViewById<ImageView>(R.id.image)
        Picasso.get()
                .load(cards!![position].imageUrl)
                .placeholder(R.drawable.ic_mobile_info)
                .into(imageView)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}
