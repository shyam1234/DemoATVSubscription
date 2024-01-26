package com.willow.android.tv.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.squareup.picasso.Picasso


object ImageUtility {
    fun loadImagewithRoundCornersTransformWithCallBack(
        url: String?,
        imagePlaceHolder: Int?,
        holder: ImageView?,
        callback: CustomTarget<Drawable>,
        errorPlaceHolder: Int? = null
    ) {
        holder?.let {
            imagePlaceHolder?.let { it1 ->
                Glide.with(it.context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
                    .placeholder(it1)
                    .error(errorPlaceHolder ?: it1)
                    .into(callback)
            }
        }
    }

    fun loadImagewithRoundCornersTransform(
        url: String?,
        imagePlaceHolder: Int?,
        holder: ImageView?,
        errorPlaceHolder: Int? = null
    ) {
        holder?.let {
            imagePlaceHolder?.let { it1 ->
                Glide.with(it.context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
                    .placeholder(it1)
                    .error(errorPlaceHolder ?: it1)
                    .into(it)
            }
        }
    }

    fun loadImage(
        url: String?,
        imagePlaceHolder: Int?,
        holder: ImageView?,
        errorPlaceHolder: Int? = null
    ) {
        holder?.let {
            imagePlaceHolder?.let { it1 ->
                Glide.with(it.context)
                    .load(url)
                    .placeholder(it1)
                    .error(errorPlaceHolder ?: it1)
                    .into(it)
            }
        }

    }

    fun getImage(uri: Uri) = Picasso.get().load(uri).get()

    fun loadImageInto(imageUrl: String?, view: ImageView) {
        if (!imageUrl.isNullOrBlank())
            Glide.with(view.context)
                .load(imageUrl)
                .into(view)

    }

    fun loadImageDontTransform(imageUrl: String?, view: ImageView?, placeHolder: Int?) {
        view?.let {
            placeHolder?.let { it1 ->
                Glide.with(view.context).load(imageUrl).placeholder(it1)
                    .dontTransform().into(it)
            }
        }
    }

    fun loadImageWithCallback(
        context: Context, url: String?,
        overrideWidth: Int, overrideHeight: Int,
        callback: CustomTarget<Drawable>
    ) {
        Glide.with(context)
            .asDrawable()
            .load(url)
            .override(overrideWidth, overrideHeight)
            .dontTransform()
            .into(callback)

    }
}