package com.grishman.profiletest.binding

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.grishman.profiletest.R
import com.squareup.picasso.Picasso


/**
 * Binding adapters
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, imageUrl: String?) {
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(view)
    }
}