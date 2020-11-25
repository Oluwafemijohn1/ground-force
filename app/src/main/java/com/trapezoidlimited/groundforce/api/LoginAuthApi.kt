package com.trapezoidlimited.groundforce.api


import com.trapezoidlimited.groundforce.data.AgentData
import com.trapezoidlimited.groundforce.model.*
import com.trapezoidlimited.groundforce.model.mission.LoginRequest
import retrofit2.http.*

/**
 * Query to make a network call to the login endpoint */

interface LoginAuthApi {

//    @FormUrlEncoded
//    @POST("Auth/login")
//    suspend fun login(
//        @Field("email") email: String,
//        @Field("pin") pin: String
//    ):  GenericResponseClass


    @POST("Auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): GenericResponseClass


    /**
     * Query to make a network call to the forgot endpoint */
    @FormUrlEncoded
    @POST("endpoint")
    suspend fun forgotPassword(
        @Field("email") email: String
    ): ForgotPasswordResponse


    @POST("Auth/verify-phone")
    suspend fun verifyPhone(
        @Body phone: VerifyPhone
    ): GenericResponseClass

    @POST("Auth/confirm-otp")
    suspend fun confirmPhone(
        @Body confirmPhone: ConfirmPhone
    ): GenericResponseClass

    @POST("Auth/register")
    suspend fun registerAgent(
        @Body agent: AgentData
    ): GenericResponseClass

}
