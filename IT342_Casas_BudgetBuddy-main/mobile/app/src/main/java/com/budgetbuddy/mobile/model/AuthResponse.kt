package com.budgetbuddy.mobile.model

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: UserData?,
    val error: ApiError?,
    val timestamp: String
)

data class UserData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class User(
    val id: Long,
    val email: String,
    val firstname: String,
    val lastname: String,
    val role: String
)

data class ApiError(
    val code: String,
    val message: String,
    val details: String?
)
