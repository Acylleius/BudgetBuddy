package com.budgetbuddy.mobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.mobile.R
import com.budgetbuddy.mobile.data.AuthRepository
import com.budgetbuddy.mobile.model.RegisterRequest

class RegisterActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val firstnameField = findViewById<EditText>(R.id.firstnameField)
        val lastnameField = findViewById<EditText>(R.id.lastnameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val request = RegisterRequest(
                firstname = firstnameField.text.toString().trim(),
                lastname = lastnameField.text.toString().trim(),
                email = emailField.text.toString().trim(),
                password = passwordField.text.toString()
            )

            authRepository.register(
                request = request,
                onSuccess = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
