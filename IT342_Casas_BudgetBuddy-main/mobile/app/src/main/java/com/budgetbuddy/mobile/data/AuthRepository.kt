package com.budgetbuddy.mobile.data

import com.budgetbuddy.mobile.adapter.AuthResponseAdapter
import com.budgetbuddy.mobile.model.AuthResponse
import com.budgetbuddy.mobile.model.LoginRequest
import com.budgetbuddy.mobile.model.RegisterRequest
import com.budgetbuddy.mobile.model.SessionUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService = RetrofitClient.instance,
    private val adapter: AuthResponseAdapter = AuthResponseAdapter()
) {

    fun login(
        request: LoginRequest,
        onSuccess: (String, SessionUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.login(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    onSuccess(body.message ?: "Login successful", adapter.adapt(body))
                } else {
                    onError(body?.error?.message ?: "Invalid credentials")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }

    fun register(
        request: RegisterRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    onSuccess(body.message ?: "Registration successful")
                } else {
                    onError(body?.error?.message ?: "Registration failed")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }
}
