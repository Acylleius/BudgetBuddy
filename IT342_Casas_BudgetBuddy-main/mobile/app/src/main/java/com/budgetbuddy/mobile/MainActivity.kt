package com.budgetbuddy.mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.mobile.ui.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Directly launch LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Close MainActivity so user can't go back to it
        finish()
    }
}
