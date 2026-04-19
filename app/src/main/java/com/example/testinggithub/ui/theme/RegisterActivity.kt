package com.example.testinggithub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.testinggithub.data.AppDatabase
import com.example.testinggithub.data.User
import com.example.testinggithub.repo.UserRepository
import com.example.testinggithub.ui.screens.RegisterScreen
import com.example.testinggithub.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getDatabase(this).userDao()
        val repository = UserRepository(dao)
        userViewModel = UserViewModel(repository)

        setContent {
            var username by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            RegisterScreen(
                username = username,
                email = email,
                password = password,
                onUsernameChange = { username = it },
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onRegisterClick = {
                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            val existingUser = repository.login(email.trim(), password.trim())

                            if (existingUser != null) {
                                runOnUiThread {
                                    Toast.makeText(this@RegisterActivity, "User already exists", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val user = User(
                                    username = username.trim(),
                                    email = email.trim(),
                                    password = password.trim()
                                )
                                userViewModel.registerUser(user)

                                runOnUiThread {
                                    Toast.makeText(this@RegisterActivity, "User Registered", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                },
                onBackToLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}