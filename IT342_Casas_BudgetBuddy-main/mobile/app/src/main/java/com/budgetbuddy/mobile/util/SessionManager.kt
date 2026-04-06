package com.budgetbuddy.mobile.util

import android.content.Context
import android.content.SharedPreferences
import com.budgetbuddy.mobile.model.SessionState
import com.budgetbuddy.mobile.model.SessionUser

object SessionManager {
    private const val PREF_NAME = "budgetbuddy_session"
    private const val KEY_EMAIL = "email"
    private const val KEY_FIRSTNAME = "firstname"
    private const val KEY_LASTNAME = "lastname"
    private const val KEY_ROLE = "role"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    private val observers = mutableMapOf<String, (SessionState) -> Unit>()
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        if (!::preferences.isInitialized) {
            preferences = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    fun addObserver(key: String, observer: (SessionState) -> Unit) {
        observers[key] = observer
    }

    fun removeObserver(key: String) {
        observers.remove(key)
    }

    fun saveSession(user: SessionUser) {
        preferences.edit()
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_FIRSTNAME, user.firstname)
            .putString(KEY_LASTNAME, user.lastname)
            .putString(KEY_ROLE, user.role)
            .putString(KEY_ACCESS_TOKEN, user.accessToken)
            .putString(KEY_REFRESH_TOKEN, user.refreshToken)
            .apply()

        notifyObservers()
    }

    fun clearSession() {
        preferences.edit().clear().apply()
        notifyObservers()
    }

    fun currentState(): SessionState {
        val email = preferences.getString(KEY_EMAIL, null)
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null)
        val isLoggedIn = !email.isNullOrBlank() && !accessToken.isNullOrBlank()

        val user = if (isLoggedIn) {
            SessionUser(
                email = email.orEmpty(),
                firstname = preferences.getString(KEY_FIRSTNAME, "").orEmpty(),
                lastname = preferences.getString(KEY_LASTNAME, "").orEmpty(),
                role = preferences.getString(KEY_ROLE, "").orEmpty(),
                accessToken = accessToken.orEmpty(),
                refreshToken = preferences.getString(KEY_REFRESH_TOKEN, "").orEmpty()
            )
        } else {
            null
        }

        return SessionState(isLoggedIn, user)
    }

    private fun notifyObservers() {
        val state = currentState()
        observers.values.forEach { observer -> observer(state) }
    }
}
