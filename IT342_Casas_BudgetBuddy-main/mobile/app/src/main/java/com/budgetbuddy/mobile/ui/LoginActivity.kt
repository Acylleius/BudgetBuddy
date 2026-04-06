package com.budgetbuddy.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.mobile.R
import com.budgetbuddy.mobile.data.AuthRepository
import com.budgetbuddy.mobile.model.LoginRequest
import com.budgetbuddy.mobile.util.SessionManager

class LoginActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)

        SessionManager.addObserver("LoginActivity") { state ->
            if (state.isLoggedIn) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }

        loginButton.setOnClickListener {
            val request = LoginRequest(
                emailField.text.toString().trim(),
                passwordField.text.toString()
            )

            authRepository.login(
                request = request,
                onSuccess = { message, sessionUser ->
                    if (sessionUser != null) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        SessionManager.saveSession(sessionUser)
                    } else {
                        Toast.makeText(this, "Login succeeded but no user data was returned", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroy() {
        SessionManager.removeObserver("LoginActivity")
        super.onDestroy()
    }
}
