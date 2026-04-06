package com.budgetbuddy.mobile.model

data class SessionUser(
    val email: String,
    val firstname: String,
    val lastname: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String
)

data class SessionState(
    val isLoggedIn: Boolean,
    val user: SessionUser?
)
