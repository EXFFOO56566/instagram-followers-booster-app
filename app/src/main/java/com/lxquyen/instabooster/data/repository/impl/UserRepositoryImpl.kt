package com.lxquyen.instabooster.data.repository.impl

import com.anjlab.android.iab.v3.PurchaseData
import com.lxquyen.instabooster.data.api.UserService
import com.lxquyen.instabooster.data.model.Boost
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.data.model.response.PackageResponse
import com.lxquyen.instabooster.data.model.response.mapToBoosts
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.utils.PrefKeys
import com.ohayo.core.di.IoDispatcher
import com.ohayo.core.helper.preferences.DataStoreManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

/**
 * Created by Furuichi on 07/07/2022
 */
class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val dataStore: DataStoreManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UserRepository {

    private val currentUserId: String
        get() = runBlocking { dataStore.get(PrefKeys.USER_ID, "") ?: "" }

    private val _currentUserFlow = MutableStateFlow<User?>(value = null)

    override fun login(username: String): Flow<User> {
        return flow {
            val params = mutableMapOf(
                "username" to username
            )
            val user = userService.login(params).user
                ?: throw NullPointerException("Oops...something wrong")
            dataStore.set(PrefKeys.USER_ID, user.id)
            _currentUserFlow.emit(user)
            emit(user)
        }.flowOn(dispatcher)
    }

    override fun checkLogin(): Flow<Unit> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId
            )
            val user = userService.profile(params).user
                ?: throw NullPointerException("Oops...something wrong")
            _currentUserFlow.emit(user)
            emit(Unit)
        }.flowOn(dispatcher)
    }

    override fun getFeeds(page: Int): Flow<List<User>> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId,
                "page" to page as Any
            )
            val users = userService.feeds(params).users
                ?: throw NullPointerException("Oops...something wrong")
            emit(users)
        }.flowOn(dispatcher)
    }

    override fun follow(feedId: String): Flow<Unit> {
        return flow {
            dataStore.remove(PrefKeys.FEED_ID_TMP)
            val params = mutableMapOf(
                "userId" to currentUserId,
                "feedId" to feedId
            )
            userService.follow(params)

            val user = _currentUserFlow.value
            val newUser = user?.copy(stars = user.stars?.plus(1))
            _currentUserFlow.emit(newUser)
            emit(Unit)
        }.flowOn(dispatcher)
    }

    override fun followOurInsta(): Flow<Unit> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId,
                "username" to "witworkapp"
            )
            userService.followOurInsta(params)
            val user = _currentUserFlow.value
            val newUser = user?.copy(stars = user.stars?.plus(2))
            _currentUserFlow.emit(newUser)
            emit(Unit)
        }.flowOn(dispatcher)
    }

    override fun boost(boostId: String): Flow<Unit> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId,
                "boostId" to boostId
            )
            userService.boost(params)
            fetchUserSafety()
            emit(Unit)
        }.flowOn(dispatcher)
    }

    override fun getBoost(): Flow<List<Boost>> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId
            )
            val boosts = userService.getBoost(params)
                .data?.mapToBoosts()
                ?: listOf()
            emit(boosts)
        }.flowOn(dispatcher)
    }

    override fun getCurrentUser(): Flow<User> {
        return _currentUserFlow.filterNotNull()
    }

    override fun getPackages(): Flow<List<PackageResponse.Data>> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId,
                "os" to "android"
            )
            val data = userService.packages(params)
                .data?.filterNotNull() ?: listOf()
            emit(data)
        }.flowOn(dispatcher)
    }

    override fun watch(): Flow<Unit> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId
            )
            val response = userService.watch(params)
            val user = _currentUserFlow.value
            val newUser = user?.copy(stars = user.stars?.minus(response.data?.reward ?: 0))
            _currentUserFlow.emit(newUser)
            emit(Unit)
        }.flowOn(dispatcher)
    }

    override fun purchase(purchaseInfo: PurchaseData): Flow<Unit> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId,
                "orderId" to purchaseInfo.orderId,
                "packageName" to purchaseInfo.packageName,
                "productId" to purchaseInfo.productId,
                "purchaseTime" to Date().time,
                "purchaseState" to 0,
                "purchaseToken" to purchaseInfo.purchaseToken,
                "quantity" to 1,
                "acknowledged" to false as Any,
            )
            userService.purchase(params)

            fetchUserSafety()

            emit(Unit)
        }.flowOn(dispatcher)
    }

    override fun updateActivity(): Flow<Unit> {
        return flow {
            val params = mutableMapOf(
                "userId" to currentUserId
            )
            userService.updateActivity(params)
            emit(Unit)
        }.flowOn(dispatcher)
    }

    private suspend fun fetchUserSafety() {
        try {
            val params = mutableMapOf(
                "userId" to currentUserId
            )
            userService.profile(params).user?.let {
                _currentUserFlow.emit(it)
            }
        } catch (ex: Exception) {
        }
    }
}