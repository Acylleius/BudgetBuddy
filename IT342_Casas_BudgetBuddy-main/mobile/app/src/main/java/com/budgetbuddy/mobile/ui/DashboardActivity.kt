package com.budgetbuddy.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.mobile.R
import com.budgetbuddy.mobile.util.SessionManager

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)
        setContentView(R.layout.activity_dashboard)

        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        SessionManager.addObserver("DashboardActivity") { state ->
            if (!state.isLoggedIn) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                val user = state.user
                val displayName = listOfNotNull(user?.firstname, user?.lastname)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .ifBlank { user?.email ?: "Budget Buddy User" }

                welcomeText.text = "Welcome, $displayName!"
            }
        }

        val state = SessionManager.currentState()
        if (state.isLoggedIn) {
            val user = state.user
            val displayName = listOfNotNull(user?.firstname, user?.lastname)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { user?.email ?: "Budget Buddy User" }

            welcomeText.text = "Welcome, $displayName!"
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        SessionManager.removeObserver("DashboardActivity")
        super.onDestroy()
    }
}
