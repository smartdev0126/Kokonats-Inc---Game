package com.biiiiit.kokonats.data.api

import com.biiiiit.lib_base.data.LoginUser
import retrofit2.http.*

/**
 * @Author yo_hack
 * @Date 2021.10.23
 * @Description
 **/
interface UserApi {

    /**
     * 获取用户信息
     */
    @GET("/user/info")
    suspend fun getUserInfo(): LoginUser

    @POST("/user/info")
    suspend fun changeUserInfo(@Body map: Map<String, String>): LoginUser

    /**
     * Check if username exists
     */
    @GET("/user/check/name")
    suspend fun checkUsername(@Query("newUsername") username: String)

    /**
     * Register user device
     */
    @GET("/user/device/register")
    suspend fun registerUserDevice(@Query("platform") platform: String, @Query("deviceToken") deviceToken: String)
}