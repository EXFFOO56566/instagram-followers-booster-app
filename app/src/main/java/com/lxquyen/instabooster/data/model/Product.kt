package com.lxquyen.instabooster.data.model

import com.anjlab.android.iab.v3.SkuDetails
import com.lxquyen.instabooster.data.model.response.PackageResponse

/**
 * Created by Furuichi on 10/7/2022
 */
data class Product(
    val skuDetails: SkuDetails
) {
    fun updateStar(packages: List<PackageResponse.Data>) {
        numberOfStars = packages
            .firstOrNull { it.packageId == skuDetails.productId }
            ?.numberOfStars ?: 0
    }

    val productId: String
        get() = skuDetails.productId

    val star: String?
        get() = "+$numberOfStars⭐️"

    val amount: String
        get() = skuDetails.priceText

    private var numberOfStars: Int = 0
}