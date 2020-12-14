package com.trapezoidlimited.groundforce.repository

import com.trapezoidlimited.groundforce.api.Resource
import com.trapezoidlimited.groundforce.model.request.*
import com.trapezoidlimited.groundforce.model.response.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Part

/**
 * manages api queries to the network endpoints */

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Resource<GenericResponseClass<LoginResponse>>
    suspend fun forgotPassword(email: String): Resource<ForgotPasswordResponse>
    suspend fun verifyPhone(phone: VerifyPhoneRequest): Resource<GenericResponseClass<VerifyPhoneResponse>>
    suspend fun confirmPhone(confirmPhone: ConfirmPhoneRequest): Resource<GenericResponseClass<ConfirmOtpResponse>>
    suspend fun registerAgent(agent: AgentDataRequest): Resource<GenericResponseClass<AgentDataResponse>>
    suspend fun verifyEmail(email: String): Resource<GenericResponseClass<VerifyEmailResponse>>
    suspend fun confirmEmail( email: String, verificationCode: String): Resource<GenericResponseClass<ConfirmEmailResponse>>
    suspend fun getUser(id: String): Resource<GenericResponseClass<UserResponse>>
    suspend fun getUser(token: String, id: String): Resource<GenericResponseClass<UserResponse>>
    suspend fun putUser(user: PutUserRequest): Resource<GenericResponseClass<PutUserResponse>>
    suspend fun verifyAccount(verifyAccountRequest: VerifyAccountRequest): Resource<GenericResponseClass<VerifyAccountResponse>>
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Resource<GenericResponseClass<ChangePasswordResponse>>
    suspend fun verifyLocation(token: String,
                               verifyLocationRequest: VerifyLocationRequest): Resource<GenericResponseClass<VerifyLocationResponse>>
    suspend fun getMissions(token: String, agentId: String, status: String, page: String): Resource<GenericResponseClass<GetMissionResponse>>
    suspend fun updateMissionStatus(token: String, missionId: String, status: String): Resource<GenericResponseClass<UpdateMissionStatusResponse>>
    suspend fun submitMission(
        token: String,
        submitMissionRequest: SubmitMissionRequest): Resource<GenericResponseClass<SubmitMissionResponse>>


}