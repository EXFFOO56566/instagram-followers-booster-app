package com.lxquyen.instabooster.data.model

import com.lxquyen.instabooster.data.model.response.BoostResponse

/**
 * Created by Furuichi on 9/7/2022
 */
data class Boost(
    val boostId: String,
    val stars: Int,
    val followers: Int
) {
    companion object {
        fun fromApi(boost: List<BoostResponse.Data>): List<Boost> {
            return boost.map {
                return@map fromApi(data = it)
            }
        }

        private fun fromApi(data: BoostResponse.Data): Boost {
            return Boost(
                boostId = data.id,
                stars = data.boostStar ?: 0,
                followers = data.boostOfFollower ?: 0
            )
        }
    }

    lateinit var isUseNow: (Int) -> Boolean
}