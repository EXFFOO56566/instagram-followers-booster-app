package com.lxquyen.instabooster.data.api

import com.lxquyen.instabooster.data.model.response.*
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * Created by Furuichi on 07/07/2022
 */
interface UserService {

    /*
    {
        "username": "justinbieber"
    }
     */
    @POST("user/login")
    suspend fun login(@Body params: MutableMap<String, String>): UserResponse

    /*
    {
        "userId": "62c990db7babb54cc166aee9"
    }
     */
    @POST("user/profile")
    suspend fun profile(@Body params: MutableMap<String, String>): UserResponse

    /*
    {
        "userId": "62c6d4b26b46b976381680e1",
        "page": 0,
        "size": 10
    }
     */
    @POST("user/feeds")
    suspend fun feeds(@Body params: MutableMap<String, Any>): FeedsResponse

    /*
    {
        "userId": "62c6d4b26b46b976381680e1",
        "feedId": "62c6b3bc3687f1752328b3db"
    }
     */
    @POST("user/follow")
    suspend fun follow(@Body params: MutableMap<String, String>): FollowResponse

    @POST("user/followOurInsta")
    suspend fun followOurInsta(@Body params: MutableMap<String, String>)

    /*
    {
        "userId": "62c990db7babb54cc166aee9",
        "boostId": 20
    }
     */
    @POST("user/boost")
    suspend fun boost(@Body params: MutableMap<String, String>)

    /*
    {
        "userId": "62c990db7babb54cc166aee9"
    }
     */
    @POST("user/getBoost")
    suspend fun getBoost(@Body params: MutableMap<String, String>): BoostResponse

    @POST("user/packages")
    suspend fun packages(@Body params: MutableMap<String, String>): PackageResponse

    @POST("user/purchase")
    suspend fun purchase(@Body params: MutableMap<String, Any>)

    @POST("user/watch")
    suspend fun watch(@Body params: MutableMap<String, String>): WatchResponse

    @PUT("user/updateActivity")
    suspend fun updateActivity(@Body params: MutableMap<String, String>)
}