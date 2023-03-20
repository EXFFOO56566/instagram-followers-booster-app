package com.lxquyen.instabooster.data.mock

import com.lxquyen.instabooster.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

/**
 * Created by Furuichi on 9/7/2022
 */
class MockInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        if (BuildConfig.DEBUG.not()) {
            //just to be on safe side.
            throw IllegalAccessError(
                "MockInterceptor is only meant for Testing Purposes and " +
                        "bound to be used only with DEBUG mode"
            )
        }

        val uri = chain.request().url.toUri().toString()

        val responseString = when {
            uri.endsWith("login") -> getLoginResponseJson
            uri.endsWith("feeds") -> getFeedResponseJson
            uri.endsWith("follow") -> getFollowResponseJson
            uri.endsWith("boost") -> getFollowResponseJson
            uri.endsWith("profile") -> getLoginResponseJson
            uri.endsWith("packages") -> getPackagesResponseJson
            else -> ""
        }

        return runBlocking {
            delay(500)
            Response.Builder()
                .request(chain.request())
                .code(200)
                .body(
                    responseString.toResponseBody(
                        "application/json".toMediaTypeOrNull()
                    )
                )
                .protocol(Protocol.HTTP_2)
                .message("Success")
                .addHeader("content-type", "application/json")
                .build()
        }
    }
}

private const val getLoginResponseJson = """
    {
    "data": {
        "isPrivate": false,
        "isVerified": true,
        "stars": 10,
        "followings": 714,
        "followers": 1094,
        "createdAt": "2022-07-07T12:42:26.582Z",
        "_id": "62c6d4b26b46b976381680e1",
        "username": "justinbieber",
        "fullName": "Justin Bieber",
        "profilePicUrl": "https://scontent.cdninstagram.com/v/t51.2885-19/271177452_149300624107486_238439661515940835_n.jpg?stp=dst-jpg_s150x150&_nc_ht=scontent.cdninstagram.com&_nc_cat=1&_nc_ohc=mkny-7LW-X0AX-9MgH8&edm=AHG7ALcBAAAA&ccb=7-5&oh=00_AT9TrcS_UNJjhqVaZucOnnuARawp6OBPX-cktM7SqXHrog&oe=62CCF15F&_nc_sid=5cbaad",
        "pk": "6860189",
        "__v": 0,
        "id": "62c6d4b26b46b976381680e1"
    }
}
"""

private const val getFeedResponseJson = """
    {
    "data": [
        {
            "isPrivate": false,
            "isVerified": true,
            "active": true,
            "boostStar": 75,
            "createdAt": "2022-07-07T10:21:16.837Z",
            "_id": "62c6b3bc3687f1752328b3db",
            "username": "espn",
            "fullName": "ESPN",
            "profilePicUrl": "https://scontent.cdninstagram.com/v/t51.2885-19/68840121_1392487614222244_1856158000587210752_n.jpg?stp=dst-jpg_s150x150&_nc_ht=scontent.cdninstagram.com&_nc_cat=1&_nc_ohc=00zupwnSmtsAX-7Ur7f&edm=ALB854YBAAAA&ccb=7-5&oh=00_AT__pKJZAyLpHzHq6ILrwgcSoWZww5lpCOYfkXfdhbU4UA&oe=62CE7832&_nc_sid=04cb80",
            "pk": "1320207",
            "__v": 0,
            "id": "62c6b3bc3687f1752328b3db"
        },
        {
            "isPrivate": false,
            "isVerified": true,
            "active": true,
            "boostStar": 20,
            "createdAt": "2022-07-07T10:12:30.134Z",
            "_id": "62c6b339ebed7f74d3033ba3",
            "username": "justinbieber",
            "fullName": "Justin Bieber",
            "profilePicUrl": "https://scontent.cdninstagram.com/v/t51.2885-19/271177452_149300624107486_238439661515940835_n.jpg?stp=dst-jpg_s150x150&_nc_ht=scontent.cdninstagram.com&_nc_cat=1&_nc_ohc=mkny-7LW-X0AX-9MgH8&edm=AHG7ALcBAAAA&ccb=7-5&oh=00_AT9TrcS_UNJjhqVaZucOnnuARawp6OBPX-cktM7SqXHrog&oe=62CCF15F&_nc_sid=5cbaad",
            "pk": "6860189",
            "__v": 0,
            "id": "62c6b339ebed7f74d3033ba3"
        },
        {
            "isPrivate": false,
            "isVerified": true,
            "active": true,
            "boostStar": 12,
            "createdAt": "2022-07-07T09:20:09.374Z",
            "_id": "62c6a54dda3ef6727da3dec3",
            "username": "ngoctrinh89",
            "fullName": "Ngoc Trinh",
            "profilePicUrl": "https://scontent.cdninstagram.com/v/t51.2885-19/290090305_418086830331787_7686408759661925670_n.jpg?stp=dst-jpg_s150x150&_nc_ht=scontent.cdninstagram.com&_nc_cat=1&_nc_ohc=7PPe_T1rlaQAX9Rt2Ig&edm=AHG7ALcBAAAA&ccb=7-5&oh=00_AT-MQF9Jlhz5gYItBuC2SO6TNIWvsZ1eYLtW7PBBMS0kqQ&oe=62CE49E3&_nc_sid=5cbaad",
            "pk": "1526791424",
            "__v": 0,
            "id": "62c6a54dda3ef6727da3dec3"
        }
    ]
}
"""

private const val getFollowResponseJson = """
    {
    "data": {
        "createdAt": "2022-07-07T10:55:04.489Z",
        "_id": "62c6dbb16b46b97638168457",
        "userId": "62c6d4b26b46b976381680e1",
        "feedId": "62c6b3bc3687f1752328b3db",
        "reward": 5,
        "__v": 0,
        "id": "62c6dbb16b46b97638168457"
    }
}
"""

private const val getPackagesResponseJson = """
    {
    "data": [
        {
            "numberOfStars": 0,
            "packsBought": 0,
            "createdAt": "2022-07-13T06:05:15.606Z",
            "_id": "62ce635664ed967812633039",
            "packageName": "Package 1",
            "packageId": "app.witwork.instagram.pack50.Android",
            "packagePricing": 10,
            "packagePlatform": "android",
            "__v": 0,
            "id": "62ce635664ed967812633039"
        },
        {
            "numberOfStars": 0,
            "packsBought": 0,
            "createdAt": "2022-07-13T06:05:15.606Z",
            "_id": "62ce635664ed96781263303a",
            "packageName": "Package 2",
            "packageId": "app.witwork.instagram.pack350.Android",
            "packagePricing": 20,
            "packagePlatform": "android",
            "__v": 0,
            "id": "62ce635664ed96781263303a"
        },
        {
            "numberOfStars": 0,
            "packsBought": 0,
            "createdAt": "2022-07-13T06:05:15.606Z",
            "_id": "62ce635664ed96781263303b",
            "packageName": "Package 3",
            "packageId": "app.witwork.instagram.pack1000.Android",
            "packagePricing": 30,
            "packagePlatform": "android",
            "__v": 0,
            "id": "62ce635664ed96781263303b"
        }
    ]
}
"""