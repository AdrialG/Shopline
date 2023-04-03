package com.adrialg.ecommercecrocodicapp.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("phone_number") phone: String,
        @Field("password") password: String
    ): String

    //getProfile
    @GET("api/profile")
    suspend fun getProfile() : String

    //list Product
    @GET("api/product")
    suspend fun getProduct() : String

    //get product specific
    @GET("api/product/{id}")
    suspend fun getProductById(
        @Path("id") id : Int
    ) : String

    //update Profile
    @FormUrlEncoded
    @POST("api/profile")
    suspend fun updateProfile(
        @Field("name") name : String,
        @Field("phone_number") phone : String,
    ) : String

    //update Profile w/ Picture
    @Multipart
    @POST("api/profile")
    suspend fun updateProfilePicture(
        @Query("name") name : String,
        @Query("phone_number") phone : String,
        @Part image : MultipartBody.Part?
    ) : String

    //logout
    @POST("api/auth/logout")
    suspend fun logout() : String

}