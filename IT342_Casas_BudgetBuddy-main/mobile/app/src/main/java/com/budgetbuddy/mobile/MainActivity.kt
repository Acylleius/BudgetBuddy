package com.budgetbuddy.mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.mobile.ui.DashboardActivity
import com.budgetbuddy.mobile.ui.LoginActivity
import com.budgetbuddy.mobile.util.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)

        val destination = if (SessionManager.currentState().isLoggedIn) {
            DashboardActivity::class.java
        } else {
            LoginActivity::class.java
        }

        startActivity(Intent(this, destination))
        finish()
    }
}
