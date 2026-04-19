package com.example.testinggithub.ui

import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.testinggithub.data.AppDatabase
import com.example.testinggithub.repo.UserRepository
import com.example.testinggithub.ui.screens.LoginScreen
import com.example.testinggithub.ui.theme.MenuActivity
import com.example.testinggithub.ui.theme.RegisterActivity
import com.example.testinggithub.util.SessionManager
import com.example.testinggithub.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ... repository initialization ...

        setContent {
            // Use remember so the text doesn't disappear on every keystroke
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            LoginScreen(
                email = email,
                password = password,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLoginClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        lifecycleScope.launch {
                            val user = withContext(Dispatchers.IO) {
                                userViewModel.login(email.trim(), password.trim())
                            }

                            if (user != null) {
                                Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                                val session = SessionManager(this@LoginActivity)

                                // Ensure your User model has an 'id' field
                                session.saveUserId(user.id)

                                // These require MenuActivity and RegisterActivity to exist
                                startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                onJoinNowClick = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            )
        }
    }
}