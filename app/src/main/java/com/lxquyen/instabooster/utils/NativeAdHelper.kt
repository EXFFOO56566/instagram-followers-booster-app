package com.lxquyen.instabooster.utils

import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.databinding.ItemNativeAdBinding
import com.lxquyen.instabooster.databinding.ItemNativeAdMediumBinding
import com.ohayo.core.ui.extensions.hide
import com.ohayo.core.ui.extensions.show
import timber.log.Timber

/**
 * Created by Furuichi on 11/07/2022
 */
object NativeAdHelper {
    fun populateNativeAd(view: NativeAdView, nativeAd: NativeAd) {
        Timber.d("populateNativeAd #called()")


        view.findViewById<ImageView>(R.id.ad_app_icon)?.let {
            it.isVisible = nativeAd.icon != null
            it.setImageDrawable(nativeAd.icon?.drawable)
            view.iconView = it
        }

        view.findViewById<TextView>(R.id.ad_headline)?.let {
            it.text = nativeAd.headline
            view.headlineView = it
        }

        view.findViewById<TextView>(R.id.ad_advertiser)?.let {
            it.isVisible = nativeAd.advertiser != null
            it.text = nativeAd.advertiser
            view.advertiserView = it
        }

        view.findViewById<RatingBar>(R.id.ad_stars)?.let {
            it.isVisible = nativeAd.starRating != null
            it.rating = nativeAd.starRating?.toFloat() ?: 0f
            view.starRatingView = it
        }

        view.findViewById<TextView>(R.id.ad_body)?.let {
            it.text = nativeAd.body
            view.bodyView = it
        }

        view.findViewById<TextView>(R.id.ad_cta)?.let {
            it.isVisible = nativeAd.callToAction != null
            it.text = nativeAd.callToAction
            view.callToActionView = it
        }

        view.findViewById<TextView>(R.id.ad_price)?.let {
            it.isVisible = nativeAd.price != null
            it.text = nativeAd.price
            view.priceView = it
        }

        view.findViewById<TextView>(R.id.ad_store)?.let {
            it.isVisible = nativeAd.store != null
            it.text = nativeAd.store
            view.storeView = it
        }

        view.findViewById<MediaView>(R.id.ad_media)?.let {
            it.isVisible = nativeAd.mediaContent != null

            nativeAd.mediaContent?.let { mediaContent ->
                it.setMediaContent(mediaContent)
            }
            it.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            view.mediaView = it
        }

        // Call the NativeAdView's setNativeAd method to register the
        // NativeAdObject.
        view.setNativeAd(nativeAd)
    }
}