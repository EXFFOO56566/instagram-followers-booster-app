package com.lxquyen.instabooster.data.repository

import com.anjlab.android.iab.v3.PurchaseData
import com.lxquyen.instabooster.data.model.Boost
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.data.model.response.PackageResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Furuichi on 07/07/2022
 */
interface UserRepository {
    fun login(username: String): Flow<User>

    fun checkLogin(): Flow<Unit>

    fun getFeeds(page: Int): Flow<List<User>>

    fun follow(feedId: String): Flow<Unit>

    fun followOurInsta(): Flow<Unit>

    fun boost(boostId: String): Flow<Unit>

    fun getBoost(): Flow<List<Boost>>

    fun getCurrentUser(): Flow<User>

    fun getPackages(): Flow<List<PackageResponse.Data>>

    fun watch(): Flow<Unit>

    fun purchase(purchaseInfo: PurchaseData): Flow<Unit>

    fun updateActivity(): Flow<Unit>
}