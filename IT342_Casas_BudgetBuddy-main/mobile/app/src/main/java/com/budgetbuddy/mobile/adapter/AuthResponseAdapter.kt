package com.budgetbuddy.mobile.adapter

import com.budgetbuddy.mobile.model.AuthResponse
import com.budgetbuddy.mobile.model.SessionUser

class AuthResponseAdapter {
    fun adapt(response: AuthResponse): SessionUser? {
        val userData = response.data ?: return null

        return SessionUser(
            email = userData.user.email,
            firstname = userData.user.firstname,
            lastname = userData.user.lastname,
            role = userData.user.role,
            accessToken = userData.accessToken,
            refreshToken = userData.refreshToken
        )
    }
}
